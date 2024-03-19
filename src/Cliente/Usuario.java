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
				if (opcaoInt < i || opcaoInt > j) {
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
				if (opcaoInt < i || opcaoInt > j) {
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
					+ "\n\t6 - Comprar veículo \n\t7 - Ver dados da minha conta \n\t8 - Sair \n\t0 - Acessar conta bancária");
			opcao = obterInt(0, 8);
			switch (opcao) {
			case 0:
				acessarBanco();
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

	protected void acessarBanco() throws Exception {
		boolean usandoBanco = true;
		while (usandoBanco) {
			System.out.println(
					"Opções do banco: \n\t1 - Fazer saque \n\t2 - Fazer depósito \n\t3 - Fazer uma transferência "
							+ "\n\t4 - Visualizar minha conta \n\t5 - Simular investimentos \n\t6 - Sair do banco");
			int opcaoBanco = obterInt(1, 6);
			switch (opcaoBanco) {
			case 1:
				fazerSaque();
				break;
			case 2:
				fazerDeposito();
				break;
			case 3:
				fazerTransferencia();
				break;
			case 4:
				verMinhaconta();
				break;
			case 5:
				simularInvestimentos();
				break;
			case 6:
				usandoBanco = false;
				break;
			}
		}

	}

	protected void fazerSaque() throws Exception {
		System.out.println("Digite o valor do seu saque: ");
		String valorSaque = obterString(1, Integer.MAX_VALUE);

		valorSaque = cifrador.criptografar(valorSaque);
		Conta conta = cifrador.criptografar(cifrador.getChaveAES(), new Conta(contaLogada));

		conta = stubGateway.fazerSaque(nome, conta, valorSaque);
		if (conta != null) {
			contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			System.out.println("Saque feito, novo saldo é: " + contaLogada.getSaldo());
		} else {
			System.out.println("Falha no saque");
		}
	}

	protected void fazerDeposito() throws Exception {
		System.out.println("Digite o valor do seu deposito: ");
		String valorDeposito = obterString(1, Integer.MAX_VALUE);

		valorDeposito = cifrador.criptografar(valorDeposito);
		Conta conta = cifrador.criptografar(cifrador.getChaveAES(), new Conta(contaLogada));

		conta = stubGateway.fazerDeposito(nome, conta, valorDeposito);
		if (conta != null) {
			contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), conta);
			System.out.println("Deposito feito, novo saldo é: " + contaLogada.getSaldo());
		} else {
			System.out.println("Falha no deposito");
		}
	}

	protected void fazerTransferencia() throws Exception {
		System.out.println("Digite o valor da sua transferência: ");
		String valorTransferencia = obterString(1, Integer.MAX_VALUE);

		System.out.println("Digite o email do favorecido: ");
		String emailFavorecido = in.nextLine();

		valorTransferencia = cifrador.criptografar(valorTransferencia);
		emailFavorecido = cifrador.criptografar(emailFavorecido);
		Conta contaBeneficente = cifrador.criptografar(cifrador.getChaveAES(), new Conta(contaLogada));

		contaBeneficente = stubGateway.fazerTransferencia(nome, contaBeneficente, valorTransferencia, emailFavorecido);
		if (contaBeneficente != null) {
			contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), contaBeneficente);
			System.out.println("Transferência feita, novo saldo é: " + contaLogada.getSaldo());
		} else {
			System.out.println("Falha na transferência");
		}
	}

	protected void verMinhaconta() throws RemoteException, Exception {
		String email = contaLogada.getEmail();
		email = cifrador.criptografar(email);
		Conta contaBusca = stubGateway.buscarConta(nome, email);
		if (contaBusca != null) {
			contaLogada = cifrador.descriptografar(cifrador.getChaveAES(), contaBusca);
			System.out.println("Meus dados: " + contaLogada);
		}

	}

	protected double calcularRendimento(double dinheiro, double taxa, int meses) {
		if (meses == 0) {
			return dinheiro;
		}
		return calcularRendimento(dinheiro * taxa, taxa, --meses);
	}

	protected void simularInvestimentos() {
		double saldoOriginal = Double.valueOf(contaLogada.getSaldo());
		double poupanca3 = calcularRendimento(saldoOriginal, 1.005, 3);
		double poupanca6 = calcularRendimento(poupanca3, 1.005, 3);
		double poupanca12 = calcularRendimento(poupanca6, 1.005, 3);

		double rendaFixa3 = calcularRendimento(saldoOriginal, 1.015, 3);
		double rendaFixa6 = calcularRendimento(rendaFixa3, 1.015, 3);
		double rendaFixa12 = calcularRendimento(rendaFixa6, 1.015, 3);

		String simulacao = String.format(
				"\n\tSaldo original: %.2f\n" + "\tPoupança após 3 meses: %.2f\n" + "\tPoupança após 6 meses: %.2f\n"
						+ "\tPoupança após 12 meses: %.2f\n\n" + "\tRenda fixa após 3 meses: %.2f\n"
						+ "\tRenda fixa após 6 meses: %.2f\n" + "\tRenda fixa após 12 meses: %.2f",
				saldoOriginal, poupanca3, poupanca6, poupanca12, rendaFixa3, rendaFixa6, rendaFixa12);

		System.out.println("Simulação de investimento: " + simulacao);
	}

	protected void getQntVeiculos() throws RemoteException, Exception {
		System.out.println(
				"Quantidade total de veículos: " + cifrador.descriptografar(stubGateway.getQntVeiculo(this.nome)));
	}

	protected void comprarVeiculo() throws RemoteException, Exception {
		System.out.println("Digite o renavam do veículo a ser comprado: ");
		String renavamCompra = in.nextLine();
		Veiculo veiculoCompra = new Economico(renavamCompra);
		Conta conta = cifrador.criptografar(cifrador.getChaveAES(), new Conta(contaLogada));
		veiculoCompra = cifrador.criptografar(cifrador.getChaveAES(), veiculoCompra);
		veiculoCompra = stubGateway.comprarVeiculo(this.nome, conta, veiculoCompra);
		if (veiculoCompra != null) {
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

	protected Veiculo buscarVeiculoRenavam() throws RemoteException, Exception {
		System.out.println("Digite o renavam: ");
		String renavam = in.nextLine();
		renavam = cifrador.criptografar(renavam);
		Veiculo veiculo = stubGateway.buscarVeiculoRenavam(this.nome, renavam);
		if (veiculo != null) {
			veiculo = cifrador.descriptografar(cifrador.getChaveAES(), veiculo);
			System.out.println("Veículos encontrado: " + veiculo);
			return veiculo;
		} else {
			System.out.println("Falha na busca");
		}
		return null;
	}

	protected void listarVeiculosCategoria() throws RemoteException, Exception {
		System.out.println("Escolha a categoria: \n\t1 - Econômico \n\t2 - Intermediário \n\t3 - Executivo");
		int categoria = obterInt(1, 3);
		String categoriaString = "ECONÔMICO";
		if (categoria == 1) {
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
