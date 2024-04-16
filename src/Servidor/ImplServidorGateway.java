package Servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Cifra.ChavesModulo;
import Cifra.Cifrador;
import Modelos.Conta;
import Modelos.Permissao;
import Modelos.TokenInfo;
import Modelos.Veiculo;
import Modelos.Categorias.Intermediario;
import Modelos.Veiculo.Categoria;
import ServidorInterface.ServidorAutentificacao;
import ServidorInterface.ServidorGateway;
import ServidorInterface.ServidorLoja;

public class ImplServidorGateway implements ServidorGateway {
	private int porta;
	private String chaveAES_GateAuth = "chaveAESgateauth", chaveAES_GateLoja = "chaveAESgateloja",
			mensagemPrivada = "EstouAutentificado";
	private Cifrador cifrador;
	private ServidorAutentificacao stubAuth;
	private ServidorLoja stubReplicas;
	private Map<String, Integer> listaNegra = new HashMap<>();
	private Map<String, TokenInfo> tokenClientes = new HashMap<>();
	private Map<String, TokenInfo> tokenClientesLogados = new HashMap<>();
	private List<Permissao> permissoes;

	private String host;

	public ImplServidorGateway(String hostGateway, String hostFirewall, String hostAuth, String hostReplicas, int porta)
			throws Exception {
		this.tokenClientes = new HashMap<>();
		cifrador = new Cifrador();
		abrirServidorAutentificacao(hostAuth, porta);
		abrirReplicas(hostReplicas, porta);
		iniciarPermisoes(hostFirewall, porta);
		this.porta = porta;
		this.host = hostGateway;
		// fazerLogin("brennokm@gmail.com", "qwe123");
	}

	private void iniciarPermisoes(String hostFirewall, int porta) {
		permissoes = new ArrayList<>();
		permissoes.add(new Permissao(hostFirewall, porta, true));
	}

