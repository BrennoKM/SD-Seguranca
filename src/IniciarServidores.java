import Processo.Cliente;
import Processo.IniciarFirewall;
import Processo.IniciarReplicasControl;
import Processo.IniciarServidorAutentificacao;
import Processo.IniciarServidorGateway;
import Processo.IniciarServidorLoja;

@SuppressWarnings("unused")
public class IniciarServidores{
	static String host = "localhost";
	static String hostFirewall = "192.168.137.1";
	static String hostGateway = "192.168.137.1";
	static String hostAuth = "192.168.137.1";
//	static String hostLoja = "127.0.0.3";
	static String hostReplicas = "192.168.137.1";
	static String[] hostsLojas = {"192.168.137.1", "192.168.137.1", "192.168.137.1"};
	static int[] portasLojas = {1099+3+0, 1099+3+1, 1099+3+2};
	static int portaInicial = 1099;

	public static void main(String[] args) throws Exception {

		// só pode ser iniciado uma vez
		// iniciar servidorGateway, servidorAutentificacao e servidorLoja respectivamente
//		new IniciarServidorLoja(hostsLojas[0], portaInicial+0, "veiculos.ser");
//		new IniciarServidorLoja(hostsLojas[1], portaInicial+1, "veiculos1.ser");
//		new IniciarServidorLoja(hostsLojas[2], portaInicial+2, "veiculos2.ser");
//		new IniciarReplicasControl(hostReplicas, hostsLojas, portasLojas, portaInicial);

		new IniciarServidorAutentificacao(hostAuth, portaInicial);
		new IniciarServidorGateway(hostFirewall, hostGateway, hostAuth, hostReplicas, portaInicial);
		new IniciarFirewall(hostFirewall, hostGateway, portaInicial);


	}

}
