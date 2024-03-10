package Servidor;

import java.math.BigInteger;

import BancoDeDados.ContaManager;
import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorAutentificacao;

public class ImplServidorAutentificacao implements ServidorAutentificacao {
	private ContaManager bd_contas;
	private BigInteger chavePubRSA, chavePrivRSA;
	private String chaveAES = "chaveAESdobd1234", chaveAES_GateAuth = "chaveAESgateauth",
			mensagemPrivada = "EstouAutentificado";
	private Cifrador cifrador;

	public ImplServidorAutentificacao() throws Exception {
		this.bd_contas = new ContaManager(chaveAES);
		cifrador = new Cifrador(chaveAES_GateAuth);
	}

	public Conta fazerLogin(String mensagem, Conta conta) throws Exception {
//		System.out.println("Conta recebida auth: " + conta);
		conta = cifrador.descriptografar(chaveAES_GateAuth, conta);
//		System.out.println("Conta descrip auth: " + conta);
		if (cifrador.descriptografar(mensagem).equals(mensagemPrivada)) {
			String email = conta.getEmail();
			String senha = conta.getSenha();
			Conta contaBusca = bd_contas.buscarConta(email);
			if (contaBusca != null && contaBusca.getSenha().equals(senha)) {
				System.out.println("			Autentificação -> Logado com sucesso! " + contaBusca);
				contaBusca = cifrador.criptografar(cifrador.getChaveAES(), contaBusca);
				return contaBusca;
			}
			System.out.println("\t\t\tAutentificação -> Falha no Login! " + conta);
			return null;
		} else {
			System.out.println("\t\t\tAutentificação -> Cliente não autentificado!!");
		}
		return null;
	}

	public Conta fazerCadastro(String mensagem, Conta conta) throws Exception {
		if (cifrador.descriptografar(mensagem).equals(mensagemPrivada)) {
			conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			conta = bd_contas.adicionarConta(conta.getEmail(), conta);
			if (conta == null) {
				System.out.println("\t\t\tAutentificação -> Falha no cadastro! " + conta);
			}
			return conta;
		} else {
			System.out.println("\t\t\tAutentificação -> Cliente não autentificado!!");
		}
		return null;
	}

	public Conta removerConta(String mensagem, Conta conta) throws Exception {
		conta = fazerLogin(mensagem, conta);
		if (conta != null) {
			conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			bd_contas.removerConta(conta.getEmail());
			return null;
		}
		return conta;
	}

	public BigInteger getChavePubRSA() {
		return this.chavePubRSA;
	}

	public BigInteger getChavePrivRSA() {
		return this.chavePrivRSA;
	}

}
