package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplServidorLoja;
import ServidorInterface.ServidorLoja;

public class IniciarServidorLoja {

	private static final int porta = 50005;

	private void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}

	public IniciarServidorLoja(String hostLoja) {
		config(hostLoja);
		int modificadorPorta = 2;
		try {
			// criar objeto servidor
			ImplServidorLoja refObjetoRemoto = new ImplServidorLoja();
			ServidorLoja skeleton = (ServidorLoja) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
			LocateRegistry.createRegistry(porta + modificadorPorta);
			Registry registro = LocateRegistry.getRegistry(porta + modificadorPorta);
			registro.bind("ServidorLoja", skeleton);
			System.out
					.println("Servidor de veiculos está no ar. host=" + hostLoja + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
