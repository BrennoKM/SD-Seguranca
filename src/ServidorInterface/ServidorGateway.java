package ServidorInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Cifra.ChavesModulo;
import Modelos.Conta;
import Modelos.Veiculo;

public interface ServidorGateway extends Remote{
	//auth
	Conta fazerLogin(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception;
	Conta fazerCadastro(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception;
	Conta removerConta(String nomeCliente, Conta conta, String hash) throws RemoteException, Exception;
	
	ChavesModulo receberChavePubModulo(String nomeCliente) throws RemoteException, Exception;
	void enviarChaveAES(String nomeCliente, String chaveAEScriptograda) throws RemoteException, Exception;
	void enviarChaveHmac(String nome, String chaveHmac) throws RemoteException, Exception;
	void enviarChavePubModulo(String nomeCliente, ChavesModulo chavePubModulo) throws RemoteException, Exception;
	
	//loja
	Veiculo adicionarVeiculo(String nomeCliente, Veiculo veiculo, String hash) throws RemoteException, Exception;
	Veiculo removerVeiculo(String nomeCliente, Veiculo veiculo, String hash) throws RemoteException, Exception;
	Veiculo atualizarVeiculo(String nomeCliente, Veiculo veiculo, String hash) throws RemoteException, Exception;
	Veiculo buscarVeiculoRenavam(String nomeCliente, String renavam, String hash) throws RemoteException, Exception;
	List<Veiculo> buscarVeiculoModelo(String nomeCliente, String modelo, String hash) throws RemoteException, Exception;
	List<Veiculo> listarVeiculos(String nomeCliente, String mensagem, String hash) throws RemoteException, Exception;
	List<Veiculo> listarVeiculosC(String nomeCliente, String categoria, String hash) throws RemoteException, Exception;
	String getQntVeiculo(String nomeCliente, String mensagem, String hash) throws RemoteException, Exception;
	Veiculo comprarVeiculo(String nomeCliente, Conta conta, Veiculo veiculo, String hashConta, String hashVeiculo) throws RemoteException, Exception;
	
	//banco
	Conta fazerSaque(String nomeCliente, Conta conta, String valorSaque, String hashConta, String hashSaque) throws RemoteException, Exception;
	Conta fazerDeposito(String nomeCliente, Conta conta, String valorDeposito, String hashConta, String hashDeposito) throws RemoteException, Exception;
	Conta fazerTransferencia(String nomeCliente, Conta contaBeneficente, String valorTransferencia, String emailFavorecido, String hashContaBene, String hashTransf, String hashEmailFavore) throws RemoteException, Exception;
	Conta buscarConta(String nomeCliente, String emailConta, String hash) throws RemoteException, Exception;
	
}
