package Modelos.Categorias;

import Modelos.Veiculo;

public class Intermediario extends Veiculo {
	private static final long serialVersionUID = 1L;

	public Intermediario(String renavam, String modelo, String ano, String preco) {
		super(renavam, modelo, ano, preco, Categoria.INTERMEDIÁRIO);	
	}
	
	public Intermediario(Veiculo veiculo) {
		super(veiculo, Categoria.INTERMEDIÁRIO);	
	}
}
