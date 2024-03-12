package Cliente;

import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Cifra.Cifrador;
import Modelos.Conta;
import Modelos.Veiculo;
import Modelos.Categorias.Economico;
import ServidorInterface.ServidorGateway;

public class Usuario {
	protected String nome;
	protected ServidorGateway stubGateway;
	protected Cifrador cifrador;
	protected Conta contaLogada = null;
	protected Scanner in = new Scanner(System.in);

	public Usuario(String nome, ServidorGateway stubGateway, Cifrador cifrador, Conta contaLogada, String mensagem)
			throws RemoteException, Exception {
		System.out.println(mensagem);
		this.nome = nome;
		this.stubGateway = stubGateway;
		this.cifrador = cifrador;
		this.contaLogada = contaLogada;
	}

	protected int obterInt(int i, int j) {
		int opcaoInt = 0;
		do {
			try {
				String opcaoString = in.nextLine();
				opcaoInt = Integer.valueOf(opcaoString);
				if(opcaoInt < i || opcaoInt > j){
					System.err.println("Digite um valor válido");
				}
			} catch (NumberFormatException | NoSuchElementException e) {
				System.err.println("Formato ou valor invalido");
			}
			
		} while (opcaoInt < i || opcaoInt > j);
		return opcaoInt;
	}
	
	protected String obterString(int i, int j) {
		int opcaoInt = 0;
		String opcaoString = null;
		do {
			try {
				opcaoString = in.nextLine();
				opcaoInt = Integer.valueOf(opcaoString);
				if(opcaoInt < i || opcaoInt > j){
					System.err.println("Digite um valor válido");
				}
			} catch (NumberFormatException | NoSuchElementException e) {
				System.err.println("Formato ou valor invalido");
			}
			
		} while (opcaoInt < i || opcaoInt > j);
		return opcaoString;
	}

	public boolean iniciar() throws RemoteException, Exception {
		int opcao = 0;
		while (opcao != 8) {
			System.out.println("Escolha uma opção: \n\t1 - Listar veículos \n\t2 - Listar veículos por categoria"
					+ "\n\t3 - Pesquisar veículo por renavam \n\t4 - Pesquisar veíuclo por modelo \n\t5 - Exibir quantidade total de veículos"
					+ "\n\t6 - Comprar veículo \n\t7 - Sair");
			opcao = obterInt(1, 8);
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
				System.out.println("Deslogando...");
				break;

			}
		}
		return false;
	}

	protected void verMinhaconta() {
		System.out.println("Meus dados: " + contaLogada);
		
	}

	protected void getQntVeiculos() throws RemoteException, Exception {
		System.out.println(cifrador.descriptografar(stubGateway.getQntVeiculo(this.nome)));
	}

	protected void comprarVeiculo() throws RemoteException, Exception {
		System.out.println("Digite o renavam do veículo a ser comprado: ");
		String renavamCompra = in.nextLine();
		Veiculo veiculoCompra = new Economico(renavamCompra);
		Conta conta = cifrador.criptografar(cifrador.getChaveAES(), contaLogada);
		veiculoCompra = cifrador.criptografar(cifrador.getChaveAES(), veiculoCompra);
		veiculoCompra = stubGateway.comprarVeiculo(this.nome, conta, veiculoCompra);
		if(veiculoCompra != null) {
			veiculoCompra = cifrador.descriptografar(cifrador.getChaveAES(), veiculoCompra);
			System.out.println("Veículo comprado com sucesso: " + veiculoCompra);
		} else {
			System.out.println("Falha na compra");
		}

	}

	protected void buscarVeiculoModelo() throws RemoteException, Exception {
		System.out.println("Digite o modelo: ");
		String modelo = in.nextLine();
		modelo = cifrador.criptografar(modelo);
		List<Veiculo> veiculos = stubGateway.buscarVeiculoModelo(this.nome, modelo);
		if (veiculos != null) {
			System.out.println("Veículos encontrados da categoria escolhida: ");
			for (Veiculo veiculo : veiculos) {
				veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
				System.out.println("\t" + veiculo);
			}
		} else {
			System.out.println("Falha na busca");
		}
	}

	protected void buscarVeiculoRenavam() throws RemoteException, Exception {
		System.out.println("Digite o renavam: ");
		String renavam = in.nextLine();
		renavam = cifrador.criptografar(renavam);
		Veiculo veiculo = stubGateway.buscarVeiculoRenavam(this.nome, renavam);
		if (veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Veículos encontrado: " + veiculo);
		} else {
			System.out.println("Falha na busca");
		}
	}

	protected void listarVeiculosCategoria() throws RemoteException, Exception {
		System.out.println("Escolha a categoria: \n\t1 - Econômico \n\t2 - Intermediário \n\t3 - Executivo");
		int categoria = obterInt(1, 3);
		String categoriaString = "ECONÔMICO";
		if(categoria == 1) {
			categoriaString = "ECONÔMICO";
		} else if (categoria == 2) {
			categoriaString = "INTERMEDIÁRIO";
		} else if (categoria == 3) {
			categoriaString = "EXECUTIVO";
		}
		String categoriaCifrada = cifrador.criptografar(categoriaString);
		List<Veiculo> veiculos = stubGateway.listarVeiculos(this.nome, categoriaCifrada);
		if (veiculos != null) {
			System.out.println("Veículos encontrados da categoria escolhida: ");
			for (Veiculo veiculo : veiculos) {
				veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
				System.out.println("\t" + veiculo);
			}
		} else {
			System.out.println("Falha na busca");
		}
	}

	protected void listarVeiculos() throws RemoteException, Exception {
		List<Veiculo> veiculos = stubGateway.listarVeiculos(this.nome);
		if (veiculos != null) {
			System.out.println("Veículos encontrados:");
			for (Veiculo veiculo : veiculos) {
				veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
				System.out.println("\t" + veiculo);
			}
		} else {
			System.out.println("Falha na busca");
		}

	}

}
