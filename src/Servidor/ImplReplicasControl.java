package Servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Modelos.Veiculo;
import Modelos.Veiculo.Categoria;
import ServidorInterface.ServidorLoja;

public class ImplReplicasControl implements ServidorLoja {

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

    private static void config() {
        System.setProperty("java.security.policy", "java.policy");
        /*
         * if (System.getSecurityManager() == null) { System.setSecurityManager(new
         * SecurityManager()); }
         */
    }

    private void abrirServidorLoja(int indice, String host, int porta) throws Exception {
        porta = porta + 2;
        config();
        Scanner entrada = new Scanner(System.in);
        // System.out.println("Informe o endereço do serviço de autentificação: ");
        // String host = entrada.nextLine();
//		String host = "localhost";
        boolean conectou = false;
        while (!conectou) {
            try {
                Registry registro = LocateRegistry.getRegistry(host, porta);

                ServidorLoja stubLoja = (ServidorLoja) registro.lookup("ServidorLoja");
                mapLojas.put(indice, stubLoja);
                indices.add(indice);
                conectou = true;
                entrada.close();
            } catch (RemoteException | NotBoundException e) {
                // e.printStackTrace();
                System.err.println("Falha ao se conectar com o servidor de veiculos. Tentando novamente em 4 segundos");
                Thread.sleep(4000);
            }
        }
    }

    private void elegerLider() {
        for (Map.Entry<Integer, ServidorLoja> entry : mapLojas.entrySet()) {
            int indice = entry.getKey();
            ServidorLoja stubLoja = entry.getValue();
            try {
                if (stubLoja.testarConexao()) {
                    this.indiceLider = indice;
                    break;
                }
            } catch (Exception e) {
                indices.remove(indice);
                System.err.println(e);
            }

        }
    }

    private ServidorLoja testarLoja(int indice) {
        try {
            ServidorLoja stubLoja = mapLojas.get(indice);
            if(stubLoja.testarConexao()) {
                return stubLoja;
            }
        } catch (Exception e) {
            indices.remove(indice);
            if(indice == this.indiceLider) {
                this.indiceLider = -1;
            }
            System.err.println(e);
        }
        return null;
    }

    public ServidorLoja getStubRead() {
        for(Integer i : indices) {
            ServidorLoja stub = testarLoja(i);
            if(stub != null) {
                return stub;
            }
        }
        return null;
    }

    public Veiculo buscarVeiculoPorRenavam(String mensagem, String renavam) throws Exception {
        return getStubRead().buscarVeiculoPorRenavam(mensagem, renavam);
    }

    public List<Veiculo> buscarVeiculoPorModelo(String mensagem, String modelo) throws Exception {
        return null;
    }

    public List<Veiculo> getVeiculos(String mensagem) throws Exception {
        return null;
    }

    public List<Veiculo> getVeiculos(String mensagem, Categoria categoria) {
        return null;
    }

    public Veiculo adicionarVeiculo(String mensagem, Veiculo veiculo) throws Exception {
        return null;
    }

    public Veiculo atualizarVeiculo(String mensagem, Veiculo veiculo) throws Exception {
        return null;
    }

    public Veiculo removerVeiculo(String mensagem, Veiculo veiculo) throws Exception {
        return null;
    }

    public Veiculo atribuirDono(String mensagem, Veiculo veiculo, String emailDono) throws Exception {

        return null;
    }

    public String getQntVeiculos(String mensagem) throws Exception {
        return mensagem;
    }

    public boolean testarConexao() throws RemoteException, Exception {
        return true;
    }

    public static void main(String[] args) throws Exception {
    }
}