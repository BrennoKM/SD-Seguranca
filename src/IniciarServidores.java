import Processo.Cliente;
import Processo.IniciarReplicasControl;
import Processo.IniciarServidorAutentificacao;
import Processo.IniciarServidorGateway;
import Processo.IniciarServidorLoja;

@SuppressWarnings("unused")
public class IniciarServidores{
	static String hostGateway = "localhost";
	static String hostAuth = "localhost";
	static String hostLoja = "localhost";
	static String hostReplicas = "localhost";
	static String[] hostsLojas = {"localhost", "localhost", "localhost"};
	static int[] portasLojas = {1099+3+0, 1099+3+1, 1099+3+2};
	static int portaInicial = 1099;

	public static void main(String[] args) throws Exception {

		// só pode ser iniciado uma vez
		// iniciar servidorGateway, servidorAutentificacao e servidorLoja respectivamente
		new IniciarServidorLoja(hostLoja, portaInicial+0, "veiculos.ser");
		new IniciarServidorLoja(hostLoja, portaInicial+1, "veiculos1.ser");
		new IniciarServidorLoja(hostLoja, portaInicial+2, "veiculos2.ser");

		new IniciarServidorAutentificacao(hostAuth, portaInicial);
		new IniciarReplicasControl(hostReplicas, hostsLojas, portasLojas, portaInicial);
		new IniciarServidorGateway(hostGateway, hostAuth, hostReplicas, portaInicial);

	}
}
