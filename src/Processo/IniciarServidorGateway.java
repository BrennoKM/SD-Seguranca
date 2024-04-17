package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import Servidor.ImplServidorGateway;
import ServidorInterface.ServidorGateway;

public class IniciarServidorGateway {
	private int porta = 1099;

	public IniciarServidorGateway(String hostFirewall,String hostGateway, String hostAuth, String hostLoja, int porta) {
		this.porta = porta;
		
//		iniciarServidorAutentificacao(hostAuth);
//		iniciarServidorLoja(hostLoja);
		iniciarServidorGateway(hostFirewall, hostGateway, hostAuth, hostLoja, this.porta);
	}

	
	
	public void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}



	private void iniciarServidorGateway(String hostFirewall, String hostGate, String hostAuth, String hostReplicas, int porta) {
		config(hostGate);
		int modificadorPorta = 0;
		try {
			// criar objeto servidor
			ImplServidorGateway refObjetoRemoto = new ImplServidorGateway(hostGate, hostFirewall, hostAuth, hostReplicas, porta);
			ServidorGateway skeleton = (ServidorGateway) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
			
			Registry registro = LocateRegistry.createRegistry(porta + modificadorPorta);
			System.out.println("Registro: " + registro);
//			registro.rebind("ServidorGateway", skeleton);
			registro.rebind("rmi://" + hostGate + "/ServidorGateway", skeleton);
			System.out.println("Servidor de gateway está no ar. host=" + hostGate + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new IniciarServidorGateway("localhost","localhost", "localhost", "localhost", 1099);
	}
}
