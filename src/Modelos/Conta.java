package Modelos;

import java.io.Serializable;


public class Conta implements Serializable{
	private static final long serialVersionUID = 1L;
	String nome, cpf, endereco, telefone, email, senha, tipoConta;
	String saldo;

	public Conta(String email, String senha) {
		this.email = email;
		this.senha = senha;

	}

	public Conta(String nome, String cpf, String endereco, String telefone, String email, String senha) {
		this.nome = nome;
		this.cpf = cpf;
		this.endereco = endereco;
		this.telefone = telefone;
		this.email = email;
		this.senha = senha;

	}

	public Conta(String nome, String cpf, String endereco, String telefone, String email, String senha, String saldo, String tipoConta) {
		this.nome = nome;
		this.cpf = cpf;
		this.endereco = endereco;
		this.telefone = telefone;
		this.email = email;
		this.senha = senha;
		this.saldo = saldo;
		this.tipoConta = tipoConta;

	}

	public Conta(Conta conta) {
		this.nome = conta.getNome();
		this.cpf = conta.getCpf();
		this.endereco = conta.getEndereco();
		this.telefone = conta.getTelefone();
		this.email = conta.getEmail();
		this.senha = conta.getSenha();
		this.saldo = conta.getSaldo();
		this.tipoConta = conta.getTipoConta();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	public String getTipoConta() {
		return this.tipoConta;
	}

	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}

	public String toString() {
		return "Conta[" +
				"nome=" + this.nome +
				", cpf=" + this.cpf +
				", email=" + this.email +
				", senha=" + this.senha +
				", endereco=" + this.endereco +
				", telefone=" + this.telefone +
				", saldo=" + this.saldo +
				", tipoConta=" +this.tipoConta +
				']';
	}

	public static void main(String[] args) {
		String saldo = "0";
		double suporte = Double.parseDouble(saldo);
		saldo = Double.toString(suporte);
		System.out.println(saldo +"    "+ suporte);
	}
}
