package ServidorInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Modelos.Veiculo;
import Modelos.Veiculo.Categoria;

public interface ServidorLoja extends Remote {
	Veiculo buscarVeiculoPorRenavam(String mensagem, String renavam) throws RemoteException, Exception;
	List<Veiculo> buscarVeiculoPorModelo(String mensagem, String modelo) throws RemoteException, Exception;
	List<Veiculo> getVeiculos(String mensagem) throws RemoteException, Exception;
	List<Veiculo> getVeiculos(String mensagem, Categoria categoria) throws RemoteException, Exception;
	Veiculo adicionarVeiculo(String mensagem, Veiculo veiculo) throws RemoteException, Exception;
	Veiculo atualizarVeiculo(String mensagem, Veiculo veiculo) throws RemoteException, Exception;
	Veiculo removerVeiculo(String mensagem, Veiculo veiculo) throws RemoteException, Exception;
	Veiculo atribuirDono(String mensagem, Veiculo veiculo, String emailDono) throws RemoteException, Exception;
	String getQntVeiculos(String mensagem) throws RemoteException, Exception;
}
