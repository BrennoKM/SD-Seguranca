import Processo.Cliente;
import Processo.IniciarServidorAutentificacao;
import Processo.IniciarServidorGateway;
import Processo.IniciarServidorLoja;

@SuppressWarnings("unused")
public class IniciarServidores{
	static String hostGateway = "localhost";
	static String hostAuth = "localhost";
	static String hostLoja = "localhost";
	static int portaInicial = 1099;
	
	public static void main(String[] args) throws Exception {
		
		// só pode ser iniciado uma vez
		// iniciar servidorGateway, servidorAutentificacao e servidorLoja respectivamente
		new IniciarServidorAutentificacao(hostAuth, portaInicial);
		new IniciarServidorLoja(hostLoja, portaInicial);
		new IniciarServidorGateway(hostGateway, hostAuth, hostLoja, portaInicial);
		
	}
}
