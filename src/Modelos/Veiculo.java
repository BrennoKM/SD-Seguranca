package Modelos;

public abstract class Veiculo {
	private String renavam, modelo, ano, preco, emailDono;
	private Categoria categoria;

	public enum Categoria {
		ECONÔMICO, INTERMEDIÁRIO, EXECUTIVO
	}

	public Veiculo(String renavam, String modelo, String ano, String preco, Categoria categoria) {
		this.renavam = renavam;
		this.modelo = modelo;
		this.ano = ano;
		this.preco = preco;
		this.categoria = categoria;
	}

	public String getRenavam() {
		return renavam;
	}

	public void setRenavam(String renavam) {
		this.renavam = renavam;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getEmailDono() {
		return emailDono;
	}

	public void setEmailDono(String emailDono) {
		this.emailDono = emailDono;
	}

	public String toString() {
		return "Veiculo [renavam=" + renavam + ", modelo=" + modelo + ", ano=" + ano + ", preco=" + preco
				+ ", categoria=" + categoria + ", emailDono=" + emailDono + "]";
	}
	
}