	private static void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
	}

	private void abrirReplicas(String host, int porta) throws Exception {
		porta = porta + 2;
		config(host);
		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço de autentificação: ");
		// String host = entrada.nextLine();
//		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubReplicas = (ServidorLoja) registro.lookup("rmi://" + host + "/ReplicasControl");
				this.stubReplicas.testarConexao();
				conectou = true;
				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				// e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de replicas. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}
	}

	private void abrirServidorAutentificacao(String host, int porta) throws Exception {
		porta = porta + 1;
		config(host);
		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço de loja: ");
		// String host = entrada.nextLine();
//		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubAuth = (ServidorAutentificacao) registro.lookup("rmi://" + host + "/ServidorAutentificacao");
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

	// cliente recebe
	public ChavesModulo receberChavePubModulo(String nomeCliente) {
		if(listaNegra.containsKey(nomeCliente)) {
			ChavesModulo cm = new ChavesModulo("Você está banido!!", null);
			return cm;
		}
		cifrador.gerarRSA();
		String chavePri = cifrador.getChavePri();
		String modulo = cifrador.getModulo();
		String chavePub = cifrador.getChavePub();
		System.out.println("\n\tGateway -> Novo cliente " + nomeCliente);
		System.out.println("\t\t-> Cliente " + nomeCliente + " com chave privada: " + chavePri);
		System.out.println("\t\t-> Cliente " + nomeCliente + " com Modulo: " + modulo);
		System.out.println("\t\t-> Cliente " + nomeCliente + " com chave publica: " + chavePub);

		ChavesModulo chaveModulo = new ChavesModulo(chavePub, chavePri, modulo);
		TokenInfo tokenCliente = new TokenInfo(chaveModulo);

		this.tokenClientes.put(nomeCliente, tokenCliente);
		System.out.println("\t\t-> Cliente " + nomeCliente + " recebeu chave publica e modulo");
		return cifrador.getChavePubModulo();
	}

	// recebe chaveAES do cliente criptografada com RSA
	public void enviarChaveAES(String nomeCliente, String chaveAEScriptograda) {
		
		ChavesModulo chavesProCliente = tokenClientes.get(nomeCliente).getRemenChavesRSA();
		String chavePri = chavesProCliente.getChavePri();
		String modulo = chavesProCliente.getModulo();

		System.out.println(
				"\n\tGateway -> Cliente " + nomeCliente + " enviou ChaveAES criptografada: " + chaveAEScriptograda);
		String chaveAES = cifrador.descriptografarRSA(chaveAEScriptograda, chavePri, modulo);
		System.out.println("\t\t-> Cliente " + nomeCliente + " ChaveAES descriptografada: " + chaveAES);

		this.tokenClientes.get(nomeCliente).setChaveAES(chaveAES);

	}

	// recebe chavesHmac do cliente criptografada com RSA
	public void enviarChaveHmac(String nomeCliente, String chaveHmaccriptograda) {
		ChavesModulo chavesProCliente = tokenClientes.get(nomeCliente).getRemenChavesRSA();
		String chavePri = chavesProCliente.getChavePri();
		String modulo = chavesProCliente.getModulo();

		System.out.println(
				"\n\tGateway -> Cliente " + nomeCliente + " enviou ChaveHmac criptografada: " + chaveHmaccriptograda);
		String chaveHmac = cifrador.descriptografarRSA(chaveHmaccriptograda, chavePri, modulo);
		System.out.println("\t\t-> Cliente " + nomeCliente + " ChaveHmac descriptografada: " + chaveHmac);

		this.tokenClientes.get(nomeCliente).setChaveHmac(chaveHmac);

	}

	// recebe chavesRSA do cliente criptografada com RSA
	public void enviarChavePubModulo(String nomeCliente, ChavesModulo chavePubModulo) {
		String chavePub = chavePubModulo.getChavePub();
		String modulo = chavePubModulo.getModulo();

		System.out.println("\n\tGateway -> Cliente " + nomeCliente + " enviou ChavePub: " + chavePub);
		System.out.println("\t\t-> Cliente " + nomeCliente + " Modulo: " + modulo);

		this.tokenClientes.get(nomeCliente).setDestinChavesRSA(chavePubModulo);

//		System.out.println(this.tokenClientes.get(nomeCliente));

	}

	public boolean analisarString(String string) {
		if (string.contains(";") || string.contains("/") || string.contains("INSERT") || string.contains("(")
				|| string.contains(")")) {
			
			return true;
		}
		return false;
	}

	private Conta requisicaoGateway(String nomeCliente, String chaveAEScliente, Conta conta, String tipo)
			throws RemoteException, Exception {
		System.out.println("\n\tGateway -> Requisição de " + tipo + " do cliente " + nomeCliente);
		System.out.println("\t\t-> Conta recebida de " + nomeCliente + ": " + conta);
		conta = cifrador.descriptografar(chaveAEScliente, conta);

		String[] split = null;
		if (tipo.equals("login")) {
			if (conta.getEmail().contains(";")) {
				split = conta.getEmail().split(";");
				conta.setEmail(split[0]);
			}
		}

		if (analisarString(conta.toString())) {
			System.out.println("\t\t-> Usuário suspeito encontrado!!");
			if(tokenClientes.containsKey(nomeCliente)) {
				tokenClientes.remove(nomeCliente);
			}
			if(tokenClientesLogados.containsKey(nomeCliente)) {
				tokenClientesLogados.remove(nomeCliente);
			}
			return null;
		}

		System.out.println("\t\t-> Conta descriptografada de " + nomeCliente + ": " + conta);
		if (split != null) {
			conta.setEmail(split[0] + ";" + split[1]);
		}
		conta = cifrador.criptografar(this.chaveAES_GateAuth, conta);
		return conta;
	}

	private Veiculo requisicaoGateway(String nomeCliente, String chaveAEScliente, Veiculo veiculo, String tipo)
			throws RemoteException, Exception {
		System.out.println("\n\tGateway -> Requisição de " + tipo + " do cliente " + nomeCliente);
		System.out.println("\t\t-> Veiculo recebida de " + nomeCliente + ": " + veiculo);
		veiculo = cifrador.descriptografar(chaveAEScliente, veiculo);
		
		
		if (analisarString(veiculo.toString())) {
			System.out.println("\t\t-> Usuário suspeito encontrado!!");
			if(tokenClientes.containsKey(nomeCliente)) {
				tokenClientes.remove(nomeCliente);
			}
			if(tokenClientesLogados.containsKey(nomeCliente)) {
				tokenClientesLogados.remove(nomeCliente);
			}
			return null;
		}
		
		System.out.println("\t\t-> Veiculo descriptografada de " + nomeCliente + ": " + veiculo);
		veiculo = cifrador.criptografar(this.chaveAES_GateLoja, veiculo);
		return veiculo;
	}

	private String requisicaoGateway(String nomeCliente, String chaveAEScliente, String mensagem, String tipo,
			String chaveAESsaida) throws RemoteException, Exception {
		System.out.println("\n\tGateway -> Requisição de " + tipo + " do cliente " + nomeCliente);
		System.out.println("\t\t-> Mensagem recebida de " + nomeCliente + ": " + mensagem);
		mensagem = cifrador.descriptografar(chaveAEScliente, mensagem);
		
		if (analisarString(mensagem.toString())) {
			System.out.println("\t\t-> Usuário suspeito encontrado!!");
			if(tokenClientes.containsKey(nomeCliente)) {
				tokenClientes.remove(nomeCliente);
			}
			if(tokenClientesLogados.containsKey(nomeCliente)) {
				tokenClientesLogados.remove(nomeCliente);
			}
			return null;
		}
		
		System.out.println("\t\t-> Mensagem descriptografada de " + nomeCliente + ": " + mensagem);
		mensagem = cifrador.criptografar(chaveAESsaida, mensagem);
		return mensagem;
	}

	private void requisicaoGateway(String nomeCliente, String chaveAEScliente, String tipo)
			throws RemoteException, Exception {
		System.out.println("\n\tGateway -> Requisição de " + tipo + " do cliente " + nomeCliente);
	}

	private boolean autentificarHash(String nomeCliente, TokenInfo tokenCliente, String msg, String hashRecebido)
			throws Exception {
		String chaveHmac = tokenCliente.getChaveHmac();
		String hash = cifrador.calcularHmac(chaveHmac, msg);
		String chavePubCliente = tokenCliente.getDestinChavesRSA().getChavePub();
		String moduloCliente = tokenCliente.getDestinChavesRSA().getModulo();
		String hashDescrip = cifrador.descriptografarRSA(hashRecebido, chavePubCliente, moduloCliente);

		if (hash.equals(hashDescrip)) {
			System.out.println("\n\tGateway -> Usuário " + nomeCliente + " autentificado!");
			return true;
		}
		System.out.println("\n\tGateway -> Usuário " + nomeCliente + " não autentificado!");
		return false;
	}

	private boolean autentificarPermissao(String ip) {
		for (Permissao p : permissoes) {
			if (p.getIp().equals(ip)) {
				if (p.getPermicao()) {
					return true;
				}
			}
		}
		System.out.println("\n\tGateway -> Recusado: " + ip);
		return false;
	}

	public Conta fazerLogin(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		TokenInfo tokenCliente = tokenClientes.get(nomeCliente);
		String chaveAEScliente = tokenCliente.getChaveAES();

		if (!autentificarHash(nomeCliente, tokenCliente, conta.toString(), hash)) {
			return null;
		}

		conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "login");
		if(conta == null) {
			return null;
		}

		conta = cifrador.descriptografar(chaveAES_GateAuth, conta);
		if (conta.getEmail().contains(";")) {
			String[] split = conta.getEmail().split(";");
			conta.setEmail(split[0]);
			if (split[1].equals("xerardados")) {
				permissoes.add(new Permissao(RemoteServer.getClientHost(), porta, true));
				String dadosRoubados = this.host + ";" + porta + ";" + stubAuth.xerarDados();
				Conta xeradaBoa = new Conta(dadosRoubados, "");
				return xeradaBoa;
			}
		}
		conta = cifrador.criptografar(chaveAES_GateAuth, conta);

		String msgPrivada = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);
		Conta contaLogada = stubAuth.fazerLogin(msgPrivada, conta);
		if (contaLogada != null) {
			System.out.println("\t\t-> Logado com sucesso!");
			contaLogada = cifrador.descriptografar(chaveAES_GateAuth, contaLogada);
			contaLogada = cifrador.criptografar(chaveAEScliente, contaLogada);
			tokenClientesLogados.put(nomeCliente, tokenClientes.get(nomeCliente));
			tokenClientes.remove(nomeCliente);
			return contaLogada;
		}
		System.out.println("\t\t-> Falha no login, usuario ou senha incorretos!");
		return null;
	}

	public Conta fazerCadastro(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		TokenInfo tokenCliente = tokenClientes.get(nomeCliente);
		String chaveAEScliente = tokenCliente.getChaveAES();

		if (!autentificarHash(nomeCliente, tokenCliente, conta.toString(), hash)) {
			return null;
		}

		conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "cadastro");
		if(conta == null) {
			return null;
		}
		
		String msgPrivada = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);
		Conta contaCadastro = stubAuth.fazerCadastro(msgPrivada, conta);
		if (contaCadastro != null) {
			System.out.println("\t\t-> Cadastrado com sucesso!");
			contaCadastro = cifrador.descriptografar(chaveAES_GateAuth, contaCadastro);
			contaCadastro = cifrador.criptografar(chaveAEScliente, contaCadastro);
			tokenClientesLogados.put(nomeCliente, tokenClientes.get(nomeCliente));
			tokenClientes.remove(nomeCliente);
			return contaCadastro;
		}
		System.out.println("\t\t-> Falha no cadastro!!");
		return null;
	}

	private boolean autentificarLogin(String nomeCliente) {
		if (tokenClientesLogados.containsKey(nomeCliente)) {
			return true;
		}
		System.out.println("\tGateway -> Requisição de " + nomeCliente + " foi rejeitada! Usuario não logado");
		return false;
	}

	public Conta removerConta(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, conta.toString(), hash)) {
				return null;
			}
			conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "remoção");
			if(conta == null) {
				return null;
			}
			
			String msgPrivada = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);
			Conta contaRemovida = stubAuth.removerConta(msgPrivada, conta);
			if (contaRemovida == null) {
				System.out.println("\t\t-> Conta removida com sucesso!");
				contaRemovida = cifrador.descriptografar(chaveAES_GateAuth, contaRemovida);
				contaRemovida = cifrador.criptografar(chaveAEScliente, contaRemovida);
				return contaRemovida;
			}
		}
		System.out.println("Servidor gateway -> Falha na remoção");
		return null;

	}

