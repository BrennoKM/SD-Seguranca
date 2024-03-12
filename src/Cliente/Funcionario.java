package Cliente;

import java.rmi.RemoteException;

import Cifra.Cifrador;
import Modelos.Conta;
import Modelos.Veiculo;
import Modelos.Categorias.Economico;
import Modelos.Categorias.Executivo;
import Modelos.Categorias.Intermediario;
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
			System.out.println("Escolha uma opção: \n\t0 - Acessar conta bancária \n\t1 - Listar veículos \n\t2 - Listar veículos por categoria"
					+ "\n\t3 - Pesquisar veículo por renavam \n\t4 - Pesquisar veíuclo por modelo \n\t5 - Exibir quantidade total de veículos"
					+ "\n\t6 - Comprar veículo \n\t7 - Ver dados da minha conta \n\t8 - Adicionar veículo \n\t9 - Remover veículo "
					+ "\n\t10 - Alterar veículo \n\t11 - Sair");
			opcao = obterInt(1, 11);
			switch (opcao) {
			case 0:
				acessarBanco();
				break;
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
				adicionarVeiculo();
				break;
			case 9:
				removerVeiculo();
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

	private void removerVeiculo() throws Exception {
		System.out.println("Remoção de véiculo: \nDigite o renavam: ");
		String renavam = in.nextLine();
		Veiculo veiculo = new Economico(renavam);
		veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
		veiculo = stubGateway.removerVeiculo(this.nome, veiculo);
		if(veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Removido com sucesso: " + veiculo);
		}
		
	}

	private void adicionarVeiculo() throws RemoteException, Exception {
		Veiculo veiculo = criarVeiculo();
		if (veiculo != null) {
			veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
			veiculo = stubGateway.adicionarVeiculo(this.nome, veiculo);
		}
		if (veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Veículo inserido com sucesso: " + veiculo);
		} else {
			System.out.println("Falha na inserção");
		}

	}

	private Veiculo criarVeiculo() {
		System.out.println("Criação de véiculo: \nDigite o renavam: ");
		String renavam = in.nextLine();
		System.out.println("Digite o modelo: ");
		String modelo = in.nextLine();
		System.out.println("Digite o ano: ");
		String ano = obterString(1, 9999);
		System.out.println("Digite o preço: ");
		String preco = obterString(1, Integer.MAX_VALUE);
		System.out.println("Escolha a categoria: \n\t1 - Econômico \n\t2 - Intermediário \n\t3 - Executivo");
		int categoria = obterInt(1, 3);
		Veiculo veiculo = null;
		if (categoria == 1) {
			veiculo = new Economico(renavam, modelo, ano, preco);
		} else if (categoria == 2) {
			veiculo = new Intermediario(renavam, modelo, ano, preco);
		} else if (categoria == 3) {
			veiculo = new Executivo(renavam, modelo, ano, preco);
		}
		return veiculo;
	}
}
