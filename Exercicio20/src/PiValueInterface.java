package pt.isec.pd.ex20;

import java.rmi.*;

/**
 * Define a interface remota PiValueInterface constituida apenas pelo metodo:
 * "double getPiValue(long nIntervals)".
 * Corresponde ao servico invocado pelos clientes remotos.
 **/

public interface PiValueInterface extends Remote {
    double getPiValue(long nIntervals) throws RemoteException;
}