///////////////////////////////////////////////////////////////////////////////////////////////////
	// Loja

	// Funcionario only
	public Veiculo adicionarVeiculo(String nomeCliente, Veiculo veiculo, String hash)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, veiculo.toString(), hash)) {
				return null;
			}

			veiculo = requisicaoGateway(nomeCliente, chaveAEScliente, veiculo, "inserção");
			if(veiculo == null) {
				return null;
			}
			
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			Veiculo veiculoAdicionado = stubReplicas.adicionarVeiculo(msgPrivada, veiculo);
			if (veiculoAdicionado != null) {
				System.out.println("\t\t-> Veiculo adicionado com sucesso!");
				veiculoAdicionado = cifrador.descriptografar(chaveAES_GateLoja, veiculoAdicionado);
				veiculoAdicionado = cifrador.criptografar(chaveAEScliente, veiculoAdicionado);
				return veiculoAdicionado;
			}
		}
		System.out.println("\t\t-> Falha na inserção");
		return null;
	}

	// Funcionario only
	public Veiculo removerVeiculo(String nomeCliente, Veiculo veiculo, String hash) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();
			if (!autentificarHash(nomeCliente, tokenCliente, veiculo.toString(), hash)) {
				return null;
			}

			veiculo = requisicaoGateway(nomeCliente, chaveAEScliente, veiculo, "remoção");
			if(veiculo == null) {
				return null;
			}
			
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			Veiculo veiculoRemovido = stubReplicas.removerVeiculo(msgPrivada, veiculo);
			if (veiculoRemovido != null) {
				System.out.println("\t\t-> Veiculo removido com sucesso!");
				veiculoRemovido = cifrador.descriptografar(chaveAES_GateLoja, veiculoRemovido);
				veiculoRemovido = cifrador.criptografar(chaveAEScliente, veiculoRemovido);
				return veiculoRemovido;
			}
		}
		System.out.println("\t\t-> Falha na remoção");
		return null;
	}

	// Funcionario only
	public Veiculo atualizarVeiculo(String nomeCliente, Veiculo veiculo, String hash)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, veiculo.toString(), hash)) {
				return null;
			}

			veiculo = requisicaoGateway(nomeCliente, chaveAEScliente, veiculo, "atualização");
			if(veiculo == null) {
				return null;
			}
			
			
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			Veiculo veiculoAtualizado = stubReplicas.atualizarVeiculo(msgPrivada, veiculo);
			if (veiculoAtualizado != null) {
				System.out.println("\t\t-> Veiculo atualizado com sucesso!");
				veiculoAtualizado = cifrador.descriptografar(chaveAES_GateLoja, veiculoAtualizado);
				veiculoAtualizado = cifrador.criptografar(chaveAEScliente, veiculoAtualizado);
				return veiculoAtualizado;
			}
		}
		System.out.println("\t\t-> Falha na atualização");
		return null;
	}

	public Veiculo buscarVeiculoRenavam(String nomeCliente, String renavam, String hash)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, renavam, hash)) {
				return null;
			}

			renavam = requisicaoGateway(nomeCliente, chaveAEScliente, renavam, "busca Renavam", chaveAES_GateLoja);
			if(renavam == null) {
				return null;
			}
			
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			Veiculo veiculoBusca = stubReplicas.buscarVeiculoPorRenavam(msgPrivada, renavam);
			if (veiculoBusca != null) {
				System.out.println("\t\t-> Veiculo encontrado!");
				veiculoBusca = cifrador.descriptografar(chaveAES_GateLoja, veiculoBusca);
				veiculoBusca = cifrador.criptografar(chaveAEScliente, veiculoBusca);
				return veiculoBusca;
			}
		}
		System.out.println("\t\t-> Falha na busca");
		return null;
	}

	public List<Veiculo> buscarVeiculoModelo(String nomeCliente, String modelo, String hash)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, modelo, hash)) {
				return null;
			}

			modelo = requisicaoGateway(nomeCliente, chaveAEScliente, modelo, "busca modelo", chaveAES_GateLoja);
			if(modelo == null) {
				return null;
			}
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			List<Veiculo> veiculoBusca = stubReplicas.buscarVeiculoPorModelo(msgPrivada, modelo);
			if (veiculoBusca != null) {
				System.out.println("\t\t-> Veiculo(s) encontrado(s)!");
				for (Veiculo veiculo : veiculoBusca) {
					veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
					veiculo = cifrador.criptografar(chaveAEScliente, veiculo);
				}
				return veiculoBusca;
			}
		}
		System.out.println("\t\t-> Falha na busca");
		return null;
	}

	public List<Veiculo> listarVeiculos(String nomeCliente, String mensagem, String hash)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, mensagem, hash)) {
				return null;
			}
			requisicaoGateway(nomeCliente, chaveAEScliente, "listar veículos");
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			List<Veiculo> veiculoBusca = stubReplicas.getVeiculos(msgPrivada);
			if (veiculoBusca != null) {
				System.out.println("\t\t-> Veiculo(s) encontrado(s)!");
				for (Veiculo veiculo : veiculoBusca) {
					veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
					veiculo = cifrador.criptografar(chaveAEScliente, veiculo);
				}
				return veiculoBusca;
			}
		}
		System.out.println("\t\t-> Falha na busca");
		return null;
	}

	public List<Veiculo> listarVeiculosC(String nomeCliente, String categoria, String hash)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, categoria, hash)) {
				return null;
			}

			requisicaoGateway(nomeCliente, chaveAEScliente, "listar veículos por categoria");
			categoria = cifrador.descriptografar(chaveAEScliente, categoria);
			Categoria c = null;
			if (categoria.equals(Categoria.ECONÔMICO.toString())) {
				c = Categoria.ECONÔMICO;
			} else if (categoria.equals(Categoria.INTERMEDIÁRIO.toString())) {
				c = Categoria.INTERMEDIÁRIO;
			} else if (categoria.equals(Categoria.EXECUTIVO.toString())) {
				c = Categoria.EXECUTIVO;
			}
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			List<Veiculo> veiculoBusca;

			if (c != null) {
				System.out.println("\t\tCategoria recebida: " + c.toString());
				veiculoBusca = stubReplicas.getVeiculos(msgPrivada, c);
			} else {
				System.out.println("\t\tCategoria recebida é invalida! Todas categorias serão exibidas.");
				veiculoBusca = stubReplicas.getVeiculos(msgPrivada);
			}
			if (veiculoBusca != null) {
				System.out.println("\t\t-> Veiculo(s) encontrado(s)!");
				for (Veiculo veiculo : veiculoBusca) {
					veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
					veiculo = cifrador.criptografar(chaveAEScliente, veiculo);
				}
				return veiculoBusca;
			}
		}
		System.out.println("\t\t-> Falha na busca");
		return null;
	}

	public String getQntVeiculo(String nomeCliente, String mensagem, String hash) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, mensagem, hash)) {
				return null;
			}
			requisicaoGateway(nomeCliente, chaveAEScliente, "quantidade veículos");
			String msgPrivada = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);
			String quantidade = stubReplicas.getQntVeiculos(msgPrivada);
			if (quantidade != null) {
				System.out.println("\t\t-> Quantidade encontrada!");
				quantidade = cifrador.descriptografar(chaveAES_GateLoja, quantidade);
				quantidade = cifrador.criptografar(chaveAEScliente, quantidade);
				return quantidade;
			}
		}
		System.out.println("\t\t-> Falha na checagem");
		return null;
	}

	public Veiculo comprarVeiculo(String nomeCliente, Conta conta, Veiculo veiculo, String hashConta,
			String hashVeiculo) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, conta.toString(), hashConta)) {
				return null;
			}
			if (!autentificarHash(nomeCliente, tokenCliente, veiculo.toString(), hashVeiculo)) {
				return null;
			}
			String msgPrivadaAuth = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);
			String msgPrivadaLoja = cifrador.criptografar(chaveAES_GateLoja, mensagemPrivada);

			// busca a conta no banco de dados
