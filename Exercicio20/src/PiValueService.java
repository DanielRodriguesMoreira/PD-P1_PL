package pt.isec.pd.ex20;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

/**
 * @author Jose'
 */

/*************************************************************************************
 * Thread responsavel por invocar o metodo "getPartialPiValue" num worker especifico,
 * cuja referencia remota e' passada como argumento no construtor (workerRef).
 * Armazena o valor retornado no atributo "partialResult".
 */
class HandleWorker extends Thread {
    private Remote workerRef;
    private long nIntervals;
    private int nWorkers;
    private int myIndex;
    private double partialResult;

    public HandleWorker(Remote workerRef, long nIntervals, int nWorkers, int myIndex) {
        this.workerRef = workerRef;
        this.nIntervals = nIntervals;
        this.nWorkers = nWorkers;
        this.myIndex = myIndex;

        partialResult = -1.0;
    }

    public double getPartialResult() {
        return partialResult;
    }

    public Remote getMyWorkerRef() {
        return workerRef;
    }

    public void run() {
        try {

            /**
             * Invoca o metodo pretendido no objecto remoto de modo a obter
             * um valor parcial de PI.
             **/
            partialResult = ((PartialPiValueInterface) workerRef).getPartialPiValue(nIntervals, nWorkers, myIndex);

            System.out.println("<Obtido valor parcial: " + partialResult + ">");

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
        }
    }
}

/*********************************************************************************************
 * Servico RMI: interface remota constituida apenas pelo metodo getPiValue(long nIntervals).
 * +
 * Parte servidora (metodos estaticos): instancia o servico e regista-o no RMI registry.
 */
public class PiValueService extends UnicastRemoteObject implements PiValueInterface {

    protected List<Remote> workers;

    public PiValueService(List<Remote> workers) throws RemoteException {
        this.workers = workers;
    }

    //synchronized: torna este metodo "thread-safe".
    //Invocacoes de clientes distintos sao internamente atendidas por threads separadas.
    //Desta forma, se existirem varias invocacoes a este metodo, estas sao serializadas,
    //ou seja, apenas e' executada uma em cada instante, pela ordem de chegada.
    //Esta "precaucao" justifica-se porque: (1) faz sentido a capacidade de um "cluster"
    //para computacao paralela ser usado por apenas um utilizador em cada instante;
    //(2) evita que uma ou varias invocacoes estejam a eliminar determinados workers com comportamento
    //errado enquanto outras constroem a lista de threads, resultando em possiveis inconsistencias.
    synchronized public double getPiValue(long nItervals) throws RemoteException {
        List<Thread> threads = new ArrayList<Thread>();
        double soma = 0;
        int i = 1;

        for (Remote w : workers) {
            /**
             * Cria a thread do tipo HandleWorker com index = i
             **/
            Thread t = new HandleWorker(w, nItervals, workers.size(), i);

            /**
             * Poe a thread a correr
             **/
            t.start();

            /**
             * Adiciona a thread 'a lista de threads
             **/
            threads.add(t);
        }

        for (Thread t : threads) {
            try {

                /**
                 * Espera que a thread t termine
                 **/
                t.join();

                /**
                 * Obtem o valor parcial recolhido pela thread t
                 **/
                double valorParcial = ((HandleWorker) t).getPartialResult();

                if (valorParcial >= 0) {
                    soma += valorParcial;
                } else {
                    //Retira da lista o worker com comportamento errado ou inacessivel
                    workers.remove(((HandleWorker) t).getMyWorkerRef());
                }

            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        return soma;
    }

    /**
     * Obtem as localizacoes dos workers a partir do ficheiro de texto fileName.
     * Este nclui um endereco IP/Nome DNS por linha.
     * Devolve a lista de enderecos IP/Nomes sob a forma de Strings.
     **/
    public static List<Remote> getWorkers(String fileName) {
        String workerName = null;

        List<Remote> workers = new ArrayList<Remote>();

        try (BufferedReader inFile = new BufferedReader(new FileReader(fileName))) {

            while ((workerName = inFile.readLine()) != null) {

                workerName = workerName.trim();
                if (workerName.length() == 0) {
                    continue;
                }

                /**
                 * Obtem a referencia remota para o worker (i.e., servico remoto com
                 * interface PartialPiValueInterface). Este encontra-se registado
                 * num registry situado maquina com endereco dado pelo atributo workerName.
                 **/

                try {

                    //... objectUrl = //...
                    String objectUrl = "rmi://" + workerName + "/piWorker";

                    //... workerRef = //...
                    Remote workerRef = Naming.lookup(objectUrl);

                    workers.add(workerRef);

                } catch (NotBoundException e) {
                    System.out.println("Erro remoto - " + e);
                }

                System.out.println("<Worker " + (workers.size()) + "> " + workerName);
            }

        } catch (IOException e) {
            System.out.println("Erro - " + e);
        }

        return workers;
    }

    /**
     * Instancia e regista o servi�o remoto.
     **/
    public static void main(String[] args) {

        List<Remote> workers;

        if (args.length != 1) {
            System.out.println("Sintaxe: java PiValueService ficheiro_workers");
            return;
        }

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

        try {

            /**
             * Obtem as referencias remotas dos workers com base no
             * conteudo do ficheiro cujo nome e' passado em args[0].
             **/
            workers = getWorkers(args[0]);

            if (workers.size() < 1) {
                return;
            }

            /**
             * Cria o servico PiValueService.
             **/
            PiValueService piValueService = new PiValueService(workers);
            System.out.println("Servico piValueService criado e em execucao...");


            // Regista o servico para que os clientes possam encontra'-lo, ou seja, obter a sua referencia remota.
            Naming.rebind("rmi://localhost/piFrontEnd", piValueService);
            System.out.println("Servico piValueService registado no registry local sob o nome piFrontEnd...");

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}
