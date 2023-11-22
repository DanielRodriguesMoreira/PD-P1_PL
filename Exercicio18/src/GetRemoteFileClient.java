package exercicio18;

/*
 * Exemplo de utilizacao do servico com interface remota GetRemoteFileInterface.
 * Assume-se que o servico encontra-se registado sob o nome "servidor-ficheiros-pd".
 * Esta classe tambem implementa uma interface remota (GetRemoteFileClientInterface)
 * que deve incluir o metodo:
 *
 *       void writeFileChunk(byte [] fileChunk, int nbytes) throws java.io.IOException
 *
 */

import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Jose'
 */
public class GetRemoteFileClient {

    public static void main(String[] args) {

        String objectUrl;
        File localDirectory;
        String fileName;
        String localFilePath;

        GetRemoteFileClientService myRemoteService = null;
        GetRemoteFileServiceInterface remoteFileService;

        /*
         * Se existirem varias interfaces de rede activas na maquina onde corre esta aplicacao,
         * convem definir de forma explicita o endereco que deve ser incluido na referencia remota do servico
         * RMI criado. Para o efeito, o endereco deve ser atribuido 'a propriedade java.rmi.server.hostname.
         *
         * Pode ser no codigo atraves do metodo System.setProperty():
         *      - System.setProperty("java.rmi.server.hostname", "10.65.129.232"); //O endereco usado e' apenas um exemplo
         *      - System.setProperty("java.rmi.server.hostname", args[3]); //Neste caso, assume-se que o endereco e' passado como quarto argumento na linha de comando
         *
         * Tambem pode ser como opcao passada 'a maquina virtual Java:
         *      - java -Djava.rmi.server.hostname=10.202.128.22 GetRemoteFileClient ... //O endereco usado e' apenas um exemplo
         *      - No Netbeans: Properties -> Run -> VM Options -> -Djava.rmi.server.hostname=10.202.128.22 //O endereco usado e' apenas um exemplo
         */

        /*
         * Trata os argumentos da linha de comando.
         */

        if (args.length != 3) {
            System.out.println("Deve passar na linha de comando: (1) a localizacao do RMI registry onte esta' "
                    + "registado o servico; (2) a diretoria local onde pretende guardar "
                    + "o ficheiro obtido; e (3) o ficheiro pretendido!");
            System.out.println();
            return;
        }

        objectUrl = "rmi://" + args[0] + "/servidor-ficheiros-pd";
        localDirectory = new File(args[1].trim());
        fileName = args[2].trim();

        if (!localDirectory.exists()) {
            System.out.println("A diretoria " + localDirectory + " nao existe!");
            return;
        }

        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma diretoria!");
            return;
        }
        if (!localDirectory.canWrite()) {
            System.out.println("Sem permissoes de escrita na diretoria " + localDirectory);
            return;
        }

        try {
            localFilePath = new File(localDirectory.getPath() + File.separator + fileName).getCanonicalPath();
        } catch (IOException ex) {
            System.out.println("Erro E/S - " + ex);
            return;
        }

        try (FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)) { //Cria o ficheiro local

            System.out.println("Ficheiro " + localFilePath + " criado.");

            /*
             * Obtem a referencia remota para o servico com nome "servidor-ficheiros-pd".
             */
            remoteFileService = (GetRemoteFileServiceInterface) Naming.lookup(objectUrl);

            /*
             * Lanca o servico local para acesso remoto por parte do servidor.
             */
            myRemoteService = new GetRemoteFileClientService();

            /*
             * Passa ao servico RMI LOCAL uma referencia para o objecto localFileOutputStream.
             */
            myRemoteService.setFout(localFileOutputStream);

            /*
             * Obtem o ficheiro pretendido, invocando o metodo getFile no servico remoto.
             */

            remoteFileService.getFile(fileName, myRemoteService);

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch (NotBoundException e) {
            System.out.println("Servico remoto desconhecido - " + e);
        } catch (IOException e) {
            System.out.println("Erro E/S - " + e);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
        } finally {
            if (myRemoteService != null) {

                /*
                 * Retira do servico local a referencia para o objecto localFileOutputStream.
                 */
                myRemoteService.setFout(null);

                /*
                 * Termina o servi�o local.
                 */
                try {
                    UnicastRemoteObject.unexportObject(myRemoteService, true);
                } catch (NoSuchObjectException e) {
                }
            }
        }

    }
}

