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

import Cifra.ChavesModulo;
import Modelos.Conta;
import Modelos.Permissao;
import Modelos.Veiculo;
import ServidorInterface.ServidorGateway;

public class ImplFirewall implements ServidorGateway {

	private int porta;
	private ServidorGateway stubGateway;
	private List<Permissao> permissoes;
	private Map<String, Integer> mapSuspeitos = new HashMap<>();

	public ImplFirewall(String hostGateway, int porta) throws Exception {
		iniciarPermisoes();
		abrirServidorGateway(hostGateway, porta);
		this.porta = porta;
	}

	private void iniciarPermisoes() {
		permissoes = new ArrayList<>();
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
//				System.out.println(host + " " + porta);
				Registry registro = LocateRegistry.getRegistry(host, porta);
				this.stubGateway = (ServidorGateway) registro.lookup("rmi://" + host + "/ServidorGateway");
				this.stubGateway.testarConexao();
				conectou = true;
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de gateway. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}
	}

	private boolean autentificarPermissao(String ip) {
		for (Permissao p : permissoes) {
			if (p.getIp().equals(ip)) {
				if (p.getPermicao() == false) {
					System.out.println("\nFirewall -> Recusado: " + ip);
					return true;
				}
			}
		}
		System.out.println("\nFirewall -> Aceito: " + ip);
		return false;
	}

	public Conta fazerLogin(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}

		if (mapSuspeitos.containsKey(nomeCliente)) {
			if (mapSuspeitos.get(nomeCliente) == 3) {
				System.out.println(
						"\nFirewall -> Cliente " + nomeCliente + " foi bloqueado por tentativas excessivas de login");
				permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
				return null;
			}
		}
		Conta retorno = (Conta) stubGateway.fazerLogin(nomeCliente, conta, hash);
		if (retorno == null) {
			if (mapSuspeitos.containsKey(nomeCliente)) {
				mapSuspeitos.put(nomeCliente, (mapSuspeitos.get(nomeCliente) + 1));
			} else {
				mapSuspeitos.put(nomeCliente, 1);
			}
			return null;
		}
		return retorno;
	}

	public Conta fazerCadastro(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.fazerCadastro(nomeCliente, conta, hash);
	}

	public Conta removerConta(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.removerConta(nomeCliente, conta, hash);
	}

	public ChavesModulo receberChavePubModulo(String nomeCliente) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
//			System.out.println("não recebeu chave");
			return null;
		}
//		System.out.println("recebeu chave");
		return stubGateway.receberChavePubModulo(nomeCliente);
	}

	public void enviarChaveAES(String nomeCliente, String chaveAEScriptograda) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
//			System.out.println("não enviou chave aes");
		} else {
			stubGateway.enviarChaveAES(nomeCliente, chaveAEScriptograda);
//			System.out.println("enviou chave aes");
		}
	}

	public void enviarChaveHmac(String nome, String chaveHmac) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			
		} else {
			stubGateway.enviarChaveHmac(nome, chaveHmac);
		}
	}

	public void enviarChavePubModulo(String nomeCliente, ChavesModulo chavePubModulo)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
//			System.out.println("não enviou chave");
		} else {
			stubGateway.enviarChavePubModulo(nomeCliente, chavePubModulo);
//			System.out.println("enviou chave");
		}
	}

	public Veiculo adicionarVeiculo(String nomeCliente, Veiculo veiculo, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}

		return stubGateway.adicionarVeiculo(nomeCliente, veiculo, hash);
	}

	public Veiculo removerVeiculo(String nomeCliente, Veiculo veiculo, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}

		return stubGateway.removerVeiculo(nomeCliente, veiculo, hash);
	}

	public Veiculo atualizarVeiculo(String nomeCliente, Veiculo veiculo, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.atualizarVeiculo(nomeCliente, veiculo, hash);
	}

	public Veiculo buscarVeiculoRenavam(String nomeCliente, String renavam, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.buscarVeiculoRenavam(nomeCliente, renavam, hash);
	}

	public List<Veiculo> buscarVeiculoModelo(String nomeCliente, String modelo, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.buscarVeiculoModelo(nomeCliente, modelo, hash);
	}

	public List<Veiculo> listarVeiculos(String nomeCliente, String mensagem, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.listarVeiculos(nomeCliente, mensagem, hash);
	}

	public List<Veiculo> listarVeiculosC(String nomeCliente, String categoria, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.listarVeiculosC(nomeCliente, categoria, hash);
	}

	public String getQntVeiculo(String nomeCliente, String mensagem, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.getQntVeiculo(nomeCliente, mensagem, hash);
	}

	public Veiculo comprarVeiculo(String nomeCliente, Conta conta, Veiculo veiculo, String hashConta,
			String hashVeiculo) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.comprarVeiculo(nomeCliente, conta, veiculo, hashConta, hashVeiculo);
	}

	public Conta fazerSaque(String nomeCliente, Conta conta, String valorSaque, String hashConta, String hashSaque)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.fazerSaque(nomeCliente, conta, valorSaque, hashConta, hashSaque);
	}

	public Conta fazerDeposito(String nomeCliente, Conta conta, String valorDeposito, String hashConta,
			String hashDeposito) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.fazerDeposito(nomeCliente, conta, valorDeposito, hashConta, hashDeposito);
	}

	public Conta fazerTransferencia(String nomeCliente, Conta contaBeneficente, String valorTransferencia,
			String emailFavorecido, String hashContaBene, String hashTransf, String hashEmailFavore)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.fazerTransferencia(nomeCliente, contaBeneficente, valorTransferencia, emailFavorecido,
				hashContaBene, hashTransf, hashEmailFavore);
	}

	public Conta buscarConta(String nomeCliente, String emailConta, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		return stubGateway.buscarConta(nomeCliente, emailConta, hash);
	}

	public boolean testarConexao() throws RemoteException, Exception {
		return true;
	}
}
