import Processo.Cliente;
import Processo.IniciarServidorAutentificacao;
import Processo.IniciarServidorGateway;
import Processo.IniciarServidorLoja;

@SuppressWarnings("unused")
public class iniciar {
	public static void main(String[] args) throws Exception {
		
		// só pode ser iniciado uma vez
		String hostGateway = "localhost";
		String hostAuth = "localhost";
		String hostLoja = "localhost";
		// iniciar servidorGateway, servidorAutentificacao e servidorLoja respectivamente
//		new IniciarServidorAutentificacao(hostAuth);
//		new IniciarServidorLoja(hostLoja);
//		new IniciarServidorGateway(hostGateway, hostAuth, hostLoja);
		
		
		// inserir clientes do gateway com nomes diferentes
		// host do servidorGateway
//		new Cliente("Kevyn", hostGateway);
	}
}
