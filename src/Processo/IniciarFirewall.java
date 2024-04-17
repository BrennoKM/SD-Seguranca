package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplFirewall;
import ServidorInterface.ServidorGateway;

public class IniciarFirewall {
	private int porta = 1099;

	public IniciarFirewall(String hostFirewall, String hostGateway, int porta) {
		this.porta = porta;

//		iniciarServidorAutentificacao(hostAuth);
//		iniciarServidorLoja(hostLoja);
		iniciarServidorFirewall(hostFirewall, hostGateway, this.porta);
	}

	public void config(String host) {
		System.setProperty("java.rmi.server.hostname", host);
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */

	}

	private void iniciarServidorFirewall(String hostFirewall, String hostGateway, int porta) {
		config(hostFirewall);
		int modificadorPorta = -1;
		try {
			// criar objeto servidor
			ImplFirewall refObjetoRemoto = new ImplFirewall(hostGateway, porta);
			ServidorGateway skeleton = (ServidorGateway) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
			
			Registry registro = LocateRegistry.createRegistry(porta + modificadorPorta);
			System.out.println("Registro: " + registro);
//			registro.rebind("ServidorGateway", skeleton);
			registro.rebind("rmi://" + hostFirewall + "/ServidorFirewall", skeleton);
			System.out.println("Servidor de Firewall está no ar. host=" + hostFirewall + " porta=" + (porta + modificadorPorta));
		} catch (Exception e) {
			System.err.println("Servidor: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
