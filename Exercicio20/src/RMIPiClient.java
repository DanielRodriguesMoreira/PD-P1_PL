package pt.isec.pd.ex20;

import java.rmi.*;
import java.util.*;

/**
 * @author José
 */

/**
 * Exemplo de um programa que acede a uma interface remota do tipo
 * PiValueInterface para obter o valor de Pi.
 * O endereco da maquina onde se encontra registado, sob o nome "piFrontEnd",
 * o objecto remoto que se pretende aceder e' fornecido atraves da linha
 * de comando. Na pratica, tambem e' a maquina onde se encontra alojado o
 * objecto remoto.
 **/

public class RMIPiClient {

    public static void main(String[] args) {

        try {
            String objectUrl = "rmi://127.0.0.1/piFrontEnd"; //rmiregistry on localhost

            if (args.length > 0)
                objectUrl = "rmi://" + args[0] + "/piFrontEnd";

            PiValueInterface piService = (PiValueInterface) Naming.lookup(objectUrl);

            System.out.print("Numero de intervalos: ");
            Scanner in = new Scanner(System.in);
            long nIntervalos = in.nextLong();

            System.out.println("Valor do Pi: " + piService.getPiValue(nIntervalos));

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch (NotBoundException e) {
            System.out.println("Servico remoto desconhecido - " + e);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
        }
    }

}
