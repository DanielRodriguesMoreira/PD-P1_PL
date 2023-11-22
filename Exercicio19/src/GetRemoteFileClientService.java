/*
 * Exemplo de utilizacao do servico com interface remota GetRemoteFileInterface.
 * Assume-se que o servico encontra-se registado sob o nome "servidor-ficheiros-pd".
 * Esta classe tambem implementa uma interface remota (GetRemoteFileClientInterface)
 * que deve incluir o metodo:
 *
 *       void writeFileChunk(byte [] fileChunk, int nbytes) throws java.io.IOException
 *
 */

package exercicio19;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GetRemoteFileClientService extends UnicastRemoteObject implements GetRemoteFileClientInterface {

    FileOutputStream fout = null;

    public GetRemoteFileClientService() throws RemoteException {
        fout = null;
    }

    public synchronized void setFout(FileOutputStream fout) {
        this.fout = fout;
    }

    @Override
    public void writeFileChunk(byte[] fileChunk, int nbytes) throws RemoteException, IOException {
        if (fout == null) {
            System.out.println("Nao existe qualquer ficheiro aberto para escrita!");
            throw new IOException("<CLI> Nao existe qualquer ficheiro aberto para escrita!");
        }

        try {
            fout.write(fileChunk, 0, nbytes);
        } catch (IOException e) {
            System.out.println("Excepcao ao escrever no ficheiro: " + e);
            throw new IOException("<CLI> Excepcao ao escrever no ficheiro", e.getCause());
        }
    }
}
