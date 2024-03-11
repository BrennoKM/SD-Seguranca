package Cifra;

import java.util.Random;

import Modelos.Conta;
import Modelos.Veiculo;

public class Cifrador {
	private String chaveAES;
	private RSA rsa;
	private String chavePub, modulo;

	public Cifrador(String chaveAES) {
		this.chaveAES = chaveAES;
		gerarRSA();
	}

	public Cifrador() {

	}

	public void gerarRSA() {
		rsa = new RSA();
		rsa.gerarChaves(1024);
		chavePub = rsa.getChavePub();
		modulo = rsa.getModulo();
	}

	public String criptografar(String conteudo) throws Exception {
		String cripAES = CifraAES.criptografar(this.chaveAES, conteudo);
		return cripAES;
	}

	public String descriptografar(String conteudo) throws Exception {
		String descripAES = CifraAES.descriptografar(chaveAES, conteudo);
		return descripAES;
	}

	public String criptografar(String chaveAES, String conteudo) throws Exception {
		String cripAES = CifraAES.criptografar(chaveAES, conteudo);
		return cripAES;
	}

	public String descriptografar(String chaveAES, String conteudo) throws Exception {
		String descripAES = CifraAES.descriptografar(chaveAES, conteudo);
		return descripAES;
	}

	public String criptografarRSA(String mensagem) {
		return rsa.criptografar(mensagem);
	}

	public String criptografarRSA(String mensagem, String chavePub, String modulo) {
		return RSA.criptografar(mensagem, chavePub, modulo);
	}

	public String descriptografarRSA(String mensagem, String chavePri, String modulo) {
		return RSA.descriptografar(mensagem, chavePri, modulo);
	}

	public String criptografarRSA(String mensagem, ChavePubModulo chavePubModulo) {
		return RSA.criptografar(mensagem, chavePubModulo.getChavePub(), chavePubModulo.getModulo());
	}

	public String getChaveAES() {
		return this.chaveAES;
	}

	public String getChavePub() {
		return this.chavePub;
	}

	public String getModulo() {
		return this.modulo;
	}

	public String getChavePri() {
		return rsa.getChavePri();
	}

	public Conta criptografar(String chaveAES, Conta conta) throws Exception {
		if (conta != null) {
			conta.setEmail(criptografar(chaveAES, conta.getEmail()));
			conta.setSenha(criptografar(chaveAES, conta.getSenha()));

			if (conta.getNome() != null) {
				conta.setNome(criptografar(chaveAES, conta.getNome()));
			}
			if (conta.getCpf() != null) {
				conta.setCpf(criptografar(chaveAES, conta.getCpf()));
			}
			if (conta.getEndereco() != null) {
				conta.setEndereco(criptografar(chaveAES, conta.getEndereco()));
			}
			if (conta.getSaldo() != null) {
				double saldoDouble = Double.parseDouble(conta.getSaldo());
				String saldoString = Double.toString(saldoDouble);
				conta.setSaldo(saldoString);
				conta.setSaldo(criptografar(chaveAES, conta.getSaldo()));
			}
			if (conta.getTelefone() != null) {
				conta.setTelefone(criptografar(chaveAES, conta.getTelefone()));
			}
			if (conta.getTipoConta() != null) {
				conta.setTipoConta(criptografar(chaveAES, conta.getTipoConta()));
			}
			return conta;
		}
		return null;
	}

	public Conta descriptografar(String chaveAES, Conta conta) throws Exception {
		if (conta != null) {
			conta.setEmail(descriptografar(chaveAES, conta.getEmail()));
			conta.setSenha(descriptografar(chaveAES, conta.getSenha()));

			if (conta.getNome() != null) {
				conta.setNome(descriptografar(chaveAES, conta.getNome()));
			}
			if (conta.getCpf() != null) {
				conta.setCpf(descriptografar(chaveAES, conta.getCpf()));
			}
			if (conta.getEndereco() != null) {
				conta.setEndereco(descriptografar(chaveAES, conta.getEndereco()));
			}
			if (conta.getSaldo() != null) {
				conta.setSaldo(descriptografar(chaveAES, conta.getSaldo()));
			} else {
				double saldoDouble = Double.parseDouble("0");
				String saldoString = Double.toString(saldoDouble);
				conta.setSaldo(saldoString);
			}
			if (conta.getTelefone() != null) {
				conta.setTelefone(descriptografar(chaveAES, conta.getTelefone()));
			}
			if (conta.getTipoConta() != null) {
				conta.setTipoConta(descriptografar(chaveAES, conta.getTipoConta()));
			} else {
				conta.setTipoConta("usuario");
			}
			return conta;
		}
		return null;
	}

	public Veiculo criptografar(String chaveAES, Veiculo veiculo) throws Exception {
//		System.out.println("Veiculo pra criptografar: " + veiculo);
		if (veiculo != null) {
			veiculo.setRenavam(criptografar(chaveAES, veiculo.getRenavam()));
			
			if (veiculo.getModelo() != null) {
				veiculo.setModelo(criptografar(chaveAES, veiculo.getModelo()));
			}
			if (veiculo.getAno() != null) {
				int anoInt = Integer.parseInt(veiculo.getAno());
				String anoString = Integer.toString(anoInt);
				veiculo.setAno(anoString);
				veiculo.setAno(criptografar(chaveAES, veiculo.getAno()));
			}
			if (veiculo.getPreco() != null) {
				double precoDouble = Double.parseDouble(veiculo.getPreco());
				String precoString = Double.toString(precoDouble);
				veiculo.setPreco(precoString);
				veiculo.setPreco(criptografar(chaveAES, veiculo.getPreco()));
			}
			if (veiculo.getEmailDono() != null) {
				veiculo.setEmailDono(criptografar(chaveAES, veiculo.getEmailDono()));
			}
			return veiculo;
		}
		return null;
	}

	public Veiculo descriptografar(String chaveAES, Veiculo veiculo) throws Exception {
//		System.out.println("Veiculo pra descriptografar: " + veiculo);
		if (veiculo != null) {
			veiculo.setRenavam(descriptografar(chaveAES, veiculo.getRenavam()));
			
			if (veiculo.getModelo() != null) {
				veiculo.setModelo(descriptografar(chaveAES, veiculo.getModelo()));
			}
			if (veiculo.getAno() != null) {
				veiculo.setAno(descriptografar(chaveAES, veiculo.getAno()));
			}
			if (veiculo.getPreco() != null) {
				veiculo.setPreco(descriptografar(chaveAES, veiculo.getPreco()));
			} else {
				double precoDouble = Double.parseDouble("80000");
				String precoString = Double.toString(precoDouble);
				veiculo.setPreco(precoString);
			}
			if (veiculo.getEmailDono() != null) {
				veiculo.setEmailDono(descriptografar(chaveAES, veiculo.getEmailDono()));
			}
			return veiculo;
		}
		return null;
	}

	public ChavePubModulo getChavePubModulo() {
		ChavePubModulo cpm = new ChavePubModulo(getChavePub(), getModulo());
		return cpm;
	}

	public static String gerarStringAleatoria(int tamanho) {
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();

		Random random = new Random();
		for (int i = 0; i < tamanho; i++) {
			int index = random.nextInt(caracteres.length());
			sb.append(caracteres.charAt(index));
		}

		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		Cifrador c = new Cifrador();
		String mensagem = "f9GPpeK9TpfPUdNSBPdtt3Pe2qywugzViOey/JZxnGI=";
		System.out.println(c.descriptografar("chaveAESgateauth", mensagem));
	}

}
