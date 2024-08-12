package Servidor;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Modelos.Veiculo;
import Modelos.Veiculo.Categoria;
import ServidorInterface.ServidorLoja;

public class ImplReplicasControl2 implements ServidorLoja {

	private boolean escrevendo = false;
	private int indiceLider = -1;
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	private Map<Integer, ServidorLoja> mapLojas = new HashMap<>();
	private ExecutorService executor;
	private int roundRobinIndex = 0;
	private SortedMap<Integer, Integer> anelMap = new TreeMap<>();
	private String[] hostsLojas;
	private int[] portasLojas;


	private static final int NUM_REPLICAS = 3; // Número de réplicas virtuais por loja

	public ImplReplicasControl2(String[] hostsLojas, int[] portasLojas) throws Exception {
		CyclicBarrier barreira = new CyclicBarrier(hostsLojas.length, () -> {
			System.out.println("\u001B[32mTodas as chaves: " + anelMap.keySet() + "\u001B[0m");
			elegerLider();
		}
		);
		executor = Executors.newFixedThreadPool(hostsLojas.length);
		this.hostsLojas = hostsLojas;
		this.portasLojas = portasLojas;
		for (int i = 0; i < hostsLojas.length; i++) {
			final int index = i;
			int indice = index;
			Runnable conectar = () -> {
				try {
					abrirServidorLoja(index, hostsLojas[index], portasLojas[index]);
					List<Integer> addedHashes = new ArrayList<>();
					synchronized (anelMap) {
						for (int j = 0; j < NUM_REPLICAS; j++) {
							int hash = hash(hostsLojas[indice] + ":" + portasLojas[indice] + ":" + j);
							anelMap.put(hash, indice);
							addedHashes.add(hash);
						}
					}
					System.out.println("\u001B[32mLoja adicionada " + index + " com Hashes: " + addedHashes + "\u001B[0m");
					await(barreira);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			executor.execute(conectar);

		}
	}

	private int hash(String key) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
			BigInteger bigInt = new BigInteger(1, hashBytes);
			return bigInt.mod(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	static void await(CyclicBarrier b){
		try {
			b.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
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
				System.out.println(stubLoja.testarConexao());
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
					return stubLoja;
				}
				return elegerLider();
			} catch (Exception e) {
//				e.printStackTrace();
//				System.out.println("\u001B[31mIndo remover loja: " + indice + "\u001B[0m");
//				removerServidor(indice);
				return elegerLider();
			}
		}
		return null;
	}

	private ServidorLoja testarLoja(int indice) {
		try {
			ServidorLoja stubLoja = mapLojas.get(indice);
			if (stubLoja != null && stubLoja.testarConexao()) {
				return stubLoja;
			}

			if (indice == this.indiceLider) {
				this.indiceLider = -1;
				elegerLider();
			}
			return testarLoja(indices.get(0));
		} catch (Exception e) {

			removerServidor(indice);
			if (indice == this.indiceLider) {
				this.indiceLider = -1;
				elegerLider();
			}
			return testarLoja(indices.get(0));
		}
	}

	private void removerServidor(int indice) {
		Iterator<Integer> iterator = indices.iterator();
		while (iterator.hasNext()) {
			int i = iterator.next();
			if (i == indice) {
				iterator.remove();
				mapLojas.remove(i);
				synchronized (anelMap) {
					Set<Map.Entry<Integer, Integer>> entrySet = anelMap.entrySet();
					List<Integer> keysToRemove = new ArrayList<>();
					entrySet.forEach(entry -> {
//						System.out.println("for each iteração: " + entry.getKey() + " " + entry.getValue());
						if (entry.getValue() == indice) {
							System.out.println("\u001B[31mRemovendo chave: " + entry.getKey() + " com valor: " + entry.getValue() + " \u001B[0m");
							keysToRemove.add(entry.getKey());
						}
					});
					keysToRemove.forEach(chave -> anelMap.remove(chave));
					System.out.println("\u001B[32mTodas as chaves: " + anelMap.keySet() + "\u001B[0m");
				}
			}
		}
	}


	// original
//	public ServidorLoja getStubRead(String key) {
//		for (int i : indices) {
//			ServidorLoja stub = testarLoja(i);
//			if (stub == null) {
//				stub = getStubRead(key);
//			}
//			return stub;
//		}
//		return null;
//	}

	// Random
//	public ServidorLoja getStubRead(String key) {
//		List<Integer> shuffledIndices = new ArrayList<>(indices);
//		Collections.shuffle(shuffledIndices);
//		for (int i : shuffledIndices) {
//			ServidorLoja stub = testarLoja(i);
//			if (stub != null) {
//				return stub;
//			}
//		}
//		return null;
//	}

	// rount robin
//	public ServidorLoja getStubRead(String key) {
//		int startIndex = (roundRobinIndex++) % indices.size();
//		for (int i = 0; i < indices.size(); i++) {
//			int currentIndex = (startIndex + i) % indices.size();
//			ServidorLoja stub = testarLoja(indices.get(currentIndex));
//			if (stub == null) {
//				stub = getStubRead(key);
//			}
//			return stub;
//		}
//		return null;
//	}

	// consistent hashing
	public ServidorLoja getStubRead(String key) {
		if (anelMap.isEmpty()) {
			return null;
		}
		int hash = hash(key);
		System.out.println("\u001B[32mCliente: " + key + " com Hash: " + hash +"\u001B[0m");

		SortedMap<Integer, Integer> tailMap = anelMap.tailMap(hash);
		int targetHash = tailMap.isEmpty() ? anelMap.firstKey() : tailMap.firstKey();
		int targetIndex = anelMap.get(targetHash);
		System.out.println("first key anel: " + anelMap.firstKey());
		System.out.println("first key tail: " + (tailMap.isEmpty() ? "Vazio" : tailMap.firstKey()));
		System.out.println("targetHash escolhido: " + targetHash);
		System.out.println("targetIndex escolhido: " + targetIndex);

		ServidorLoja stub = testarLoja(targetIndex);
		if (stub == null) {
			synchronized (anelMap) {
				anelMap.remove(targetHash);
			}
			return getStubRead(key);
		}
		return stub;
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

	public Veiculo buscarVeiculoPorRenavam(String ipCliente, String mensagem, String renavam) throws Exception {
		return getStubRead(ipCliente).buscarVeiculoPorRenavam(ipCliente, mensagem, renavam);
	}

	public List<Veiculo> buscarVeiculoPorModelo(String ipCliente, String mensagem, String modelo) throws Exception {

		List<Veiculo> veiculos = getStubRead(ipCliente).getVeiculos(ipCliente, mensagem);
		List<Veiculo> veiculosFiltrados = new ArrayList<>();

		// Usando função lambda para filtrar veículos ao inves de implementação dificil de ler (comparar com VeiculoManager.java)
		FiltroVeiculo filtro = veiculo -> veiculo.getModelo().equalsIgnoreCase(modelo);
		for (Veiculo veiculo : veiculos) {
			if (filtro.filtrar(veiculo)) {
				veiculosFiltrados.add(veiculo);
			}
		}

		return veiculosFiltrados;
	}

	public List<Veiculo> getVeiculos(String ipCliente, String mensagem) throws Exception {
//		System.out.println("getveic");
		return getStubRead(ipCliente).getVeiculos(ipCliente, mensagem);
	}

	public List<Veiculo> getVeiculos(String ipCliente, String mensagem, Categoria categoria) throws RemoteException, Exception {
		return getStubRead(ipCliente).getVeiculos(ipCliente, mensagem, categoria);
	}

	public String getQntVeiculos(String ipCliente, String mensagem) throws Exception {
		return getStubRead(ipCliente).getQntVeiculos(ipCliente, mensagem);
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

				AtualizacaoVeiculo atualizacao = (msg, v) -> {
					if (stub.atualizarVeiculo(msg, v) == null) {
						stub.adicionarVeiculo(msg, v);
					}
				};
				atualizacao.atualizar(mensagem, veiculo);

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

		return true;
	}

	public static void main(String[] args) throws Exception {
	}
}