package Servidor;

import java.math.BigInteger;

import BancoDeDados.VeiculoManager;
import Cifra.Cifrador;
import Modelos.Veiculo;
import ServidorInterface.ServidorLoja;

public class ImplServidorLoja implements ServidorLoja{
	private VeiculoManager bd_veiculos;
	private BigInteger chavePubRSA, chavePrivRSA;
	private String chaveAES = "chaveAESdobd4321", chaveAES_GateLoja = "chaveAESgateloja",
			mensagemPrivada = "EstouAutentificado";
	private Cifrador cifrador;
	
	public ImplServidorLoja() throws Exception {
		this.bd_veiculos = new VeiculoManager(chaveAES);
		cifrador = new Cifrador(chaveAES_GateLoja);
	}
	
	public Veiculo buscarVeiculoPorRenavam(String mensagem, String renavam) throws Exception {
		if (cifrador.descriptografar(mensagem).equals(mensagemPrivada)) {
			
		} else {
			System.out.println("\t\t\tLoja -> Cliente não autentificado!!");
		}
		return null;
	}
}
