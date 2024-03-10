package Modelos.Categorias;

import Modelos.Veiculo;

public class Intermediario extends Veiculo {
	public Intermediario(String renavam, String modelo, String ano, String preco) {
		super(renavam, modelo, ano, preco, Categoria.INTERMEDIÁRIO);	
	}
}
