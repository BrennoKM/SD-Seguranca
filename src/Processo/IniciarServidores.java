package Processo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servidor.ImplServidorAutentificacao;
import Servidor.ImplServidorGateway;
import Servidor.ImplServidorLoja;
import ServidorInterface.ServidorAutentificacao;
import ServidorInterface.ServidorGateway;
import ServidorInterface.ServidorLoja;

public class IniciarServidores {
	private static final int porta = 50005;
	
	public static void config() {
		System.setProperty("java.rmi.server.hostname", "127.0.0.1");
		System.setProperty("java.security.policy", "java.policy");
		/*
		 * if (System.getSecurityManager() == null) { System.setSecurityManager(new
		 * SecurityManager()); }
		 */
		
	}
	public static void main(String[] args) {
		config();	
		iniciarServidorAutentificacao();
		iniciarServidorLoja();
		iniciarServidorGateway();
	}
	private static void iniciarServidorLoja() {
		int modificadorPorta = 2;
		try {
	        // criar objeto servidor
	        ImplServidorLoja refObjetoRemoto = new ImplServidorLoja();
	        ServidorLoja skeleton = (ServidorLoja) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
	        LocateRegistry.createRegistry(porta + modificadorPorta);
	        Registry registro = LocateRegistry.getRegistry(porta + modificadorPorta);
	        registro.bind("ServidorLoja", skeleton);
	        System.out.println("Servidor de veiculos está no ar.");
	    } catch (Exception e) {
	        System.err.println("Servidor: " + e.getMessage());
	        e.printStackTrace();
	    }
		
	}
	private static void iniciarServidorAutentificacao() {
		int modificadorPorta = 1;
		try {
	        // criar objeto servidor
	        ImplServidorAutentificacao refObjetoRemoto = new ImplServidorAutentificacao();
	        ServidorAutentificacao skeleton = (ServidorAutentificacao) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
	        LocateRegistry.createRegistry(porta + modificadorPorta);
	        Registry registro = LocateRegistry.getRegistry(porta + modificadorPorta);
	        registro.bind("ServidorAutentificacao", skeleton);
	        System.out.println("Servidor de autentificação está no ar.");
	    } catch (Exception e) {
	        System.err.println("Servidor: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	private static void iniciarServidorGateway() {
		int modificadorPorta = 0;
		try {
	        // criar objeto servidor
	        ImplServidorGateway refObjetoRemoto = new ImplServidorGateway();
	        ServidorGateway skeleton = (ServidorGateway) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
	        LocateRegistry.createRegistry(porta + modificadorPorta);
	        Registry registro = LocateRegistry.getRegistry(porta + modificadorPorta);
	        registro.bind("ServidorGateway", skeleton);
	        System.out.println("Servidor de gateway está no ar.");
	    } catch (Exception e) {
	        System.err.println("Servidor: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}
