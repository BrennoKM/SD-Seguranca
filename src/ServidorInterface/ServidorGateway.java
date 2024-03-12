package ServidorInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Cifra.ChavePubModulo;
import Modelos.Conta;
import Modelos.Veiculo;

public interface ServidorGateway extends Remote{
	//auth
	Conta fazerLogin(String nomeCliente, Conta conta) throws RemoteException, Exception;
	Conta fazerCadastro(String nomeCliente,Conta conta) throws RemoteException, Exception;
	Conta removerConta(String nomeCliente,Conta conta) throws RemoteException, Exception;
	ChavePubModulo receberChavePubModulo(String nomeCliente) throws RemoteException, Exception;
	void enviarChaveAES(String nomeCliente, String chaveAEScriptograda)throws RemoteException, Exception;
	
	//loja
	Veiculo adicionarVeiculo(String nomeCliente, Veiculo veiculo) throws RemoteException, Exception;
	Veiculo removerVeiculo(String nomeCliente, Veiculo veiculo) throws RemoteException, Exception;
	Veiculo atualizarVeiculo(String nomeCliente, Veiculo veiculo) throws RemoteException, Exception;
	Veiculo buscarVeiculoRenavam(String nomeCliente, String renavam) throws RemoteException, Exception;
	List<Veiculo> buscarVeiculoModelo(String nomeCliente, String modelo) throws RemoteException, Exception;
	List<Veiculo> listarVeiculos(String nomeCliente) throws RemoteException, Exception;
	List<Veiculo> listarVeiculos(String nomeCliente, String categoria) throws RemoteException, Exception;
	String getQntVeiculo(String nomeCliente) throws RemoteException, Exception;
	Veiculo comprarVeiculo(String nomeCliente, Conta conta, Veiculo veiculo) throws RemoteException, Exception;
	
	//banco
	Conta fazerSaque(String nomeCliente, Conta conta, String valorSaque) throws RemoteException, Exception;
}
