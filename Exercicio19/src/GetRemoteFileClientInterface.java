package exercicio19;

import java.io.IOException;
import java.rmi.RemoteException;

public interface GetRemoteFileClientInterface extends java.rmi.Remote {
    void writeFileChunk(byte[] fileChunk, int nbytes) throws RemoteException, IOException;
}
