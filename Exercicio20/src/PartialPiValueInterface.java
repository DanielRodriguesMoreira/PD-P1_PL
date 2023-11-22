package pt.isec.pd.ex20;

import java.rmi.*;

/**
 * Define a interface remota PartialPiValueInterface constituida apenas pelo metodo:
 * "double getPartialPiValue(long nIntervals, int nWorkers, int myIndex)".
 * Corresponde 'a interface dos Workers.
 **/
public interface PartialPiValueInterface extends Remote {
    double getPartialPiValue(long nIntervals, int nWorkers, int myIndex) throws RemoteException;
}
