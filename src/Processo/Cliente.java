package Processo;

import Cliente.ClienteGateway;

public class Cliente {
	public Cliente(String nomeCliente, String host, int porta) throws Exception {
		new ClienteGateway(nomeCliente, host, porta);
	}
}
