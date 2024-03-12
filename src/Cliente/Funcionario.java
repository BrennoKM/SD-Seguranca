package Cliente;

import java.rmi.RemoteException;

import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorGateway;

public class Funcionario extends Usuario {
//	private String nome;
//	private ServidorGateway stubGateway;
//	private Cifrador cifrador;
//	private Conta contaLogada = null;
//	private Scanner in = new Scanner(System.in);

	public Funcionario(String nome, ServidorGateway stubGateway, Cifrador cifrador, Conta contaLogada, String mensagem)
			throws RemoteException, Exception {
		super(nome, stubGateway, cifrador, contaLogada, mensagem);
	}

	public boolean iniciar() throws RemoteException, Exception {
		int opcao = 0;
		while (opcao != 11) {
			System.out.println("Escolha uma opção: \n\t1 - Listar veículos \n\t2 - Listar veículos por categoria"
					+ "\n\t3 - Pesquisar veículo por renavam \n\t4 - Pesquisar veíuclo por modelo \n\t5 - Exibir quantidade total de veículos"
					+ "\n\t1 - Comprar veículo \n\t7 - Ver dados da minha conta \n\t8 - Adicionar veículo \n\t9 - Remover veículo "
					+ "\n\t10 - Alterar veículo \n\t11 - Sair");
			opcao = obterOpcao(1, 11);
			switch (opcao) {
			case 1:
				listarVeiculos();
				break;
			case 2:
				listarVeiculosCategoria();
				break;
			case 3:
				buscarVeiculoRenavam();
				break;
			case 4:
				buscarVeiculoModelo();
				break;
			case 5:
				getQntVeiculos();
				break;
			case 6:
				comprarVeiculo();
				break;
			case 7:
				verMinhaconta();
				break;
			case 8:

				break;
			case 9:

				break;
			case 10:

				break;
			case 11:
				System.out.println("Deslogando...");
				break;

			}
		}
		return false;
	}
}
