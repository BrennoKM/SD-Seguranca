package Servidor;

import java.math.BigInteger;

import BancoDeDados.ContaManager;
import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorAutentificacao;

public class ImplServidorAutentificacao implements ServidorAutentificacao {
	private ContaManager bd_contas;
	private BigInteger chavePubRSA, chavePrivRSA;
	private String chaveAESbd = "chaveAESdobd1234", chaveAES_GateAuth = "chaveAESgateauth",
			mensagemPrivada = "EstouAutentificado";
	private Cifrador cifrador;

	public ImplServidorAutentificacao() throws Exception {
		this.bd_contas = new ContaManager(chaveAESbd);
		cifrador = new Cifrador(chaveAES_GateAuth);
	}
	
	private boolean autentificar(String mensagem) {
		if (mensagem.equals(mensagemPrivada)) {
			return true;
		} else {
			System.out.println("\t\t\tAutentificação -> Cliente não autentificado!!");
		}
		return false;
	}

	public Conta fazerLogin(String mensagem, Conta conta) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		conta = cifrador.descriptografar(chaveAES_GateAuth, conta);
		if (autentificar(mensagem)) {
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
		}
		return null;
	}

	public Conta fazerCadastro(String mensagem, Conta conta) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			Conta contaCadastrada = bd_contas.adicionarConta(conta.getEmail(), conta);
			if (contaCadastrada != null) {
				System.out.println("\t\t\tAutentificação -> Cadastrado com sucesso! " + contaCadastrada);
				contaCadastrada = cifrador.criptografar(cifrador.getChaveAES(), contaCadastrada);
//				bd_contas.salvarLista();
//				bd_contas.carregarLista();
				return contaCadastrada;
			}
			
		}
		System.out.println("\t\t\tAutentificação -> Falha no cadastro!");
		return null;
	}

	public Conta removerConta(String mensagem, Conta conta) throws Exception {
		conta = fazerLogin(mensagem, conta);
		if (conta != null) {
			conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			bd_contas.removerConta(conta.getEmail());
//			bd_contas.salvarLista();
//			bd_contas.carregarLista();
			return conta;
		}
		return null;
	}

	public BigInteger getChavePubRSA() {
		return this.chavePubRSA;
	}

	public BigInteger getChavePrivRSA() {
		return this.chavePrivRSA;
	}

}
