package Servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
						"\nFirewall -> Cliente " + nomeCliente + " foi bloqueado por 10 segundo por tentativas excessivas de login");
				permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
				final String ip = RemoteServer.getClientHost();
				Thread th = new Thread(() -> {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mapSuspeitos.remove(nomeCliente);
					Iterator<Permissao> iterator = permissoes.iterator();
					while (iterator.hasNext()) {
					    Permissao p = iterator.next();
					    if (p.getIp().equals(ip)) {
					        iterator.remove();
					    }
					}
				});
				th.start();
				return null;
			}
		}
		Conta retorno = (Conta) stubGateway.fazerLogin(nomeCliente + "#" + RemoteServer.getClientHost(), conta, hash);
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
			return null;
		}
		ChavesModulo cm = stubGateway.receberChavePubModulo(nomeCliente + "#" + RemoteServer.getClientHost());
		if(cm.getChavePub().equals("podebanir") && cm.getModulo() == null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return cm;
		}
		return cm;
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
		Veiculo v = stubGateway.adicionarVeiculo(nomeCliente, veiculo, hash);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return v;
	}

	public Veiculo removerVeiculo(String nomeCliente, Veiculo veiculo, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}

		Veiculo v = stubGateway.removerVeiculo(nomeCliente, veiculo, hash);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return v;
	}

	public Veiculo atualizarVeiculo(String nomeCliente, Veiculo veiculo, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		Veiculo v = stubGateway.atualizarVeiculo(nomeCliente, veiculo, hash);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return v;
	}

	public Veiculo buscarVeiculoRenavam(String nomeCliente, String renavam, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		Veiculo v = stubGateway.buscarVeiculoRenavam(nomeCliente, renavam, hash);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return v;
	}

	public List<Veiculo> buscarVeiculoModelo(String nomeCliente, String modelo, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		List<Veiculo> vlist = stubGateway.buscarVeiculoModelo(nomeCliente, modelo, hash);
		Veiculo v = vlist.get(0);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return vlist;
	}

	public List<Veiculo> listarVeiculos(String nomeCliente, String mensagem, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		List<Veiculo> vlist = stubGateway.listarVeiculos(nomeCliente, mensagem, hash);
		Veiculo v = vlist.get(0);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return vlist;
	}

	public List<Veiculo> listarVeiculosC(String nomeCliente, String categoria, String hash)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		List<Veiculo> vlist = stubGateway.listarVeiculosC(nomeCliente, categoria, hash);
		Veiculo v = vlist.get(0);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return vlist;
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
		Veiculo v = stubGateway.comprarVeiculo(nomeCliente, conta, veiculo, hashConta, hashVeiculo);
		if(v.getRenavam().equals("podebanir") && v.getAno() == null && v.getModelo()==null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return v;
	}

	public Conta fazerSaque(String nomeCliente, Conta conta, String valorSaque, String hashConta, String hashSaque)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		Conta c = stubGateway.fazerSaque(nomeCliente, conta, valorSaque, hashConta, hashSaque);
		if(c.getEmail().equals("podebanir") && c.getSenha() == null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return c;
	}

	public Conta fazerDeposito(String nomeCliente, Conta conta, String valorDeposito, String hashConta,
			String hashDeposito) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		Conta c = stubGateway.fazerDeposito(nomeCliente, conta, valorDeposito, hashConta, hashDeposito);
		if(c.getEmail().equals("podebanir") && c.getSenha() == null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return c;
	}

	public Conta fazerTransferencia(String nomeCliente, Conta contaBeneficente, String valorTransferencia,
			String emailFavorecido, String hashContaBene, String hashTransf, String hashEmailFavore)
			throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		Conta c = stubGateway.fazerTransferencia(nomeCliente, contaBeneficente, valorTransferencia, emailFavorecido,
				hashContaBene, hashTransf, hashEmailFavore);
		if(c.getEmail().equals("podebanir") && c.getSenha() == null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return c;
	}

	public Conta buscarConta(String nomeCliente, String emailConta, String hash) throws RemoteException, Exception {
		if (autentificarPermissao(RemoteServer.getClientHost())) {
			return null;
		}
		Conta c = stubGateway.buscarConta(nomeCliente, emailConta, hash);
		if(c.getEmail().equals("podebanir") && c.getSenha() == null) {
			System.out.println("Firewall -> Cliente " + RemoteServer.getClientHost() + " foi banido!");
			permissoes.add(new Permissao(RemoteServer.getClientHost(), this.porta, false));
			return null;
		}
		return c;
	}

	public boolean testarConexao() throws RemoteException, Exception {
		return true;
	}
}