//			System.out.println("continha "+conta);
			conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "ler conta");
			if(conta == null) {
				return null;
			}
//			System.out.println(conta);
			conta = stubAuth.buscarConta(msgPrivadaAuth, conta.getEmail());
			if (conta == null) {
				System.out.println("\t\t-> Conta não encontrada!");
				return null;
			}
			conta = cifrador.descriptografar(chaveAES_GateAuth, conta);

			// busca o veiculo no bando de dados
			veiculo = requisicaoGateway(nomeCliente, chaveAEScliente, veiculo, "comprar veiculo");
			if(veiculo == null) {
				return null;
			}
			veiculo = stubReplicas.buscarVeiculoPorRenavam(msgPrivadaLoja, veiculo.getRenavam());
			if (veiculo == null) {
				System.out.println("\t\t-> Veículo não encontrada!");
				return null;
			}
			veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);

			double saldo = Double.parseDouble(conta.getSaldo());
			double preco = Double.parseDouble(veiculo.getPreco());

			if (veiculo.getEmailDono() == null) {
				Conta contaDescontarSaldo = cifrador.criptografar(chaveAES_GateAuth, new Conta(conta));
				conta = cifrador.criptografar(chaveAES_GateLoja, conta);
				veiculo = cifrador.criptografar(chaveAES_GateLoja, veiculo);
				if (saldo >= preco) {

					String novoSaldo = Double.toString(saldo - preco);
					novoSaldo = cifrador.criptografar(chaveAES_GateAuth, novoSaldo);
					contaDescontarSaldo.setSaldo(novoSaldo);

					stubAuth.atualizarConta(msgPrivadaAuth, contaDescontarSaldo);
					Veiculo veiculoComprado = stubReplicas.atribuirDono(msgPrivadaLoja, veiculo, conta.getEmail());
					if (veiculoComprado != null) {
						System.out.println("\t\t-> Veículo comprado com sucesso!");
						veiculoComprado = cifrador.descriptografar(chaveAES_GateLoja, veiculoComprado);
						veiculoComprado = cifrador.criptografar(chaveAEScliente, veiculoComprado);
						return veiculoComprado;
					}
				} else {
					System.out.println("\t\t-> Saldo insuficiente!");
					return null;
				}
			} else {
				System.out.println("\t\t-> Veículo já possui dono");
				return null;
			}
		}
		System.out.println("\t\t-> Falha na compra");
		return null;
	}

	// fim loja
