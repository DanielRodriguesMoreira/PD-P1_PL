package pt.isec.pd.ex20;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

/**
 * @author José
 */
public class PartialPiValueService extends UnicastRemoteObject implements PartialPiValueInterface {

    public PartialPiValueService() throws RemoteException {
    }

    @Override
    public double getPartialPiValue(long nIntervals, int nWorkers, int myIndex) throws RemoteException {
        long i;
        double dX, xi, myResult;

        if (nIntervals < 1 || nWorkers < 1 || myIndex < 1 || myIndex > nWorkers) {
            return 0.0;
        }

        dX = 1.0 / nIntervals;
        myResult = 0;

        for (i = myIndex - 1; i < nIntervals; i += nWorkers) {
            xi = dX * (i + 0.5);
            myResult += (4.0 / (1.0 + xi * xi));
        }

        myResult *= dX;

        System.out.println("<Calculado valor parcial: " + myResult + ">");

        return myResult;
    }

    public static void main(String[] args) {
        try {

            /**
             * Lanca um registry na maquina local e
             * no porto por omissao Registry.REGISTRY_PORT.
             **/
            try {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("Registry lancado!");
            } catch (RemoteException e) {
                //Provavelmente ja tinha sido lancado.
                System.out.println("Registry provavelmente ja' em execucao!");
            }

            /**
             * Cria o servico PartialPiValueService.
             **/

            PartialPiValueService partialPiValueService = new PartialPiValueService();
            System.out.println("Servico partialPiValueService criado e em execucao...");

            /**
             * Regista o servico com o nome "piWorker" para que os clientes possam encontra'-lo, ou seja,
             * obter a sua referencia remota.
             **/

            Naming.bind("rmi://localhost/piWorker", partialPiValueService);
            System.out.println("Servico partialPiValueService registado no registry sob o nome piWorker...");
        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}
