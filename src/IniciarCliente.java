import Processo.Cliente;

public class IniciarCliente extends IniciarServidores{

	public static void main(String[] args) throws Exception {
		// inserir clientes do gateway com nomes diferentes
		// host do servidorGateway
		String nome = hostsLojas[0] + ":" + (portaInicial+2) + ":" + 0;
//		new Cliente("Kevyn", hostFirewall, portaInicial-1);
		new Cliente("Edivaldo", hostFirewall, portaInicial-1);
	}
}
