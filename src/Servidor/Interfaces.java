package Servidor;

import Modelos.Veiculo;

import java.rmi.RemoteException;

@FunctionalInterface
interface AtualizacaoVeiculo {
    void atualizar(String mensagem, Veiculo veiculo) throws RemoteException, Exception;
}

@FunctionalInterface
interface FiltroVeiculo {
    boolean filtrar(Veiculo veiculo);
}
