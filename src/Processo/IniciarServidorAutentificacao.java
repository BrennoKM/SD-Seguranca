package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplServidorAutentificacao;
import ServidorInterface.ServidorAutentificacao;

public class IniciarServidorAutentificacao {
	private static final int porta = 50005;
	
	private void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}
	
	public IniciarServidorAutentificacao(String hostAuth) {
		config(hostAuth);
		int modificadorPorta = 1;
		try {
			// criar objeto servidor
			ImplServidorAutentificacao refObjetoRemoto = new ImplServidorAutentificacao();
			ServidorAutentificacao skeleton = (ServidorAutentificacao) UnicastRemoteObject.exportObject(refObjetoRemoto,
					0);
			LocateRegistry.createRegistry(porta + modificadorPorta);
			Registry registro = LocateRegistry.getRegistry(porta + modificadorPorta);
			registro.bind("ServidorAutentificacao", skeleton);
			System.out.println(
					"Servidor de autentificação está no ar. host=" + hostAuth + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
