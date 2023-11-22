package exercicio19;

import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;

public class GetRemoteFileObserver extends UnicastRemoteObject implements GetRemoteFileObserverInterface {

    public GetRemoteFileObserver() throws RemoteException {
    }

    public void notifyNewOperationConcluded(String description) throws RemoteException {
        System.out.println("-> " + description);
        System.out.println();
    }

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("Deve passar 1 argumentos na linha de comando:");
                System.out.println("    1 - endereco do RMI registry onde esta' registado o servico remoto de download de ficheiros");
                System.exit(1);
            }
            //Localiza o servico remoto nomeado "GetRemoteFile"
            String objectUrl = "rmi://" + args[0] + "/servidor-ficheiros-pd";
            GetRemoteFileServiceInterface getRemoteFileService = (GetRemoteFileServiceInterface) Naming.lookup(objectUrl);

            //Cria e lanca o servico
            GetRemoteFileObserver observer = new GetRemoteFileObserver();
            System.out.println("Servico GetRemoteFileObserver criado e em execucao...");

            //adiciona observador no servico remoto
            getRemoteFileService.addObserver(observer);

            System.out.println("<Enter> para terminar...");
            System.out.println();
            System.in.read();

            getRemoteFileService.removeObserver(observer);
            UnicastRemoteObject.unexportObject(observer, true);

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        } catch (IOException | NotBoundException e) {
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}

