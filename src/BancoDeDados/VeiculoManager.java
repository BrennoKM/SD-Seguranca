package BancoDeDados;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cifra.Cifrador;
import Modelos.Veiculo;
import Modelos.Veiculo.Categoria;
import Modelos.Categorias.Economico;
import Modelos.Categorias.Executivo;
import Modelos.Categorias.Intermediario;

public class VeiculoManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Veiculo> mapaVeiculos;
	private String arquivo = "veiculos.ser";
	//	private String chaveAES = "chaveAESdobd4321";
	private Cifrador cifrador;

	public VeiculoManager(String chaveAES, String arquivo) throws Exception {
		mapaVeiculos = new HashMap<>();
		this.arquivo = arquivo;
		cifrador = new Cifrador(chaveAES);
		carregarLista();

		// System.out.println(cifrador.descriptografar("g0XgXoe+stE7tsfrA8N1qQ=="));
		// String email = cifrador.descriptografar("/EojTzc2PV56n5igCGnEzA==");
		// System.out.println(email + " Variavel");
	}

	public synchronized Veiculo adicionarVeiculo(String renavam, Veiculo veiculo) throws Exception {

		Veiculo veiculoBusca = buscarVeiculoRenavam(renavam);
//		System.out.println("Veiculo adicionar>posbusca: " + veiculoBusca);
		if (veiculoBusca == null) {
			mapaVeiculos.put(renavam, veiculo);
			salvarLista();
			carregarLista();
			veiculoBusca = buscarVeiculoRenavam(renavam);
//			System.out.println("Veiculo adicionar>posbusca>posbuscaRenavam: " + veiculoBusca);
			return veiculoBusca;
		}
		return null;
	}

	public synchronized Veiculo buscarVeiculoRenavam(String renavam) throws Exception {
//		salvarLista(); // não salvar na busca
		carregarLista();
		if (mapaVeiculos.containsKey(renavam)) {
			Veiculo veiculo = mapaVeiculos.get(renavam);
			return veiculo;
		}
		return null;
	}

	public synchronized List<Veiculo> buscarVeiculoModelo(String modelo) throws Exception {
//		salvarLista(); // não salvar na busca
		carregarLista();
		List<Veiculo> veiculos = getVeiculos();
		for (int i = 0; i < veiculos.size(); i++) {
			if (!veiculos.get(i).getModelo().equals(modelo)) {
				veiculos.remove(i);
				i--;
			}
		}
		if (veiculos.size() > 1) {
			veiculos.sort(Comparator.comparing(Veiculo::getModelo));
			return veiculos;
		}
		return null;
	}

	public synchronized Veiculo removerVeiculo(String renavam) throws Exception {
		carregarLista();
		if (mapaVeiculos.containsKey(renavam)) {
//			System.out.println("Print veiculos na pré remoção");
//			for (Veiculo v : getVeiculos()) {
//				System.out.println(v);
//			}
			Veiculo veiculo = mapaVeiculos.remove(renavam);
//			System.out.println("Removendo: " + veiculo);
//			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
//		carregarLista();
			salvarLista();
			carregarLista();
			return veiculo;
		}
		return null;
	}

	public synchronized Veiculo atualizarVeiculo(String renavam, Veiculo novoVeiculo) throws Exception {
//		System.out.println("atualizar: " + novoVeiculo);
		if (mapaVeiculos.containsKey(renavam)) {
			removerVeiculo(renavam);
//			System.out.println("pos remoção: " + novoVeiculo);
			Veiculo veiculoAtualizado = adicionarVeiculo(novoVeiculo.getRenavam(), novoVeiculo);
//			System.out.println("veiculo pos adicionar: "+veiculoAtualizado);
//			carregarLista();
			salvarLista();
//			System.out.println("veiculo pos salvar: "+veiculoAtualizado);
			carregarLista();
//			System.out.println("veiculo pos carregar: "+veiculoAtualizado);
			veiculoAtualizado = buscarVeiculoRenavam(renavam);
			return veiculoAtualizado;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized void carregarLista() throws Exception {
		try (FileInputStream fileIn = new FileInputStream(arquivo);
			 ObjectInputStream in = new ObjectInputStream(fileIn)) {
			this.mapaVeiculos = (HashMap<String, Veiculo>) in.readObject();
			// Descriptografar e atualizar os veículos no mapa
			for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
				String renavam = entry.getKey();
				Veiculo veiculo = entry.getValue();
//	            mapaVeiculos.remove(renavam);
				Veiculo veiculoDescriptografado = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
				mapaVeiculos.put(renavam, veiculoDescriptografado); // Atualizar o veículo no mapa
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized void salvarLista() throws Exception {
		try (FileOutputStream fileOut = new FileOutputStream(arquivo);
			 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
			for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
				Veiculo veiculo = entry.getValue();
//				System.out.println("Salvando: " + veiculo);
				veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
			}
			out.writeObject(mapaVeiculos);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		descriptografarLista();
	}

	public synchronized void criptografarLista() throws Exception {
		for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
			Veiculo veiculo = entry.getValue();
			veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
		}
	}

	public synchronized void descriptografarLista() throws Exception {
		for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
			Veiculo veiculo = entry.getValue();
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
		}
	}

	public synchronized List<Veiculo> getVeiculos() throws Exception {
		carregarLista();
		List<Veiculo> veiculos = new ArrayList<>();
		for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
			veiculos.add(entry.getValue());
		}
		veiculos.sort(Comparator.comparing(Veiculo::getModelo));
		return veiculos;
	}

	public synchronized List<Veiculo> getVeiculos(Categoria categoria) throws Exception {
		List<Veiculo> veiculos = getVeiculos();
		for (int i = 0; i < veiculos.size(); i++) {
			Veiculo v = veiculos.get(i);
			if (v.getCategoria() != categoria) {
				veiculos.remove(i);
				i--;
			}
		}
		veiculos.sort(Comparator.comparing(Veiculo::getModelo));
		return veiculos;
	}

	public synchronized int getQntVeiculos() {
		return mapaVeiculos.size();
	}

	public synchronized Map<String, Veiculo> getMapaVeiculos() {
		return this.mapaVeiculos;
	}

	public static void main(String[] args) throws Exception {
		final String chaveAES = "chaveAESdobd4321";
		VeiculoManager manager = new VeiculoManager(chaveAES, "veiculos.ser");

		// Carregar lista do arquivo
		// manager.carregarLista();

		// Adicionar ou manipular contas
		manager.adicionarVeiculo("00011122233", new Economico("00011122233", "Fiat uno", "2024", "65000"));
		manager.adicionarVeiculo("00011122200", new Economico("00011122200", "Fiat uno", "2024", "61000"));
		manager.adicionarVeiculo("00011122201", new Economico("00011122201", "Fiat uno", "2024", "62000"));
		manager.adicionarVeiculo("00011122202", new Economico("00011122202", "Fiat uno", "2024", "64000"));
		manager.adicionarVeiculo("11122233344", new Economico("11122233344", "Chevrolet onix", "2024", "85000"));
		manager.adicionarVeiculo("12345678900", new Economico("12345678900", "Ford ka", "2024", "55000"));
		manager.adicionarVeiculo("12345678901", new Economico("12345678901", "Hyundai hb20", "2024", "64000"));
		manager.adicionarVeiculo("12345678902", new Economico("12345678902", "Nissan march", "2024", "45000"));
		manager.adicionarVeiculo("12345678903", new Economico("12345678903", "Chevrolet onix", "2024", "67000"));
		manager.adicionarVeiculo("12345678905", new Intermediario("12345678905", "Ford ka sedan", "2024", "61000"));
		manager.adicionarVeiculo("12345678906",
				new Intermediario("12345678906", "Chevrolet onix plus", "2024", "69000"));
		manager.adicionarVeiculo("12345678907", new Intermediario("12345678907", "Hyundai hb20s", "2024", "73000"));
		manager.adicionarVeiculo("12345678908", new Intermediario("12345678908", "Renault logan", "2024", "80000"));
		manager.adicionarVeiculo("12345678909", new Intermediario("12345678909", "Toyota etios", "2024", "81000"));
		manager.adicionarVeiculo("12345678910", new Executivo("12345678910", "Toyota corolla", "2024", "100000"));
		manager.adicionarVeiculo("12345678911", new Executivo("12345678911", "Honda civic", "2024", "120000"));
		manager.adicionarVeiculo("12345678912", new Executivo("12345678912", "Chevrolet cruze", "2024", "99000"));
		manager.adicionarVeiculo("12345678913", new Executivo("12345678913", "Audi a3.", "2024", "200000"));

//		 Salvar lista no arquivo
		manager.salvarLista();
//        manager.carregarLista();

		// Buscar conta pelo CPF
		Veiculo veiculo = manager.buscarVeiculoRenavam("12345678905");
		if (veiculo != null) {
			System.out.println("Veiculo encontrado: " + veiculo);
		} else {
			System.out.println("Veiculo não encontrado.");
		}
		System.out.println("");
		for (Veiculo v : manager.getVeiculos()) {
			System.out.println(v);
		}

//        System.out.println("");
//        for(Veiculo v : manager.getVeiculos()){
//        	System.out.println(v);
//        }

		System.out.println("\nVeiculos por categoria");
		for (Veiculo v : manager.getVeiculos(Categoria.EXECUTIVO)) {
			System.out.println(v);
		}

		System.out.println("\nVeiculos por modelo");
		for (Veiculo v : manager.buscarVeiculoModelo("Fiat uno")) {
			System.out.println(v);
		}

	}
}
