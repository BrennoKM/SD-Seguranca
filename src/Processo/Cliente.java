package Processo;

import Cliente.ClienteGateway;

public class Cliente {
	public Cliente(String nomeCliente) throws Exception {
		new ClienteGateway(nomeCliente);
	}
}
