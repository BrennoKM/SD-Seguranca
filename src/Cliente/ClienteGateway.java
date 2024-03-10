package Cliente;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Cifra.ChavePubModulo;
import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorGateway;

public class ClienteGateway {
	private String nome;
	private ServidorGateway stubGateway;
	private Cifrador cifrador;
	private Conta contaLogada = null;
	private Scanner in = new Scanner(System.in);

	public ClienteGateway(String nome) throws Exception {
		this.nome = nome;
		abrirServidorGateway();
	}

	private static void config() {
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
	}

	private void abrirServidorGateway() throws Exception {
		int porta = 50005;
		config();
//		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço: ");
		// String host = entrada.nextLine();
		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubGateway = (ServidorGateway) registro.lookup("ServidorGateway");

				ChavePubModulo chavePubModulo = this.stubGateway.receberChavePubModulo(this.nome);
				System.out.println(chavePubModulo);
				String chaveAES = Cifrador.gerarStringAleatoria(16);
				cifrador = new Cifrador(chaveAES);
				System.out.println("Minha chaveAES: " + chaveAES);
				chaveAES = cifrador.criptografarRSA(chaveAES, chavePubModulo);
				System.out.println("Minha chaveAES criptografada: " + chaveAES);
				this.stubGateway.enviarChaveAES(this.nome, chaveAES);

				conectou = true;
//				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de gateway. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}
		fazerLogin();
		if(contaLogada.getTipoConta().equals("usuario")) {
			usuarioLogado();
		}
		if(contaLogada.getTipoConta().equals("funcionario")) {
			funcionarioLogado();
		}
		
	}

	private void usuarioLogado() {
		new Usuario(nome, stubGateway, cifrador, contaLogada);
		
	}
	
	private void funcionarioLogado() {
		new Funcionario(nome, stubGateway, cifrador, contaLogada);
		
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
					} catch (NumberFormatException | NoSuchElementException e) {
						System.err.println("Formato ou valor invalido");
//						Thread.sleep(2000);
					}
				} while (opcaoInt < 1 || opcaoInt > 2);
				switch (opcaoInt) {
				case 1:
					conta = construirMensagemLogin("login", in, cifrador);
					contaLogada = stubGateway.fazerLogin(this.nome, conta);
					break;
				case 2:
					conta = construirMensagemLogin("cadastro", in, cifrador);
					contaLogada = stubGateway.fazerCadastro(this.nome, conta);
					break;
				}
				if(contaLogada != null) {
					contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), contaLogada);
					System.out.println("Logado com sucesso! "+contaLogada);
					login = true;
				} else {
					System.out.println("Falha no login! Usuario ou senha incorretos!");
				}
			}
			Thread.sleep(2000);
		}
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
		new ClienteGateway("Kevyn");
	}

}
