package Cliente;

import java.util.Scanner;

import Cifra.Cifrador;
import Modelos.Conta;
import ServidorInterface.ServidorGateway;

public class Funcionario {
	private String nome;
	private ServidorGateway stubGateway;
	private Cifrador cifrador;
	private Conta contaLogada = null;
	private Scanner in = new Scanner(System.in);

	public Funcionario(String nome, ServidorGateway stubGateway, Cifrador cifrador, Conta contaLogada) {
		System.out.println("Você é um funcionario");
		this.nome = nome;
		this.stubGateway = stubGateway;
		this.cifrador = cifrador;
		this.contaLogada = contaLogada;
	}
}
