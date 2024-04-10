import Processo.Cliente;

public class IniciarCliente extends IniciarServidores{
	public static void main(String[] args) throws Exception {
		// inserir clientes do gateway com nomes diferentes
		// host do servidorGateway
		new Cliente("Arthur", "192.168.0.21", portaInicial);
	}
}