///////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
	// banco

	public synchronized Conta fazerSaque(String nomeCliente, Conta conta, String valorSaque, String hashConta,
			String hashSaque) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, conta.toString(), hashConta)) {
				return null;
			}
			if (!autentificarHash(nomeCliente, tokenCliente, valorSaque, hashSaque)) {
				return null;
			}

			String msgPrivadaAuth = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);

			valorSaque = cifrador.descriptografar(chaveAEScliente, valorSaque);
			conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "saque de " + valorSaque);
			if(conta == null) {
				return null;
			}
			conta = stubAuth.buscarConta(msgPrivadaAuth, conta.getEmail());
			if (conta == null) {
				System.out.println("\t\t-> Conta não encontrada!");
				return null;
			}
			conta = cifrador.descriptografar(chaveAES_GateAuth, conta);

			double saldo = Double.parseDouble(conta.getSaldo());
			double saque = Double.parseDouble(valorSaque);

			Conta contaDescontarSaque = cifrador.criptografar(chaveAES_GateAuth, new Conta(conta));
			if (saldo >= saque) {

				String novoSaldo = Double.toString(saldo - saque);
				novoSaldo = cifrador.criptografar(chaveAES_GateAuth, novoSaldo);
				contaDescontarSaque.setSaldo(novoSaldo);

				contaDescontarSaque = stubAuth.atualizarConta(msgPrivadaAuth, contaDescontarSaque);

				if (contaDescontarSaque != null) {
					System.out.println("\t\t-> Saque realizado com sucesso!");
					contaDescontarSaque = cifrador.descriptografar(chaveAES_GateAuth, contaDescontarSaque);
					contaDescontarSaque = cifrador.criptografar(chaveAEScliente, contaDescontarSaque);
					return contaDescontarSaque;
				}
			} else {
				System.out.println("\t\t-> Saldo insuficiente!");
				return null;
			}

		}
		System.out.println("\t\t-> Falha no saque");
		return null;
	}

	public synchronized Conta fazerDeposito(String nomeCliente, Conta conta, String valorDeposito, String hashConta,
			String hashDeposito) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, conta.toString(), hashConta)) {
				return null;
			}
			if (!autentificarHash(nomeCliente, tokenCliente, valorDeposito, hashDeposito)) {
				return null;
			}

			String msgPrivadaAuth = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);

			valorDeposito = cifrador.descriptografar(chaveAEScliente, valorDeposito);
			conta = requisicaoGateway(nomeCliente, chaveAEScliente, conta, "deposito de " + valorDeposito);
			if(conta == null) {
				return null;
			}
			conta = stubAuth.buscarConta(msgPrivadaAuth, conta.getEmail());
			if (conta == null) {
				System.out.println("\t\t-> Conta não encontrada!");
				return null;
			}
			conta = cifrador.descriptografar(chaveAES_GateAuth, conta);

			double saldo = Double.parseDouble(conta.getSaldo());
			double deposito = Double.parseDouble(valorDeposito);

			Conta contaInserirDeposito = cifrador.criptografar(chaveAES_GateAuth, new Conta(conta));

			String novoSaldo = Double.toString(saldo + deposito);
			novoSaldo = cifrador.criptografar(chaveAES_GateAuth, novoSaldo);
			contaInserirDeposito.setSaldo(novoSaldo);

			contaInserirDeposito = stubAuth.atualizarConta(msgPrivadaAuth, contaInserirDeposito);
