import Processo.Cliente;
import Processo.IniciarServidores;

@SuppressWarnings("unused")
public class iniciar {
	public static void main(String[] args) throws Exception {
		
		// só precisa ser iniciado uma vez
//		new IniciarServidores();
		
		
		// inserir clientes do gateway com nomes diferentes
		new Cliente("Kevyn", "localhost");
	}
}
