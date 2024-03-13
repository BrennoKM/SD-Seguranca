package Servidor;

import java.util.List;

import BancoDeDados.VeiculoManager;
import Cifra.Cifrador;
import Modelos.Veiculo;
import Modelos.Categorias.Intermediario;
import Modelos.Veiculo.Categoria;
import ServidorInterface.ServidorLoja;

public class ImplServidorLoja implements ServidorLoja {
	private VeiculoManager bd_veiculos;
	private String chaveAESbd = "chaveAESdobd4321", chaveAES_GateLoja = "chaveAESgateloja",
			mensagemPrivada = "EstouAutentificado";
	private Cifrador cifrador;

	public ImplServidorLoja() throws Exception {
		this.bd_veiculos = new VeiculoManager(chaveAESbd);
		cifrador = new Cifrador(chaveAES_GateLoja);
	}

	private boolean autentificar(String mensagem) {
		if (mensagem.equals(mensagemPrivada)) {
			return true;
		} else {
			System.out.println("\t\t\tLoja -> Cliente não autentificado!!");
		}
		return false;
	}

	public Veiculo buscarVeiculoPorRenavam(String mensagem, String renavam) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		renavam = cifrador.descriptografar(renavam);
		if (autentificar(mensagem)) {
			Veiculo veiculo = bd_veiculos.buscarVeiculoRenavam(renavam);
			if (veiculo != null) {
				System.out.println("\t\t\tLoja -> Veiculo encontrado: " + veiculo);
				veiculo = cifrador.criptografar(chaveAES_GateLoja, veiculo);
				return veiculo;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo não encontrado!!");
		return null;
	}

	public List<Veiculo> buscarVeiculoPorModelo(String mensagem, String modelo) throws Exception {
		List<Veiculo> veiculos;
		mensagem = cifrador.descriptografar(mensagem);
		modelo = cifrador.descriptografar(modelo);
		if (autentificar(mensagem)) {
			veiculos = bd_veiculos.buscarVeiculoModelo(modelo);
			if (veiculos != null) {
				System.out.println("\t\t\tLoja -> Veiculo(s) encontrado(s): ");
				for (Veiculo veiculo : veiculos) {
					System.out.println("\t\t\t\t" + veiculo);
					veiculo = cifrador.criptografar(chaveAES_GateLoja, veiculo);
				}
				return veiculos;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo(s) não encontrado(s)!!");
		return null;
	}

	public List<Veiculo> getVeiculos(String mensagem) throws Exception {
		List<Veiculo> veiculos;
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			veiculos = bd_veiculos.getVeiculos();
			if (veiculos != null) {
				System.out.println("\t\t\tLoja -> Veiculo(s) encontrado(s): ");
				for (Veiculo veiculo : veiculos) {
					System.out.println("\t\t\t\t" + veiculo);
					veiculo = cifrador.criptografar(chaveAES_GateLoja, veiculo);
				}
				return veiculos;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo(s) não encontrado(s)!!");
		return null;
	}

	public List<Veiculo> getVeiculos(String mensagem, Categoria categoria) throws Exception {
		List<Veiculo> veiculos;
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			veiculos = bd_veiculos.getVeiculos(categoria);
			if (veiculos != null) {
				System.out.println("\t\t\tLoja -> Veiculo(s) encontrado(s) da categoria" + categoria.name() + ": ");
				for (Veiculo veiculo : veiculos) {
					System.out.println("\t\t\t\t" + veiculo);
					veiculo = cifrador.criptografar(chaveAES_GateLoja, veiculo);
				}
				return veiculos;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo(s) não encontrado(s)!!");
		return null;
	}

	public Veiculo adicionarVeiculo(String mensagem, Veiculo veiculo) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
			Veiculo veiculoAdicionado = bd_veiculos.adicionarVeiculo(veiculo.getRenavam(), veiculo);
			if (veiculoAdicionado != null) {
				System.out.println("\t\t\tLoja -> Veiculo adicionado: " + veiculoAdicionado);
				veiculoAdicionado = cifrador.criptografar(chaveAES_GateLoja, veiculoAdicionado);
				return veiculoAdicionado;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo não adicionado!!");
		return null;
	}

	public Veiculo atualizarVeiculo(String mensagem, Veiculo veiculo) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
			Veiculo veiculoAtualizado = bd_veiculos.atualizarVeiculo(veiculo.getRenavam(), veiculo);
			if (veiculoAtualizado != null) {
				System.out.println("\t\t\tLoja -> Veiculo atualizado: " + veiculoAtualizado);
				veiculoAtualizado = cifrador.criptografar(chaveAES_GateLoja, veiculoAtualizado);
				return veiculoAtualizado;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo não atualizado!!");
		return null;
	}

	public Veiculo removerVeiculo(String mensagem, Veiculo veiculo) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		
//		System.out.println("loja remoção crip: " + veiculo);
		if (autentificar(mensagem)) {
			veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
//			System.out.println("loja remoção descrip: " + veiculo);
//			System.out.println("renavam: " + veiculo.getRenavam());
			Veiculo veiculoRemovido = bd_veiculos.removerVeiculo(veiculo.getRenavam());
			if (veiculoRemovido != null) {
				System.out.println("\t\t\tLoja -> Veiculo removido: " + veiculoRemovido);
				veiculoRemovido = cifrador.criptografar(chaveAES_GateLoja, veiculoRemovido);
				return veiculoRemovido;
			}
		}
		System.out.println("\t\t\tLoja -> Veiculo não removido!!");
		return null;
	}

	public Veiculo atribuirDono(String mensagem, Veiculo veiculo, String emailDono) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			veiculo = cifrador.descriptografar(chaveAES_GateLoja, veiculo);
			emailDono = cifrador.descriptografar(emailDono);
			veiculo.setEmailDono(emailDono);
			
			
			Veiculo veiculoNovoDono = bd_veiculos.atualizarVeiculo(veiculo.getRenavam(), veiculo);
//			veiculoNovoDono = cifrador.descriptografar(chaveAES_GateLoja, veiculoNovoDono);

			if (veiculoNovoDono != null) {
				System.out.println("\t\t\tLoja -> Veiculo comprado: " + veiculoNovoDono);
				veiculoNovoDono = cifrador.criptografar(chaveAES_GateLoja, veiculoNovoDono);
				return veiculoNovoDono;
			}

		}
		System.out.println("\t\t\tLoja -> Veiculo não foi comprado!!");
		return null;
	}

	public String getQntVeiculos(String mensagem) throws Exception {
		mensagem = cifrador.descriptografar(mensagem);
		if (autentificar(mensagem)) {
			String qntVeiculos = Integer.toString(bd_veiculos.getQntVeiculos());
			System.out.println("\t\t\tLoja -> Quantidade de veiculos: " + qntVeiculos);
			qntVeiculos = cifrador.criptografar(chaveAES_GateLoja, qntVeiculos);
			return qntVeiculos;

		}
		System.out.println("\t\t\tLoja -> Falha total???!!");
		return null;
	}

	public static void main(String[] args) throws Exception {
		ImplServidorLoja isl = new ImplServidorLoja();
		Cifrador cifrador = new Cifrador("chaveAESgateloja");
		String mensagem = cifrador.criptografar("EstouAutentificado");
		String email = cifrador.criptografar("brennokm@gmail.com");
		String modelo = cifrador.criptografar("Fiat uno");
		Veiculo v = new Intermediario("12345678905", "Ford ka sedan", "2024", "61000");
		v = cifrador.criptografar(cifrador.getChaveAES(), v);
		isl.atribuirDono(mensagem, v, email);
		isl.buscarVeiculoPorModelo(mensagem, modelo);
	}
}
