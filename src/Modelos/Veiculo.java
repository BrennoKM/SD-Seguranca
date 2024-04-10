package Modelos;

import java.io.Serializable;

import Modelos.Categorias.Economico;
import Modelos.Categorias.Executivo;
import Modelos.Categorias.Intermediario;

public abstract class Veiculo implements Serializable {
	private static final long serialVersionUID = 1L;
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

	public Veiculo(Veiculo veiculo, Categoria categoria) {
		this.renavam = veiculo.getRenavam();
		this.modelo = veiculo.getModelo();
		this.ano = veiculo.getAno();
		this.preco = veiculo.getPreco();
		this.categoria = categoria;
	}

	public Veiculo(Veiculo veiculo) {
		this.renavam = veiculo.getRenavam();
		this.modelo = veiculo.getModelo();
		this.ano = veiculo.getAno();
		this.preco = veiculo.getPreco();
		this.categoria = veiculo.getCategoria();
	}

	public Veiculo() {

	}

	public static Veiculo newVeiculo(Veiculo veiculo) {
		Categoria categoria = veiculo.getCategoria();
		if (categoria == Categoria.INTERMEDIÁRIO) {
			return new Intermediario(veiculo);
		} else if (categoria == Categoria.ECONÔMICO) {
			return new Economico(veiculo);
		} else if (categoria == Categoria.EXECUTIVO) {
			return new Executivo(veiculo);
		}
		return null;
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
		return "Veiculo [renavam=" + renavam + ",	modelo=" + modelo + ",	ano=" + ano + ",	preco=" + preco
				+ ",	categoria=" + categoria + ",	emailDono=" + emailDono + "]";
	}

}
