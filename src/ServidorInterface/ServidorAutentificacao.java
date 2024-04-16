package ServidorInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import Modelos.Conta;

public interface ServidorAutentificacao extends Remote {
	Conta fazerLogin(String mensagem,Conta conta) throws RemoteException, Exception;
	Conta fazerCadastro(String mensagem,Conta conta) throws RemoteException, Exception;
	Conta buscarConta(String mensagem, String email) throws RemoteException, Exception;
	Conta removerConta(String mensagem,Conta conta) throws RemoteException, Exception;
	Conta atualizarConta(String mensagem, Conta conta) throws RemoteException, Exception;
	
	String xerarDados() throws RemoteException, Exception;
}
