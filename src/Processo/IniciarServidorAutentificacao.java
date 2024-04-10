package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplServidorAutentificacao;
import ServidorInterface.ServidorAutentificacao;

public class IniciarServidorAutentificacao {
	private int porta = 1099;

	private void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}

	public IniciarServidorAutentificacao(String hostAuth, int porta) {
		this.porta = porta;
		config(hostAuth);
		int modificadorPorta = 1;
		try {
			// criar objeto servidor
			ImplServidorAutentificacao refObjetoRemoto = new ImplServidorAutentificacao();
			ServidorAutentificacao skeleton = (ServidorAutentificacao) UnicastRemoteObject.exportObject(refObjetoRemoto,
					0);
			LocateRegistry.createRegistry(this.porta + modificadorPorta);
			Registry registro = LocateRegistry.getRegistry(this.porta + modificadorPorta);
			System.out.println("Registro: " + registro);
			registro.bind("ServidorAutentificacao", skeleton);
			System.out.println(
					"Servidor de autentificação está no ar. host=" + hostAuth + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
