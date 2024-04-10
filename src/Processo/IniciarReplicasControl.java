package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplReplicasControl;
import ServidorInterface.ServidorLoja;

public class IniciarReplicasControl {
    private int porta = 1099;

    private void config(String host) {
        System.setProperty("java.rmi.server.hostname", host);
        System.setProperty("java.security.policy", "java.policy");
        /*
         * if (System.getSecurityManager() == null) { System.setSecurityManager(new
         * SecurityManager()); }
         */

    }

    public IniciarReplicasControl(String hostReplicas, String[] hostsLojas, int[] portasLojas, int porta) {
        this.porta = porta;
        config(hostReplicas);
        int modificadorPorta = 2;
        try {
            // criar objeto servidor
            ImplReplicasControl refObjetoRemoto = new ImplReplicasControl(hostsLojas, portasLojas);
            ServidorLoja skeleton = (ServidorLoja) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
            LocateRegistry.createRegistry(this.porta + modificadorPorta);
            Registry registro = LocateRegistry.getRegistry(this.porta + modificadorPorta);
            System.out.println("Registro: " + registro);
            registro.bind("ReplicasControl", skeleton);
            System.out
                    .println("Servidor de Replicas está no ar. host=" + hostReplicas + " porta=" + (porta + modificadorPorta));
        } catch (Exception e) {
            System.err.println("Servidor: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

