package Modelos.Categorias;

import Modelos.Veiculo;

public class Executivo extends Veiculo {
	private static final long serialVersionUID = 1L;

	public Executivo(String renavam, String modelo, String ano, String preco) {
		super(renavam, modelo, ano, preco, Categoria.EXECUTIVO);	
	}
	
	public Executivo(Veiculo veiculo) {
		super(veiculo, Categoria.EXECUTIVO);	
	}
}
