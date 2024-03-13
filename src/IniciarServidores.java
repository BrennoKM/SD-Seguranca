import Processo.Cliente;
import Processo.IniciarServidorAutentificacao;
import Processo.IniciarServidorGateway;
import Processo.IniciarServidorLoja;

@SuppressWarnings("unused")
public class IniciarServidores{
	static String hostGateway = "localhost";
	static String hostAuth = "localhost";
	static String hostLoja = "localhost";
	public static void main(String[] args) throws Exception {
		
		// só pode ser iniciado uma vez
		// iniciar servidorGateway, servidorAutentificacao e servidorLoja respectivamente
		new IniciarServidorAutentificacao(hostAuth);
		new IniciarServidorLoja(hostLoja);
		new IniciarServidorGateway(hostGateway, hostAuth, hostLoja);
		
	}
}
