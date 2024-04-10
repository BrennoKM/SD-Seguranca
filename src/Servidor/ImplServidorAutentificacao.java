package Servidor;

import BancoDeDados.ContaManager;
import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorAutentificacao;

public class ImplServidorAutentificacao implements ServidorAutentificacao {
	private ContaManager bd_contas;
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
		if (autentificar(mensagem)) {
			conta = cifrador.descriptografar(chaveAES_GateAuth, conta);
			String email = conta.getEmail();
			String senha = conta.getSenha();
			Conta contaBusca = bd_contas.buscarConta(email);
			if (contaBusca != null && contaBusca.getSenha().equals(senha)) {
				System.out.println("\t\t\tAutentificação -> Logado com sucesso! " + contaBusca);
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
//		System.out.println("Conta recebida cad: " + conta);
		if (autentificar(mensagem)) {
			conta = cifrador.descriptografar(chaveAES_GateAuth, conta);
//			System.out.println("Conta recebida cad descri: " + conta);
			Conta contaCadastrada = bd_contas.adicionarConta(conta.getEmail(), conta);
			if (contaCadastrada != null) {
				System.out.println("\t\t\tAutentificação -> Cadastrado com sucesso! " + contaCadastrada);
				contaCadastrada = cifrador.criptografar(chaveAES_GateAuth, contaCadastrada);
//				bd_contas.salvarLista();
//				bd_contas.carregarLista();
				return contaCadastrada;
			}

		}
		System.out.println("\t\t\tAutentificação -> Falha no cadastro!");
		return null;
	}

	public Conta buscarConta(String mensagem, String email) throws Exception {
//		System.out.println("mensagem authrecebida: "+ mensagem);
		mensagem = cifrador.descriptografar(mensagem);
//		System.out.println("mensagem descrip: "+ mensagem);
		if (autentificar(mensagem)) {
			email = cifrador.descriptografar(cifrador.getChaveAES(), email);
//			System.out.println("email descrip busca conta implauth: "+ email);
			Conta contaBusca = bd_contas.buscarConta(email);
			if (contaBusca != null) {
				System.out.println("\t\t\tAutentificação -> Conta encontrada: " + contaBusca);
				contaBusca = cifrador.criptografar(cifrador.getChaveAES(), contaBusca);
//				bd_contas.salvarLista();
//				bd_contas.carregarLista();
				return contaBusca;
			}

		}
		System.out.println("\t\t\tAutentificação -> Falha na busca!");
		return null;
	}

	public Conta atualizarConta(String mensagem, Conta conta) throws Exception {
//		System.out.println("mensagem authrecebida: "+ mensagem);
		mensagem = cifrador.descriptografar(mensagem);
//		System.out.println("mensagem descrip: "+ mensagem);
		if (autentificar(mensagem)) {
			conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			Conta contaAtualizada = bd_contas.atualizarConta(conta.getEmail(), conta);
			if (contaAtualizada != null) {
				System.out.println("\t\t\tAutentificação -> Conta atualizada: " + contaAtualizada);
				contaAtualizada = cifrador.criptografar(cifrador.getChaveAES(), contaAtualizada);
//				bd_contas.salvarLista();
//				bd_contas.carregarLista();
				return contaAtualizada;
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


	public static void main(String[] args) throws Exception {
		ImplServidorAutentificacao isa = new ImplServidorAutentificacao();
		Cifrador cifrador = new Cifrador("chaveAESgateauth");
		String mensagem = cifrador.criptografar("EstouAutentificado");
		String email = cifrador.criptografar("brennokm@gmail.com");

		Conta conta = isa.buscarConta(mensagem, email);
		System.out.println(conta);
		conta = cifrador.descriptografar(cifrador.getChaveAES(), conta);
		System.out.println(conta);
	}

}