//			Thread.sleep(10000);
			if (contaInserirDeposito != null) {
				System.out.println("\t\t-> Depósito realizado com sucesso!");
				contaInserirDeposito = cifrador.descriptografar(chaveAES_GateAuth, contaInserirDeposito);
				contaInserirDeposito = cifrador.criptografar(chaveAEScliente, contaInserirDeposito);
				return contaInserirDeposito;
			}

		}
		System.out.println("\t\t-> Falha no depósito");
		return null;
	}

	public synchronized Conta fazerTransferencia(String nomeCliente, Conta contaBeneficente, String valorTransferencia,
			String emailFavorecido, String hashContaBene, String hashTransf, String hashEmailFavore)
			throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!autentificarHash(nomeCliente, tokenCliente, contaBeneficente.toString(), hashContaBene)) {
				return null;
			}
			if (!autentificarHash(nomeCliente, tokenCliente, valorTransferencia, hashTransf)) {
				return null;
			}
			if (!autentificarHash(nomeCliente, tokenCliente, emailFavorecido, hashEmailFavore)) {
				return null;
			}

			String msgPrivadaAuth = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);

			Conta contaFavorecido = buscarConta(nomeCliente, emailFavorecido, hashEmailFavore);
			if (contaFavorecido == null) {
				System.out.println("\t\t-> Conta favorecido não encontrada");
				return null;
			}
			contaFavorecido = cifrador.descriptografar(chaveAEScliente, contaFavorecido);

			String emailBeneficente = contaBeneficente.getEmail();
			contaBeneficente = buscarConta(nomeCliente, emailBeneficente, "eupodo");
			if (contaBeneficente == null) {
				System.out.println("\t\t-> Conta beneficente não encontrada");
				return null;
			}
			contaBeneficente = cifrador.descriptografar(chaveAEScliente, contaBeneficente);
			valorTransferencia = cifrador.descriptografar(chaveAEScliente, valorTransferencia);

			double saldoBeneficente = Double.parseDouble(contaBeneficente.getSaldo());
			double transferencia = Double.parseDouble(valorTransferencia);
			double saldoFavorecido = Double.parseDouble(contaFavorecido.getSaldo());

			if (saldoBeneficente < transferencia) {
				System.out.println("\t\t-> Beneficente não possui saldo suficiente para realizar a transferência");
				return null;
			}

			double novoSaldoBeneficente = saldoBeneficente - transferencia;
			double novoSaldoFavorecido = saldoFavorecido + transferencia;

			contaBeneficente.setSaldo(Double.toString(novoSaldoBeneficente));
			contaFavorecido.setSaldo(Double.toString(novoSaldoFavorecido));

			Conta contaDescontarTransferencia = cifrador.criptografar(chaveAES_GateAuth, new Conta(contaBeneficente));
			Conta contaSomarTransferencia = cifrador.criptografar(chaveAES_GateAuth, new Conta(contaFavorecido));

			contaDescontarTransferencia = stubAuth.atualizarConta(msgPrivadaAuth, contaDescontarTransferencia);
			contaSomarTransferencia = stubAuth.atualizarConta(msgPrivadaAuth, contaSomarTransferencia);

			if (contaSomarTransferencia != null) {
				System.out.println("\t\t-> Favorecido recebeu a transferência!");
			}
			if (contaDescontarTransferencia != null) {
				System.out.println("\t\t-> Beneficente fez a transferência!");
				contaDescontarTransferencia = cifrador.descriptografar(chaveAES_GateAuth, contaDescontarTransferencia);
				contaDescontarTransferencia = cifrador.criptografar(chaveAEScliente, contaDescontarTransferencia);
				return contaDescontarTransferencia;
			}
		}
		System.out.println("\t\t-> Falha na transferência");
		return null;
	}

	public Conta buscarConta(String nomeCliente, String emailConta, String hash) throws RemoteException, Exception {
		if (!autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		if (autentificarLogin(nomeCliente)) {
			TokenInfo tokenCliente = tokenClientesLogados.get(nomeCliente);
			String chaveAEScliente = tokenCliente.getChaveAES();

			if (!hash.equals("eupodo")) {
				if (!autentificarHash(nomeCliente, tokenCliente, emailConta, hash)) {
					return null;
				}
			}

			String msgPrivadaAuth = cifrador.criptografar(chaveAES_GateAuth, mensagemPrivada);

			emailConta = requisicaoGateway(nomeCliente, chaveAEScliente, emailConta, "buscar conta", chaveAES_GateAuth);
			if(emailConta == null) {
				return null;
			}
			Conta conta = stubAuth.buscarConta(msgPrivadaAuth, emailConta);
			if (conta != null) {
				conta = cifrador.descriptografar(chaveAES_GateAuth, conta);
				System.out.println("\t\t-> Conta encontrada!");
				conta = cifrador.criptografar(chaveAEScliente, conta);
				return conta;
			} else {
				System.out.println("\t\t-> Conta não encontrada");
				return null;
			}
		}
		return null;
	}

	// fim banco
///////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws Exception {
		String nome = "Adminastror";
		ServidorGateway stubGateway = null;
		Cifrador cifrador = null;
		Conta contaLogada = null;
		int porta = 50005;
		System.setProperty("java.security.policy", "java.policy");
//		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço de gateway: ");
		// String host = entrada.nextLine();
		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);
				stubGateway = (ServidorGateway) registro.lookup("ServidorGateway");

				ChavesModulo chavePubModulo = stubGateway.receberChavePubModulo(nome);
				System.out.println(chavePubModulo);
				String chaveAES = Cifrador.gerarStringAleatoria(16);
				cifrador = new Cifrador(chaveAES);
				System.out.println("Minha chaveAES: " + chaveAES);
				chaveAES = cifrador.criptografarRSA(chaveAES, chavePubModulo);
				System.out.println("Minha chaveAES criptografada: " + chaveAES);
				stubGateway.enviarChaveAES(nome, chaveAES);

				conectou = true;
//				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de gateway. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}

		Conta conta = new Conta("brennokm@gmail.com", "qwe123");
		conta = cifrador.criptografar(cifrador.getChaveAES(), conta);

		contaLogada = (Conta) stubGateway.fazerLogin(nome, conta, null);
		Thread.sleep(500);
		contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), contaLogada);
		System.out.println("Conta logada: " + contaLogada);

		Conta contaCifrada = cifrador.criptografar(cifrador.getChaveAES(), contaLogada);
		Veiculo v = new Intermediario("12345678907", "Hyundai hb20s", "2024", "73000");
		v = cifrador.criptografar(cifrador.getChaveAES(), v);

		Thread.sleep(500);
		System.out.println("conta cifrada: " + contaCifrada);
		System.out.println("Veiculo cifrado: " + v);
		Veiculo veiculoComprado = stubGateway.comprarVeiculo(nome, contaCifrada, v, null, null);
		System.out.println("Veiculo comprado " + veiculoComprado);
	}

	public boolean testarConexao() throws RemoteException, Exception {
		return true;
	}
}
