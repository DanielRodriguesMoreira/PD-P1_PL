import java.net.*;
import java.io.*;

public class Cliente {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5; //segundos

    public static void main(String[] args) {
        File localDirectory;
        String fileName, localFilePath = null;
        InetAddress serverAddr;
        int serverPort;
        PrintWriter pout;
        InputStream in;
        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;
        int contador = 0;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        //<editor-fold desc="Validar sintaxe">
        if (args.length != 4) {
            System.out.println("Sintaxe: java Cliente serverAddress serverPort fileToGet localDirectory");
            return;
        }
        //</editor-fold>

        /**
         * Nas linhas seguintes estamos a popular as variáveis fileName e localDirectory com os valores que obtemos
         * quando executamos a aplicação no terminal.
         */
        fileName = args[2].trim();
        localDirectory = new File(args[3].trim());

        /**
         * Nas linhas seguintes estamos métodos da classe File que permitem fazer validações ao nível da directoria.
         * 1º Se a directoria existe
         * 2º se é uma directoria
         * 3º se temos permissões para escrita
         */
        //<editor-fold desc="Validações sobre a diretoria">
        if (!localDirectory.exists()) {
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }
        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }
        if (!localDirectory.canWrite()) {
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }
        //</editor-fold>

        try {
            /**
             * Nas linhas seguintes vamos criar o objeto que irá ser usado para escrever (output) o ficheiro que
             * irá ser recebido via datagrama.
             *
             * NOTA: neste ponto ainda não estamos a escrever nada no ficheiro, apenas o abrimos para escrita e por isso
             *       este já foi criado na directoria e com o nome que especificámos.
             */
            localFilePath = localDirectory.getCanonicalPath() + File.separator + fileName;

            try (FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)) {
                System.out.println("Ficheiro " + localFilePath + " criado.");

                try {
                    /**
                     * Nas linhas seguintes vamos obter o endereço IP do servidor através dos valores passados
                     * como argumentos na linha de comandos.
                     *
                     * NOTA: a variável serverAddr representa um endereço IP (Internet Protocol) e, como tal,
                     *       temos que converter o valor passado na linha de comandos para "algo" que seja reconhecido
                     *       como um IP válido. Para isso usamos o método estático getByName da class InetAddress que
                     *       permite converter um nome ou uma string em um IP válido.
                     *       Desta forma tanto conseguem obter um IP válido passando a string "127.0.0.1" como usando um nome, por exemplo, "localhost"
                     */
                    serverAddr = InetAddress.getByName(args[0]);
                    serverPort = Integer.parseInt(args[1]);

                    /**
                     * Na linha seguinte vamos criar um objeto do tipo Socket que permite a comunicação entre 2 aplicações.
                     *
                     * NOTA: que temos que especificar o endereço IP e porto do servidor para criar a conexão
                     *
                     * NOTA2: estamos a usar try-with-resouces o que faz com que o recurso (neste caso o socket)
                     *        seja fechado automaticamente no final
                     */
                    try (Socket socketToServer = new Socket(serverAddr, serverPort)) {
                        /**
                         * Na linha seguinta estamos a especificar um tempo de timeout.
                         * Isto faz com o que ao usar a função receive(), esta fique bloqueada à espera de resposta por X tempo
                         * (neste caso o timeout é definido pelo valor da constante TIMEOUT)
                         */
                        socketToServer.setSoTimeout(TIMEOUT * 1000);

                        /**
                         * Nas linhas seguintes vamos escrever dados para o servidor usando a stream de output (OutputStream).
                         *
                         * NOTA: a classe Socket disponibiliza o método getOutputStream() que permite obter o stream usado para escrita
                         *
                         * NOTA2: estamos a usar a classe auxiliar PrintStream (existem outras que fazem o mesmo efeito)
                         *        que nos permite converter caracters em bytes e, desta forma, serem "reconhecidos" pelo OutputStream
                         */
                        pout = new PrintWriter(socketToServer.getOutputStream(), true);
                        pout.println(fileName);
                        pout.flush();

                        /**
                         * Nas linhas seguintes vamos ler os bytes do InputStream e "escrevê-los" no ficheiro através do FileOutputStream.
                         */
                        in = socketToServer.getInputStream();
                        while ((nbytes = in.read(fileChunk)) > 0) {
                            System.out.println("Recebido o bloco n. " + ++contador + " com " + nbytes + " bytes.");
                            localFileOutputStream.write(fileChunk, 0, nbytes);
                            System.out.println("Acrescentados " + nbytes + " bytes ao ficheiro " + localFilePath + ".");
                        }

                        System.out.println("Transferencia concluida.");

                    /**
                     * Nas linhas seguintes estamos a fazer o tratamento das exceções.
                     * NOTA: apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto será alvo de avaliação
                     *       no trabalho prático e/ou exame)
                     *
                     * NOTA2: como estamos a usar try-with-resources não precisamos de libertar os recursos, isto é feito automáticamente.
                     */
                    } catch (NumberFormatException e) {
                        System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Não foi recebida qualquer bloco adicional, podendo a transferencia estar incompleta:\n\t" + e);
                    } catch (SocketException e) {
                        System.out.println("Ocorreu um erro ao nível do socket TCP:\n\t" + e);
                    } catch (IOException e) {
                        System.out.println("Ocorreu um erro no acesso ao socket ou ao ficheiro local " + localFilePath + ":\n\t" + e);
                    }
                } catch (UnknownHostException e) {
                    System.out.println("Destino desconhecido:\n\t" + e);
                }
            }
        } catch (IOException e) {
            if (localFilePath == null) {
                System.out.println("Ocorreu a excepcao {" + e + "} ao obter o caminho canonico para o ficheiro local!");
            } else {
                System.out.println("Ocorreu a excepcao {" + e + "} ao tentar criar o ficheiro " + localFilePath + "!");
            }
        }

    }
}

