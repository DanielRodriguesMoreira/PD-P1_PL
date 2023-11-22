package exercicio19;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteFileServiceInterface extends Remote {
    byte[] getFileChunk(String fileName, long offset) throws RemoteException, IOException;

    void getFile(String fileName, GetRemoteFileClientInterface cli) throws RemoteException, IOException;


    //Exercício 19
    void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException;

    void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
}
