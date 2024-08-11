import Processo.Cliente;
import Processo.IniciarFirewall;
import Processo.IniciarReplicasControl;
import Processo.IniciarServidorAutentificacao;
import Processo.IniciarServidorGateway;
import Processo.IniciarServidorLoja;

@SuppressWarnings("unused")
public class IniciarServidores{
	static String host = "localhost";
	static String hostFirewall = "127.0.0.1";
	static String hostGateway = "127.0.0.2";
	static String hostAuth = "127.0.0.2";
	static String hostLoja = "127.0.0.2";
	static String hostReplicas = "127.0.0.2";
	static String[] hostsLojas = {"127.0.0.2", "127.0.0.2", "127.0.0.2"};
	static int[] portasLojas = {1099+3+0, 1099+3+1, 1099+3+2};
	static int portaInicial = 1099;

	public static void main(String[] args) throws Exception {

		// só pode ser iniciado uma vez
		// iniciar servidorGateway, servidorAutentificacao e servidorLoja respectivamente
//		new IniciarServidorLoja(hostLoja, portaInicial+0, "veiculos.ser");
//		new IniciarServidorLoja(hostLoja, portaInicial+1, "veiculos1.ser");
//		new IniciarServidorLoja(hostLoja, portaInicial+2, "veiculos2.ser");

		new IniciarServidorAutentificacao(hostAuth, portaInicial);
		new IniciarReplicasControl(hostReplicas, hostsLojas, portasLojas, portaInicial);
		new IniciarServidorGateway(hostFirewall, hostGateway, hostAuth, hostReplicas, portaInicial);
		new IniciarFirewall(hostFirewall, hostGateway, portaInicial);

	}
}
