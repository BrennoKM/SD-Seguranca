package Processo;

import Cliente.ClienteGateway;

public class Cliente {
	public Cliente(String nomeCliente, String host) throws Exception {
		new ClienteGateway(nomeCliente, host);
	}
}
