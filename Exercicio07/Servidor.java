import java.net.*;
import java.io.*;
import java.util.Calendar;

public class Servidor {
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        int listeningPort;
        String receivedMsg, timeMsg;
        Calendar calendar;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        if (args.length != 1) {
            System.out.println("Sintaxe: java Servidor listeningPort");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);

        /**
         * Na linha seguinte vamos criar um ServerSocket.
         *
         * NOTA: estamos a passar o porto como argumento para o construtor. Ao contrário do cliente
         *       aqui precisamos de especificar que porto queremos usar, pois temos que ter essa informação
         *       para os clientes terem a possibilidade de se conectar.
         */
        try (ServerSocket serverSocket = new ServerSocket(listeningPort);) {

            while (true) {

                /**
                 * Na linha seguinte vamos esperar pela conexão de um cliente e quando isso acontecer irá ser criado
                 * um socket para comunicação cliente-servidor.
                 *
                 * NOTA: será criado um socket por cliente conectado, ou seja, se se ligarem 3 clientes então serão
                 *       criados 3 socket e cada um é especifico para comunicação entre esse cliente e o servidor.
                 *
                 * NOTA2: o método accept() é bloqueante, ou seja, a thread fica bloqueada até haver uma nova ligação.
                 */
                try (Socket clientSocket = serverSocket.accept()) {

                    /**
                     * Nas linhas seguintes vamos ler dados recebidos pelo cliente usando a stream de input (InputStream)
                     * Notem que a classe Socket disponibiliza o método getInputStream() que permite obter o stream usado para leitura
                     *
                     * Mais uma vez, temos que usar classes auxiliares para conseguirmos ler os dados a um nível mais alto (caracteres e strings):
                     * InputStream -> permite ler dados como um array de bytes
                     * InputStreamReader -> permite ler dados como caracteres
                     * BufferedReader -> permite ler dados como strings
                     *
                     * NOTA: estamos a fazer print do IP e porto do cliente, isto é possível pois temos essa
                     *       informação no Socket criado na linha 47.
                     */
                    BufferedReader bin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    receivedMsg = bin.readLine();
                    System.out.println("Recebido \"" + receivedMsg + "\" de " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                    /**
                     * A linha seguinte é apenas uma verificação para não ignorar e não enviar resposta caso a mensagem recebida não seja igual a "TIME"
                     */
                    if (!receivedMsg.equalsIgnoreCase(TIME_REQUEST)) {
                        continue;
                    }

                    /**
                     * Nas linhas seguintes estamos a obter a hora do calendário e a colocar essa informação na variável (timeMsg)
                     * que irá conter os dados que queremos enviar para o cliente
                     */
                    calendar = Calendar.getInstance();
                    timeMsg = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                            calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

                    /**
                     * Nas linhas seguintes vamos escrever dados para o cliente usando a stream de output (OutputStream).
                     *
                     * NOTA: a classe Socket disponibiliza o método getOutputStream() que permite obter o stream usado para escrita
                     *
                     * NOTA2: estamos a usar a classe auxiliar PrintStream (existem outras que fazem o mesmo efeito)
                     * que nos permite converter caracters em bytes e, desta forma, serem "reconhecidos" pelo OutputStream
                     */
                    PrintStream pout = new PrintStream(clientSocket.getOutputStream());
                    pout.println(timeMsg);
                    pout.flush();
                }
            }

        /**
         * Nas linhas seguintes estamos a fazer o tratamento das exceções.
         *
         * NOTA: apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto
         *       será alvo de avaliação no trabalho prático e/ou exame)
         *
         * NOTA2: como estamos a usar try-with-resources não precisamos de libertar os recursos, isto é feito automáticamente.
         */
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do serverSocket TCP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao serverSocket:\n\t" + e);
        }
    }
}
