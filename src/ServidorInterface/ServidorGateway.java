package ServidorInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Cifra.ChavePubModulo;
import Modelos.Conta;

public interface ServidorGateway extends Remote{
	Conta fazerLogin(String nomeCliente, Conta conta) throws RemoteException, Exception;
	Conta fazerCadastro(String nomeCliente,Conta conta) throws RemoteException, Exception;
	Conta removerConta(String nomeCliente,Conta conta) throws RemoteException, Exception;
	ChavePubModulo receberChavePubModulo(String nomeCliente) throws RemoteException, Exception;
	void enviarChaveAES(String nomeCliente, String chaveAEScriptograda)throws RemoteException, Exception;
}
