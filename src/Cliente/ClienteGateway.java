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
	private String nome;
	private ServidorGateway stubGateway;
	private Cifrador cifrador;
	private TokenInfo tokenServidor;
	private Conta contaLogada = null;
	private Scanner in = new Scanner(System.in);

	public ClienteGateway(String nome, String host) throws Exception {
		this.nome = nome;
		abrirServidorGateway(host);
	}

	private void config() {
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
	}

	private void abrirServidorGateway(String host) throws Exception {
		int porta = 50005;
		config();
//		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço de gateway: ");
		// String host = entrada.nextLine();
//		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubGateway = (ServidorGateway) registro.lookup("ServidorGateway");
				tokenServidor = new TokenInfo();
				cifrador = new Cifrador();
				ChavesModulo chaveModuloServidor = this.stubGateway.receberChavePubModulo(this.nome);

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

				this.stubGateway.enviarChaveAES(this.nome, chaveAES);
				this.stubGateway.enviarChaveHmac(this.nome, chaveHmac);
				chaveModulo.setChavePri(null);
				this.stubGateway.enviarChavePubModulo(this.nome, chaveModulo);
				chaveModulo.setChavePri(chavePri);

				System.out.println();

				conectou = true;
//				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de gateway. Tentando novamente em 4 segundos");
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
		return new Usuario(nome, stubGateway, cifrador, contaLogada, "Você logou como um usuário comum", tokenServidor)
				.iniciar();

	}

	private boolean funcionarioLogado() throws RemoteException, Exception {
		return new Funcionario(nome, stubGateway, cifrador, contaLogada, "Você logou como funcionário", tokenServidor)
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
					contaLogada = stubGateway.fazerLogin(this.nome, conta, hashAssinado);
					break;
				case 2:
					conta = construirMensagemLogin("cadastro", in, cifrador);
					hashAssinado = assinarMsg(conta.toString());
					contaLogada = stubGateway.fazerCadastro(this.nome, conta, hashAssinado);
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
		new ClienteGateway("Kevyn", "localhost");
	}

}
