package Modelos.Categorias;

import Modelos.Veiculo;

public class Executivo extends Veiculo {
	public Executivo(String renavam, String modelo, String ano, String preco) {
		super(renavam, modelo, ano, preco, Categoria.EXECUTIVO);	
	}
}
