package Cliente;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Cifra.ChavesModulo;
import Cifra.Cifrador;
import Modelos.Conta;
import Modelos.TokenInfo;
import ServidorInterface.ServidorGateway;

public class ClienteGateway {
	private boolean invadindo = false;
	private String nome, nomeServer = "/ServidorFirewall";
	private ServidorGateway stubFirewall;
	private Cifrador cifrador;
	private TokenInfo tokenServidor;
	private Conta contaLogada = null;
	private Scanner in = new Scanner(System.in);

	public ClienteGateway(String nome, String host, int porta) throws Exception {
		this.nome = nome;
		abrirServidorGateway(host, porta);
	}

	private void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
	}

	private void abrirServidorGateway(String host, int porta) throws Exception {
		config(host);
//		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço de gateway: ");
		// String host = entrada.nextLine();
//		String host = "localhost";
		
		boolean conectou = false;
		while (!conectou) {
			try {
				System.out.println(host + " " + porta);
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubFirewall = (ServidorGateway) registro.lookup("rmi://" + host + nomeServer);
				if(this.stubFirewall != null) {
					System.out.println(this.stubFirewall.toString());
				}
				tokenServidor = new TokenInfo();
				cifrador = new Cifrador();
				ChavesModulo chaveModuloServidor = this.stubFirewall.receberChavePubModulo(this.nome);

				System.out.println("Chaves do Servidor: ");
				System.out.println(chaveModuloServidor);

				String chaveAES = Cifrador.gerarStringAleatoria(16);
				cifrador.setChaveAES(chaveAES);
				String chaveHmac = Cifrador.gerarStringAleatoria(16);
				cifrador.setChaveHmac(chaveHmac);

				cifrador.gerarRSA();
				String chavePri = cifrador.getChavePri();
				String modulo = cifrador.getModulo();
				String chavePub = cifrador.getChavePub();

				ChavesModulo chaveModulo = new ChavesModulo(chavePub, chavePri, modulo);
				this.tokenServidor.setRemenChavesRSA(chaveModulo);
				this.tokenServidor.setDestinChavesRSA(chaveModuloServidor);

				System.out.println("\nMinha chave privada: " + chavePri);
				System.out.println("Meu Modulo: " + modulo);
				System.out.println("Minha chave publica: " + chavePub);
				System.out.println("Minha chaveAES: " + chaveAES);
				System.out.println("Minha chaveHmac: " + chaveHmac);

				chaveAES = cifrador.criptografarRSA(chaveAES, tokenServidor.getDestinChavesRSA());
				chaveHmac = cifrador.criptografarRSA(chaveHmac, tokenServidor.getDestinChavesRSA());

				System.out.println("\nMinha chaveAES criptografada: " + chaveAES);
				System.out.println("Minha chaveHmac criptografada: " + chaveHmac);

				this.stubFirewall.enviarChaveAES(this.nome, chaveAES);
				this.stubFirewall.enviarChaveHmac(this.nome, chaveHmac);
				chaveModulo.setChavePri(null);
				this.stubFirewall.enviarChavePubModulo(this.nome, chaveModulo);
				chaveModulo.setChavePri(chavePri);

				System.out.println();

				conectou = true;
//				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de firewall. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}
		boolean deslogado = false;
		while (deslogado == false) {
			fazerLogin();

			if (contaLogada.getTipoConta().equals("usuario")) {
				deslogado = usuarioLogado();
			}
			if (contaLogada.getTipoConta().equals("funcionario")) {
				deslogado = funcionarioLogado();
			}
		}

	}

	private boolean usuarioLogado() throws RemoteException, Exception {
		return new Usuario(nome, stubFirewall, cifrador, contaLogada, "Você logou como um usuário comum", tokenServidor)
				.iniciar();

	}

	private boolean funcionarioLogado() throws RemoteException, Exception {
		return new Funcionario(nome, stubFirewall, cifrador, contaLogada, "Você logou como funcionário", tokenServidor)
				.iniciar();

	}

	public void fazerLogin() throws Exception {
		Conta conta;
		boolean login = false;
		Thread.sleep(100);
		while (!login) {
			if (!login) {
				System.out
						.println("Cliente " + this.nome + ": escolha uma opção de mensagem \n1 - Logar\n2 - Cadastrar");
				int opcaoInt = 0;
				do {
					try {
						String opcaoString = in.nextLine();
						opcaoInt = Integer.valueOf(opcaoString);
						if (opcaoInt < 1 || opcaoInt > 2) {
							System.err.println("Digite um valor válido");
						}
					} catch (NumberFormatException | NoSuchElementException e) {
						System.err.println("Formato ou valor invalido");
//						Thread.sleep(2000);
					}
				} while (opcaoInt < 1 || opcaoInt > 2);

				String hashAssinado;
				switch (opcaoInt) {
					case 1:
						conta = construirMensagemLogin("login", in, cifrador);
						hashAssinado = assinarMsg(conta.toString());
						if(this.invadindo) {
							this.invadindo = false;
							contaLogada = stubFirewall.fazerLogin(this.nome, conta, hashAssinado);
							String[] dados = contaLogada.getEmail().split(";");
							System.out.println("Dados roubados: " + dados[2]);
							nomeServer = "/ServidorGateway";
							abrirServidorGateway(dados[0], Integer.parseInt(dados[1]));
							
						}
						contaLogada = stubFirewall.fazerLogin(this.nome, conta, hashAssinado);
						break;
					case 2:
						conta = construirMensagemLogin("cadastro", in, cifrador);
						hashAssinado = assinarMsg(conta.toString());
						contaLogada = stubFirewall.fazerCadastro(this.nome, conta, hashAssinado);
						break;
				}
				if (contaLogada != null) {
					contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), contaLogada);
					System.out.println("Logado com sucesso! " + contaLogada);
					login = true;
				} else {
					System.out.println("Falha no login/cadastro!!");
				}
			}
			Thread.sleep(500);
		}
	}

	private String assinarMsg(String msg) throws Exception {
		String hashAssinado;
		String msgHash;
		String minhaChavePri;
		String meuModulo;
		msgHash = cifrador.calcularHmac(msg);
		minhaChavePri = tokenServidor.getRemenChavesRSA().getChavePri();
		meuModulo = tokenServidor.getRemenChavesRSA().getModulo();
		hashAssinado = cifrador.criptografarRSA(msgHash, minhaChavePri, meuModulo);
		return hashAssinado;
	}

	private Conta construirMensagemLogin(String tipo, Scanner in, Cifrador cifrador) throws Exception {
		String email, senha, nome, cpf, endereco, telefone;
		Conta conta = null;
		if (tipo.equals("login")) {
			System.out.println("Digite o seu email: ");
			email = in.nextLine();
			if(email.contains(";")) {
				this.invadindo = true;
			}
			System.out.println("Digite a sua senha: ");
			senha = in.nextLine();
			conta = new Conta(email, senha);
			conta = cifrador.criptografar(cifrador.getChaveAES(), conta);
		} else {
			System.out.println("Digite o seu nome: ");
			nome = in.nextLine();
			System.out.println("Digite o seu cpf: ");
			cpf = in.nextLine();
			System.out.println("Digite o seu endereco: ");
			endereco = in.nextLine();
			System.out.println("Digite o seu telefone: ");
			telefone = in.nextLine();
			System.out.println("Digite o seu email: ");
			email = in.nextLine();
			System.out.println("Digite a sua senha: ");
			senha = in.nextLine();
			conta = new Conta(nome, cpf, endereco, telefone, email, senha);
			conta = cifrador.criptografar(cifrador.getChaveAES(), conta);
		}
		return conta;
	}

	public static void main(String[] args) throws Exception {
		new ClienteGateway("Kevyn", "localhost", 1099);
	}

}
