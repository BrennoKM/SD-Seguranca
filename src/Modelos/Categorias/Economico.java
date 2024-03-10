package Modelos.Categorias;

import Modelos.Veiculo;

public class Economico extends Veiculo {
	public Economico(String renavam, String modelo, String ano, String preco) {
		super(renavam, modelo, ano, preco, Categoria.ECONÔMICO);	
	}
}
