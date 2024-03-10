package BancoDeDados;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import Cifra.Cifrador;
import Modelos.Conta;
import Modelos.Veiculo;
import Modelos.Categorias.Economico;
import Modelos.Categorias.Executivo;
import Modelos.Categorias.Intermediario;

public class VeiculoManager implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<String, Veiculo> mapaVeiculos;
    private final String arquivo = "veiculos.ser";
	private String chaveAES = "chaveAESdobd4321";
    private Cifrador cifrador;
    
    public VeiculoManager(String chaveAES) throws Exception {
    	mapaVeiculos = new HashMap<>();
		this.chaveAES = chaveAES;
		cifrador = new Cifrador(this.chaveAES);
		carregarLista();
		
		//System.out.println(cifrador.descriptografar("g0XgXoe+stE7tsfrA8N1qQ=="));
		//String email = cifrador.descriptografar("/EojTzc2PV56n5igCGnEzA==");
		//System.out.println(email + " Variavel");
    }
    
    public VeiculoManager() throws Exception {
    	mapaVeiculos = new HashMap<>();
    	cifrador = new Cifrador(this.chaveAES);
    	carregarLista();
    }

    public Veiculo adicionarVeiculo(String renavam, Veiculo veiculo) throws Exception {
    	Veiculo contaBusca = buscarVeiculo(renavam);
    	if(contaBusca == null) {
    		mapaVeiculos.put(renavam, veiculo);
    		return veiculo;
    	}
        return null;
    }

    public Veiculo buscarVeiculo(String renavam) throws Exception {
    	if (mapaVeiculos.containsKey(renavam)) {
    		Veiculo veiculo = mapaVeiculos.get(renavam);
	        return veiculo;
    	}
    	return null;
    }

    public void removerVeiculo(String renavam) throws Exception {
    	mapaVeiculos.remove(renavam);
        salvarLista();
    }

    public void atualizarVeiculo(String renavam, Veiculo novoVeiculo) throws Exception {
        if (mapaVeiculos.containsKey(renavam)) {
        	adicionarVeiculo(renavam, novoVeiculo);
        } else {
            System.out.println("Conta não encontrada.");
        }
    }
    
	
    
    @SuppressWarnings("unchecked")
	public void carregarLista() throws Exception {
        try (FileInputStream fileIn = new FileInputStream(arquivo);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
        	mapaVeiculos = (HashMap<String, Veiculo>) in.readObject();
            for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
            	Veiculo veiculo = entry.getValue();
            	veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void salvarLista() throws Exception {
        try (FileOutputStream fileOut = new FileOutputStream(arquivo);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
        	for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
        		Veiculo veiculo = entry.getValue();
        		veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
        		//conta.setEmail(cifrador.criptografar(conta.getEmail()));
            	//conta.setSenha(cifrador.criptografar(conta.getSenha()));
            }
            out.writeObject(mapaVeiculos);
            //System.out.println("Lista de contas salva em: " + new File(arquivo).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void printVeiculos() {
    	System.out.println("\n\n");
		for (Map.Entry<String, Veiculo> entry : mapaVeiculos.entrySet()) {
    		System.out.println(entry.getValue());
        }
		
	}

    public Map<String, Veiculo> getMapaVeiculos(){
    	return this.mapaVeiculos;
    }
    
    public static void main(String[] args) throws Exception {
		final String chaveAES = "chaveAESdobd1234";
		VeiculoManager manager = new VeiculoManager(chaveAES);
        
        // Carregar lista do arquivo
        //manager.carregarLista();

        // Adicionar ou manipular contas
        manager.adicionarVeiculo("00011122233", new Economico		("00011122233",	"Fiat uno",			"2024", "65000"));
        manager.adicionarVeiculo("11122233344", new Economico		("11122233344",	"Chevrolet onix",	"2024", "85000"));
        manager.adicionarVeiculo("12345678900", new Economico		("12345678900", "Ford ka", 			"2024", "85000"));
        manager.adicionarVeiculo("12345678901", new Economico		("12345678901", "Hyundai hb20",		"2024", "85000"));
        manager.adicionarVeiculo("12345678902", new Economico		("12345678902", "Nissan march",		"2024", "85000"));
        manager.adicionarVeiculo("12345678903", new Economico		("12345678903", "Chevrolet onix",	"2024", "85000"));
        manager.adicionarVeiculo("12345678905", new Intermediario	("12345678905", "Ford ka sedan",	"2024", "65000"));
        manager.adicionarVeiculo("12345678906", new Intermediario	("12345678906", "Chevrolet onix plus",	"2024", "65000"));
        manager.adicionarVeiculo("12345678907", new Intermediario	("12345678907", "Hyundai hb20s",	"2024", "65000"));
        manager.adicionarVeiculo("12345678908", new Intermediario	("12345678908", "Renault logan",	"2024", "65000"));
        manager.adicionarVeiculo("12345678909", new Intermediario	("12345678909", "Toyota etios",		"2024", "65000"));
        manager.adicionarVeiculo("12345678910", new Executivo		("", "Toyota corolla", "2024", "85000"));
        manager.adicionarVeiculo("12345678911", new Executivo		("", "Honda civic", "2024", "85000"));
        manager.adicionarVeiculo("12345678912", new Executivo		("", "Chevrolet cruze", "2024", "85000"));
        manager.adicionarVeiculo("12345678913", new Executivo		("", "Audi a3.", "2024", "85000"));
        //Conta contaBusca = manager.buscarConta("98765432109");
        //contaBusca.setSaldo(95);
        //manager.atualizarConta("98765432109", contaBusca);
        // Salvar lista no arquivo
        manager.salvarLista();
        manager.carregarLista();

        // Buscar conta pelo CPF
        Veiculo veiculo = manager.buscarVeiculo("Veiculo");
        if (veiculo != null) {
            System.out.println("Veiculo encontrado: " + veiculo);
        } else {
            System.out.println("Veiculo não encontrado.");
        }
        manager.printVeiculos();
    }
}



