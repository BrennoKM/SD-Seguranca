package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplServidorLoja;
import ServidorInterface.ServidorLoja;

public class IniciarServidorLoja {

	private int porta = 1099;

	private void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}

	public IniciarServidorLoja(String hostLoja, int porta) {
		this.porta = porta;
		config(hostLoja);
		int modificadorPorta = 2;
		try {
			// criar objeto servidor
			ImplServidorLoja refObjetoRemoto = new ImplServidorLoja();
			ServidorLoja skeleton = (ServidorLoja) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
			LocateRegistry.createRegistry(this.porta + modificadorPorta);
			Registry registro = LocateRegistry.getRegistry(this.porta + modificadorPorta);
			registro.bind("ServidorLoja", skeleton);
			System.out
					.println("Servidor de veiculos está no ar. host=" + hostLoja + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
