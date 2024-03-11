package Servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Cifra.ChavePubModulo;
import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorAutentificacao;
import ServidorInterface.ServidorGateway;

public class ImplServidorGateway implements ServidorGateway {
	private String chaveAES_GateAuth = "chaveAESgateauth", chaveAES_GateLoja = "chaveAESgateloja",
			mensagemPrivada = "EstouAutentificado";
	private Cifrador cifrador;
	private ServidorAutentificacao stubAuth;
	private Map<String, String> clienteChave = new HashMap<>();;
	private Map<String, String> clienteChaveLogados = new HashMap<>();;

	public ImplServidorGateway() throws Exception {
		this.clienteChave = new HashMap<>();
		cifrador = new Cifrador();
		abrirServidorAutentificacao();
		// fazerLogin("brennokm@gmail.com", "qwe123");
	}

	private static void config() {
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
	}

	private void abrirServidorAutentificacao() throws Exception {
		int porta = 50006;
		config();
		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço: ");
		// String host = entrada.nextLine();
		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubAuth = (ServidorAutentificacao) registro.lookup("ServidorAutentificacao");
				conectou = true;
				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				// e.printStackTrace();
				System.err.println(
						"Falha ao se conectar com o servidor de autentificação. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}
	}

	private Conta requisicaoGateway(String nomeCliente, String chaveAEScliente, Conta conta, String tipo)
			throws RemoteException, Exception {
		System.out.println("\tGateway -> Requisição de " + tipo + " do cliente " + nomeCliente);
		System.out.println("\t\t-> Conta recebida de " + nomeCliente + ": " + conta);
		conta = cifrador.descriptografar(chaveAEScliente, conta);
		System.out.println("\t\t-> Conta descriptografada de " + nomeCliente + ": " + conta);
		conta = cifrador.criptografar(this.chaveAES_GateAuth, conta);
		return conta;
	}

	public Conta fazerLogin(String nomeCliente, Conta conta) throws RemoteException, Exception {
		String chaveAEScliente = clienteChave.get(nomeCliente);
		conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "login");
		Conta contaLogada = stubAuth.fazerLogin(cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada), conta);
		if (contaLogada != null) {
			System.out.println("\t\t-> Logado com sucesso!");
			contaLogada = cifrador.descriptografar(chaveAES_GateAuth, contaLogada);
			contaLogada = cifrador.criptografar(chaveAEScliente, contaLogada);
			clienteChaveLogados.put(nomeCliente, chaveAEScliente);
			clienteChave.remove(nomeCliente);
			return contaLogada;
		}
		System.out.println("\t\t-> Falha no login, usuario ou senha incorretos!");
		return null;
	}

	public Conta fazerCadastro(String nomeCliente, Conta conta) throws RemoteException, Exception {
		String chaveAEScliente = clienteChave.get(nomeCliente);
		conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "cadastro");
		Conta contaCadastro = stubAuth.fazerCadastro(cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada), conta);
		if (contaCadastro != null) {
			System.out.println("\t\t-> Cadastrado com sucesso!");
			contaCadastro = cifrador.descriptografar(chaveAES_GateAuth, contaCadastro);
			contaCadastro = cifrador.criptografar(chaveAEScliente, contaCadastro);
			clienteChaveLogados.put(nomeCliente, chaveAEScliente);
			clienteChave.remove(nomeCliente);
			return contaCadastro;
		}
		System.out.println("\t\t-> Falha no cadastro!!");
		return null;
	}

	public Conta removerConta(String nomeCliente, Conta conta) throws RemoteException, Exception {
		String chaveAEScliente = clienteChaveLogados.get(nomeCliente);
		conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "remoção");
		Conta contaRemovida = stubAuth.removerConta(cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada), conta);
		if (contaRemovida == null) {
			System.out.println("\t\t-> Conta removida com sucesso!");
			contaRemovida = cifrador.descriptografar(chaveAES_GateAuth, contaRemovida);
			contaRemovida = cifrador.criptografar(chaveAEScliente, contaRemovida);
			return contaRemovida;
		}
		System.out.println("Servidor gateway -> Falha na remoção");
		return null;
	}

	// cliente recebe
	public ChavePubModulo receberChavePubModulo(String nomeCliente) {
		cifrador.gerarRSA();
		String chavePri = cifrador.getChavePri();
		String modulo = cifrador.getModulo();
		String chavePub = cifrador.getChavePub();
		String chavePriModulo = chavePri + ":" + modulo;
		System.out.println("\tGateway -> Novo cliente " + nomeCliente);
		System.out.println("\t\t-> Cliente " + nomeCliente + " com chave privada: " + chavePri);
		System.out.println("\t\t-> Cliente " + nomeCliente + " com Modulo: " + modulo);
		System.out.println("\t\t-> Cliente " + nomeCliente + " com chave publica: " + chavePub);
		this.clienteChave.put(nomeCliente, chavePriModulo);
		System.out.println("\t\t-> Cliente " + nomeCliente + " recebeu chave publica e modulo");
		return cifrador.getChavePubModulo();
	}

	// recebe chaveAES do cliente criptografada com RSA
	public void enviarChaveAES(String nomeCliente, String chaveAEScriptograda) {
		String[] chavePriModulo = clienteChave.get(nomeCliente).split(":");
		String chavePri = chavePriModulo[0];
		String modulo = chavePriModulo[1];
		System.out.println("\tGateway -> Cliente " + nomeCliente + " enviou ChaveAES criptografada: "
				+ chaveAEScriptograda);
		String chaveAES = cifrador.descriptografarRSA(chaveAEScriptograda, chavePri, modulo);
		System.out.println("\t\t-> Cliente " + nomeCliente + " ChaveAES descriptografada: " + chaveAES);
		this.clienteChave.put(nomeCliente, chaveAES);
	}

}
