package Modelos.Categorias;

import Modelos.Veiculo;

public class Economico extends Veiculo {
	private static final long serialVersionUID = 1L;

	public Economico(String renavam, String modelo, String ano, String preco) {
		super(renavam, modelo, ano, preco, Categoria.ECONÔMICO);	
	}
	
	public Economico(Veiculo veiculo) {
		super(veiculo, Categoria.ECONÔMICO);	
	}

	public Economico(String renavam) {
		super();
		this.setRenavam(renavam);
	}
}
