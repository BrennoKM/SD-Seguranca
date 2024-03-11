import Modelos.Veiculo.Categoria;

public class classeTeste {
	public static void main(String[] args) {
		String categoria = "ECONÔMICO";
		Categoria c = null;
		if(categoria.equals(Categoria.ECONÔMICO.toString())) {
			c = Categoria.ECONÔMICO;
		} else if (categoria.equals(Categoria.INTERMEDIÁRIO.toString())){
			c = Categoria.INTERMEDIÁRIO;
		} else if (categoria.equals(Categoria.EXECUTIVO.toString())){
			c = Categoria.EXECUTIVO;
		}
		System.out.println(c.toString());
	}
}
