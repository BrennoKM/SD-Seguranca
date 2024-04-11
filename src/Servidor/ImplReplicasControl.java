package Servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Modelos.Veiculo;
import Modelos.Veiculo.Categoria;
import ServidorInterface.ServidorLoja;

public class ImplReplicasControl implements ServidorLoja {

	private boolean escrevendo = false;
	private int indiceLider = -1;
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	private Map<Integer, ServidorLoja> mapLojas = new HashMap<>();

	public ImplReplicasControl(String[] hostsLojas, int[] portasLojas) throws Exception {
		for (int i = 0; i < hostsLojas.length; i++) {
			final int index = i;
			Thread thread = new Thread(() -> {
				try {
					abrirServidorLoja(index, hostsLojas[index], portasLojas[index]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			thread.start();
		}

	}

	private static void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
	}

	private void abrirServidorLoja(int indice, String host, int porta) throws Exception {
//		porta = porta + 2;
		config(host);
		Scanner entrada = new Scanner(System.in);
		// System.out.println("Informe o endereço do serviço de autentificação: ");
		// String host = entrada.nextLine();
//		String host = "localhost";
		boolean conectou = false;
		while (!conectou) {
			try {
				Registry registro = LocateRegistry.getRegistry(host, porta);

				ServidorLoja stubLoja = (ServidorLoja) registro.lookup("rmi://" + host + "/ServidorLoja");
				mapLojas.put(indice, stubLoja);
				indices.add(indice);
				System.out.println("Loja adicionada " + indice);
				System.out.println(indices);
				conectou = true;
				entrada.close();
			} catch (RemoteException | NotBoundException e) {
				// e.printStackTrace();
				System.err.println("Falha ao se conectar com o servidor de veiculos. Tentando novamente em 4 segundos");
				Thread.sleep(4000);
			}
		}
	}

	private ServidorLoja elegerLider() {
		for (Map.Entry<Integer, ServidorLoja> entry : mapLojas.entrySet()) {
			int indice = entry.getKey();
			ServidorLoja stubLoja = entry.getValue();
			try {
				if (stubLoja.testarConexao()) {
					this.indiceLider = indice;
//					System.out.println("novo lider " + indice);
					return stubLoja;
				}
//				System.out.println("novo lider não encontrado, tentando novamente...");
				return elegerLider();
			} catch (Exception e) {
//				System.out.println("exception eleição");
				Iterator<Integer> iterator = indices.iterator();
				while (iterator.hasNext()) {
				    int i = iterator.next();
				    if (i == indice) {
				        iterator.remove();
				        mapLojas.remove(i);
				    }
				}
//				e.printStackTrace();
				return elegerLider();
				
			}

		}
		return null;
	}

	private ServidorLoja testarLoja(int indice) {
		try {
//			System.out.println("Testando " + indice);
			ServidorLoja stubLoja = mapLojas.get(indice);
			if (stubLoja != null && stubLoja.testarConexao()) {
//				System.out.println("loja funciona " + indice);
				return stubLoja;
			}
//			System.out.println("não funcionou");
			for(int i : indices) {
				if(i == indice) {
//					System.out.println("loja não funciona removendo " + indice);
					indices.remove(i);
					mapLojas.remove(i);
				}
			}
			if (indice == this.indiceLider) {
				this.indiceLider = -1;
				elegerLider();
			}
//			System.out.println("indo testar nova loja " + indices.get(0));
			return testarLoja(indices.get(0));
		} catch (Exception e) {
			Iterator<Integer> iterator = indices.iterator();
			while (iterator.hasNext()) {
			    int i = iterator.next();
			    if (i == indice) {
			        iterator.remove();
			        mapLojas.remove(i);
			    }
			}
			
			if (indice == this.indiceLider) {
				this.indiceLider = -1;
				elegerLider();
			}
//			e.printStackTrace();
			return testarLoja(indices.get(0));
			
		}
	}

	public ServidorLoja getStubRead() {
		for (int i : indices) {
			ServidorLoja stub = testarLoja(i);
			if (stub == null) {
				stub = getStubRead();
			}
			return stub;
		}
//		System.out.println("saindo getstubread");
		return null;
	}

	public ServidorLoja getStubWrite() {
		ServidorLoja stub = testarLoja(this.indiceLider);
		if (stub == null) {
			stub = elegerLider();
		}
		if (stub != null) {
//			System.out.println("achou o lider" + this.indiceLider);
			return stub;
		}
		return null;
	}

	public Veiculo buscarVeiculoPorRenavam(String mensagem, String renavam) throws Exception {
		return getStubRead().buscarVeiculoPorRenavam(mensagem, renavam);
	}

	public List<Veiculo> buscarVeiculoPorModelo(String mensagem, String modelo) throws Exception {
		return getStubRead().buscarVeiculoPorModelo(mensagem, modelo);
	}

	public List<Veiculo> getVeiculos(String mensagem) throws Exception {
//		System.out.println("getveic");
		return getStubRead().getVeiculos(mensagem);
	}

	public List<Veiculo> getVeiculos(String mensagem, Categoria categoria) throws RemoteException, Exception {
		return getStubRead().getVeiculos(mensagem, categoria);
	}

	public String getQntVeiculos(String mensagem) throws Exception {
		return getStubRead().getQntVeiculos(mensagem);
	}

	public Veiculo adicionarVeiculo(String mensagem, Veiculo veiculo) throws Exception {
//		System.out.println("tentando adicionar");
		if (this.escrevendo == true) {
			do {
			} while (this.escrevendo == true);
		}
		this.escrevendo = true;
//		System.out.println("chegou aqui");
		Veiculo v = getStubWrite().adicionarVeiculo(mensagem, veiculo);
//		System.out.println("pos add");
		if (v != null) {
			atualizarReplicas(mensagem, v);
		}
//		System.out.println("escrevendo falsew");
		this.escrevendo = false;
//		System.out.println("escrevendo false");
		return v;
	}

	public Veiculo atualizarVeiculo(String mensagem, Veiculo veiculo) throws Exception {
		if (this.escrevendo == true) {
			do {
			} while (this.escrevendo == true);
		}
		this.escrevendo = true;
		Veiculo v = getStubWrite().atualizarVeiculo(mensagem, veiculo);
		if (v != null) {
			atualizarReplicas(mensagem, v);
		}
		this.escrevendo = false;
		return v;
	}

	public Veiculo removerVeiculo(String mensagem, Veiculo veiculo) throws Exception {
		if (this.escrevendo == true) {
			do {
			} while (this.escrevendo == true);
		}
		this.escrevendo = true;
		Veiculo v = getStubWrite().removerVeiculo(mensagem, veiculo);
		if (v != null) {
			atualizarReplicas(mensagem, v, true);
		}
		this.escrevendo = false;
		return v;
	}

	public Veiculo atribuirDono(String mensagem, Veiculo veiculo, String emailDono) throws Exception {
		if (this.escrevendo == true) {
			do {
			} while (this.escrevendo == true);
		}
		this.escrevendo = true;
		Veiculo v = getStubWrite().atribuirDono(mensagem, veiculo, emailDono);
		if (v != null) {
			atualizarReplicas(mensagem, v);
		}
		this.escrevendo = false;
		return v;
	}

	public void atualizarReplicas(String mensagem, Veiculo veiculo) throws RemoteException, Exception {
		ArrayList<Integer> replicas = new ArrayList<Integer>(this.indices);
//		System.out.println(replicas);
//		replicas.remove(indiceLider);
//		System.out.println(replicas);
		for (int i : replicas) {
//			System.out.println("for att");
			if (i != this.indiceLider) {
				ServidorLoja stub = mapLojas.get(i);
				if (stub.atualizarVeiculo(mensagem, veiculo) == null) {
					stub.adicionarVeiculo(mensagem, veiculo);
				}
			}
		}
	}

	private void atualizarReplicas(String mensagem, Veiculo veiculo, boolean b) throws RemoteException, Exception {
		ArrayList<Integer> replicas = new ArrayList<Integer>(this.indices);
//		System.out.println(replicas);
//		replicas.remove(indiceLider);
//		System.out.println(replicas);
		for (int i : replicas) {
			if (i != this.indiceLider) {
				ServidorLoja stub = mapLojas.get(i);
//				System.out.println("removendo do indice " + i);
				stub.removerVeiculo(mensagem, veiculo);
			}
		}
	}

	public boolean testarConexao() throws RemoteException, Exception {
		elegerLider();
		return true;
	}

	public static void main(String[] args) throws Exception {
	}
}