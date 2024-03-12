package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplServidorGateway;
import ServidorInterface.ServidorGateway;

public class IniciarServidorGateway {
	private static final int porta = 50005;

	public IniciarServidorGateway(String hostGateway, String hostAuth, String hostLoja) {

//		iniciarServidorAutentificacao(hostAuth);
//		iniciarServidorLoja(hostLoja);
		iniciarServidorGateway(hostGateway, hostAuth, hostLoja);
	}

	public void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}

	

	private void iniciarServidorGateway(String hostGate, String hostAuth, String hostLoja) {
		config(hostGate);
		int modificadorPorta = 0;
		try {
			// criar objeto servidor
			ImplServidorGateway refObjetoRemoto = new ImplServidorGateway(hostAuth, hostLoja);
			ServidorGateway skeleton = (ServidorGateway) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
			LocateRegistry.createRegistry(porta + modificadorPorta);
			Registry registro = LocateRegistry.getRegistry(porta + modificadorPorta);
			registro.bind("ServidorGateway", skeleton);
			System.out.println("Servidor de gateway está no ar. host=" + hostGate + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new IniciarServidorGateway("localhost", "localhost", "localhost");
	}
}
