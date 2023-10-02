import java.net.*;
import java.io.*;

public class Servidor {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 10;

    public static void main(String[] args) {

        File localDirectory;
        String requestedFileName, requestedCanonicalFilePath = null;
        int listeningPort;
        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        //<editor-fold desc="Validar sintaxe">
        if (args.length != 2) {
            System.out.println("Sintaxe: java Servidor listeningPort localRootDirectory");
            return;
        }
        //</editor-fold>

        localDirectory = new File(args[1].trim());

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
        if (!localDirectory.canRead()) {
            System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
            return;
        }
        //</editor-fold>

        listeningPort = Integer.parseInt(args[0]);

        /**
         * Na linha seguinte vamos criar um ServerSocket.
         *
         * NOTA: estamos a passar o porto como argumento para o construtor. Ao contrário do cliente
         *       aqui precisamos de especificar que porto queremos usar, pois temos que ter essa informação
         *       para os clientes terem a possibilidade de se conectar.
         */
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {

            while (true) {

                /**
                 * Na linha seguinte vamos esperar pela conexão de um cliente e quando isso acontecer irá ser criado
                 * um socket para comunicação cliente-servidor.
                 *
                 * NOTA: será criado um socket por cliente conectado, ou seja, se se ligarem 3 clientes então serão
                 *       criados 3 socket e cada um é especifico para comunicação entre esse cliente e o servidor.
                 *
                 * NOTA2: o método accept() é bloqueante, ou seja, a thread fica bloqueada até haver uma
                 *        nova ligação.
                 */
                try (Socket clientSocket = serverSocket.accept()) {
                    clientSocket.setSoTimeout(TIMEOUT * 1000);

                    /**
                     * Nas linhas seguintes vamos ler dados recebidos pelo cliente usando a stream de input (InputStream)
                     * A classe Socket disponibiliza o método getInputStream() que permite obter o stream usado para leitura
                     *
                     * NOTA: estamos a fazer print do IP e porto do cliente, isto é possível pois temos essa informação no Socket criado na linha 75.
                     */
                    BufferedReader bin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    requestedFileName = bin.readLine();
                    System.out.println("Recebido pedido para \"" + requestedFileName + "\" de " + clientSocket.getInetAddress().getHostName() + ":" + clientSocket.getPort());


                    /**
                     * Nas linhas seguintes estamos a verificar se temos acesso ao ficheiro que o cliente pretende obter.
                     */
                    //<editor-fold desc="Validar se é possível aceder ao ficheiro">
                    requestedCanonicalFilePath = new File(localDirectory + File.separator + requestedFileName).getCanonicalPath();

                    if (!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                        System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                        System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath() + "!");
                        continue;
                    }
                    //</editor-fold>


                    /**
                     * Na linha estamos a abrir o ficheiro para leitura (input). Para isso podemos usar a classe FileInputStream em que
                     * usamos o seu construtor para especificar que ficheiro queremos efetivamente abrir para leitura.
                     */
                    try (FileInputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath)) {
                        System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");

                        OutputStream out = clientSocket.getOutputStream();

                        /**
                         * Este ciclo do...while() irá ser executado enquanto houverem bytes para serem lidos do ficheiro, ou seja,
                         * enquanto não tivermos lido todo o ficheiro.
                         */
                        do {

                            /**
                             * Na linha seguinte vamos ler um chunk do ficheiro, neste caso especificámos que os chunks serão de
                             * 4000 bytes no máximo.
                             */
                            nbytes = requestedFileInputStream.read(fileChunk);

                            /**
                             * O if seguinte é usado porque ao tentar ler um chunk que já não existe, o nbytes será -1
                             */
                            if (nbytes != -1) {
                                /**
                                 * Nas linhas seguintes vamos escrever os chunks na stream de output obtida através do socket.
                                 */
                                out.write(fileChunk, 0, nbytes);
                                out.flush();
                            }
                        } while (nbytes > 0);

                        System.out.println("Transferencia concluida");
                    }

                    /**
                     * Nas linhas seguintes estamos a fazer o tratamento das exceções.
                     *
                     * NOTA: apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto
                     *       será alvo de avaliação no trabalho prático e/ou exame)
                     */
                } catch (SocketTimeoutException ex) { //Subclasse de IOException
                    System.out.println("O cliente atual nao enviou qualquer nome de ficheiro (timeout)");
                } catch (FileNotFoundException e) {   //Subclasse de IOException
                    System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro " + requestedCanonicalFilePath + "!");
                } catch (IOException ex) {
                    System.out.println("Problem de I/O no atendimento ao cliente atual: " + ex);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu uma excepcao ao nivel do socket UDP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
        }
    }
}
