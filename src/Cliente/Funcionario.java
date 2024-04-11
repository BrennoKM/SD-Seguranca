package Cliente;

import java.rmi.RemoteException;

import Cifra.Cifrador;
import Modelos.Conta;
import Modelos.TokenInfo;
import Modelos.Veiculo;
import Modelos.Veiculo.Categoria;
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

	public Funcionario(String nome, ServidorGateway stubGateway, Cifrador cifrador, Conta contaLogada, String mensagem,
			TokenInfo tokenServidor) throws RemoteException, Exception {
		super(nome, stubGateway, cifrador, contaLogada, mensagem, tokenServidor);
	}

	public boolean iniciar() throws RemoteException, Exception {

		int opcao = 0;
		while (opcao != 11) {
			try {
				System.out.println("Escolha uma opção: \n\t1 - Listar veículos \n\t2 - Listar veículos por categoria"
						+ "\n\t3 - Pesquisar veículo por renavam \n\t4 - Pesquisar veíuclo por modelo \n\t5 - Exibir quantidade total de veículos"
						+ "\n\t6 - Comprar veículo \n\t7 - Ver dados da minha conta \n\t8 - Adicionar veículo \n\t9 - Remover veículo "
						+ "\n\t10 - Alterar veículo \n\t11 - Sair \n\t0 - Acessar conta bancária");
				opcao = obterInt(0, 11);
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
					alterarVeiculo();
					break;
				case 11:
					System.out.println("Deslogando...");
					break;

				}
			} catch (Exception e) {
				System.err.println("A sessão foi perdida!! :(");
//				e.printStackTrace();
			}
		}

		return false;
	}

	private void removerVeiculo() throws Exception {
		System.out.println("Remoção de véiculo: \nDigite o renavam: ");
		String renavam = in.nextLine();
		Veiculo veiculo = new Economico(renavam);
		veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
		String hashVeiculo = assinarMsg(veiculo.toString());
		veiculo = stubGateway.removerVeiculo(this.nome, veiculo, hashVeiculo);
		if (veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Removido com sucesso: " + veiculo);
		}

	}

	private void adicionarVeiculo() throws RemoteException, Exception {
		Veiculo veiculo = criarVeiculo();
		if (veiculo != null) {
			veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
			String hashVeiculo = assinarMsg(veiculo.toString());
			veiculo = stubGateway.adicionarVeiculo(this.nome, veiculo, hashVeiculo);
		}
		if (veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Veículo inserido com sucesso: " + veiculo);
		} else {
			System.out.println("Falha na inserção");
		}

	}

	private void alterarVeiculo() throws RemoteException, Exception {
		Veiculo veiculo = buscarVeiculoRenavam();
//		System.out.println("Alteração de véiculo: \nDigite o renavam do veículo a ser alterado: ");
		String renavamOriginal = veiculo.getRenavam();
		String renavam, modelo, ano, preco, emailDono;
		boolean modificando = true;
		if (veiculo != null) {
			while (modificando) {
				System.out.println("Quais atributos deseja mudar? " + veiculo);
				System.out.println(
						"1 - Renavam \n2 - Modelo \n3 - Ano \n4 - Preço \n5 - Categoria \n6 - Email do dono \n7 - Enviar alterações");
				int opcao = obterInt(1, 7);
				switch (opcao) {
				case 1:
					System.out.println("Digite o renavam: ");
					renavam = in.nextLine();
					veiculo.setRenavam(renavam);
					break;
				case 2:
					System.out.println("Digite o modelo: ");
					modelo = in.nextLine();
					veiculo.setModelo(modelo);
					break;
				case 3:
					System.out.println("Digite o ano: ");
					ano = obterString(1, Integer.MAX_VALUE);
					veiculo.setAno(ano);
					break;
				case 4:
					System.out.println("Digite o preço: ");
					preco = obterString(1, Integer.MAX_VALUE);
					veiculo.setPreco(preco);
					break;
				case 5:
					System.out.println("Escolha a categoria: \n1 - Econômico \n2 - Intermediário \n3 - Executivo");
					int categoria = obterInt(1, 3);
					if (categoria == 1) {
						veiculo.setCategoria(Categoria.ECONÔMICO);
					} else if (categoria == 2) {
						veiculo.setCategoria(Categoria.INTERMEDIÁRIO);
					} else if (categoria == 3) {
						veiculo.setCategoria(Categoria.EXECUTIVO);
					}
					break;
				case 6:
					System.out.println("Digite o email do novo dono (digite 'null' para apagar dono): ");
					emailDono = in.nextLine();
					if (emailDono.equals("null")) {
						veiculo.setEmailDono(null);
					} else {
						veiculo.setEmailDono(emailDono);
					}
					break;
				case 7:
					modificando = false;
					break;
				}

			}
			if (!veiculo.getRenavam().equals(renavamOriginal)) {
				Veiculo veiculoRemover = Veiculo.newVeiculo(veiculo);
				veiculoRemover.setRenavam(renavamOriginal);
				veiculoRemover = cifrador.criptografar(cifrador.getChaveAES(), veiculoRemover);
				String hashVeiculoRem = assinarMsg(veiculoRemover.toString());
				stubGateway.removerVeiculo(nome, veiculoRemover, hashVeiculoRem);
				veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
				String hashVeiculo = assinarMsg(veiculo.toString());
				veiculo = stubGateway.adicionarVeiculo(this.nome, veiculo, hashVeiculo);

			} else {
				veiculo = cifrador.criptografar(cifrador.getChaveAES(), veiculo);
				String hashVeiculo = assinarMsg(veiculo.toString());
				veiculo = stubGateway.atualizarVeiculo(this.nome, veiculo, hashVeiculo);
			}
		}
		if (veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Veículo atualizado com sucesso: " + veiculo);
		} else {
			System.out.println("Falha na atualização");
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
