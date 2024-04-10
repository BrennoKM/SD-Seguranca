package BancoDeDados;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import Cifra.Cifrador;
import Modelos.Conta;

public class ContaManager implements Serializable{
    private static final long serialVersionUID = 1L;
    private Map<String, Conta> mapaContas;
    private final String arquivo = "contas.ser";
    //	private String chaveAES = "chaveAESdobd1234";
    private Cifrador cifrador;

    public ContaManager(String chaveAES) throws Exception {
        mapaContas = new HashMap<>();
        cifrador = new Cifrador(chaveAES);
        carregarLista();
    }


    public synchronized Conta adicionarConta(String email, Conta conta) throws Exception {
        Conta contaBusca = buscarConta(email);
        if(contaBusca == null) {
            mapaContas.put(email, conta);
            salvarLista();
            carregarLista();
            contaBusca = buscarConta(email);
            return contaBusca;
        }
        return null;
    }

    public synchronized Conta buscarConta(String email) throws Exception {
//    	salvarLista(); // não salvar na busca
        carregarLista();
        if (mapaContas.containsKey(email)) {
            Conta conta = mapaContas.get(email);
            return conta;
        }
        return null;
    }

    public synchronized Conta removerConta(String email) throws Exception {
        carregarLista();
        if (mapaContas.containsKey(email)) {
            Conta contaRemovida = mapaContas.remove(email);
            salvarLista();
            carregarLista();
            return contaRemovida;
        }
        return null;

    }

    public synchronized Conta atualizarConta(String email, Conta conta) throws Exception {
        if (mapaContas.containsKey(email)) {
            removerConta(email);
            Conta contaAtualizada = adicionarConta(conta.getEmail(), conta);
            salvarLista();
            carregarLista();
            contaAtualizada = buscarConta(email);
            return contaAtualizada;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public synchronized void carregarLista() throws Exception {
        try (FileInputStream fileIn = new FileInputStream(arquivo);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            mapaContas = (HashMap<String, Conta>) in.readObject();
            for (Map.Entry<String, Conta> entry : mapaContas.entrySet()) {
                String numeroConta = entry.getKey();
                Conta conta = entry.getValue();
                Conta contaDescriptografada = cifrador.descriptografar(cifrador.getChaveAES(), conta);
                mapaContas.put(numeroConta, contaDescriptografada);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public synchronized void salvarLista() throws Exception {
        try (FileOutputStream fileOut = new FileOutputStream(arquivo);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            for (Map.Entry<String, Conta> entry : mapaContas.entrySet()) {
                Conta conta = entry.getValue();
                conta = cifrador.criptografar(cifrador.getChaveAES(), conta);
                //conta.setEmail(cifrador.criptografar(conta.getEmail()));
                //conta.setSenha(cifrador.criptografar(conta.getSenha()));
            }
            out.writeObject(mapaContas);
            //System.out.println("Lista de contas salva em: " + new File(arquivo).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        descriptografarLista();
    }

    public synchronized void criptografarLista() throws Exception {
        for (Map.Entry<String, Conta> entry : mapaContas.entrySet()) {
            Conta conta = entry.getValue();
            conta = cifrador.criptografar(cifrador.getChaveAES(), conta);
        }
    }

    public synchronized void descriptografarLista() throws Exception {
        for (Map.Entry<String, Conta> entry : mapaContas.entrySet()) {
            Conta conta = entry.getValue();
            conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
        }
    }

    public void printContas() {
        System.out.println("\n\n");
        for (Map.Entry<String, Conta> entry : mapaContas.entrySet()) {
            System.out.println("key: " + entry.getKey() +"	value -> "+ entry.getValue());
        }

    }

    public synchronized Map<String, Conta> getMapaContas(){
        return this.mapaContas;
    }

    public static void main(String[] args) throws Exception {
        final String chaveAES = "chaveAESdobd1234";
        ContaManager manager = new ContaManager(chaveAES);

        // Carregar lista do arquivo
        //manager.carregarLista();

        // Adicionar ou manipular contas
//        manager.adicionarConta("davi@gmail.com", new Conta("Davi", "12345678900", "bem ali do meu lado", "12345", "davi@gmail.com", "qwe1234", "50.0", "usuario"));
//        manager.adicionarConta("brennokm@gmail.com", new Conta("Brenno", "98765432100", "bem ali", "1234", "brennokm@gmail.com", "qwe123", "999999", "funcionario"));
//        manager.adicionarConta("icaro@gmail.com", new Conta("Icaro", "98765432109", "bem ali pertinho", "123466", "icaro@gmail.com", "qwe12"));
//      manager.removerConta("Sifu");  
//        Conta contaBusca = manager.buscarConta("a");
//        contaBusca.setSaldo("9599999");
//        contaBusca.setEmail("sifu");
//        contaBusca.setSenha("sifu");
//        contaBusca.setTipoConta("funcionario");
//        manager.atualizarConta("a", contaBusca);
        // Salvar lista no arquivo
        manager.salvarLista();
//        manager.carregarLista();

        // Buscar conta pelo CPF
        Conta conta = manager.buscarConta("icaro@gmail.com");
        if (conta != null) {
            System.out.println("Conta encontrada: " + conta);
        } else {
            System.out.println("Conta não encontrada.");
        }
        manager.printContas();
    }
}


