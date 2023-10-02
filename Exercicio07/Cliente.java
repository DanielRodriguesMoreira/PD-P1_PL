import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class Cliente {
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10;

    public static void main(String[] args) {

        InetAddress serverAddr = null;
        int serverPort = -1;
        String response;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        if (args.length != 2) {
            System.out.println("Sintaxe: java Cliente serverAddress serverPort");
            return;
        }

        try {
            /**
             * Nas linhas seguintes vamos obter o endereço IP do servidor através dos valores passados
             * como argumentos na linha de comandos.
             *
             * Notem que a variável serverAddr representa um endereço IP (Internet Protocol) e, como tal,
             * temos que converter o valor passado na linha de comandos para "algo" que seja reconhecido
             * como um IP válido. Para isso usamos o método estático getByName da class InetAddress que
             * permite converter um nome ou uma string em um IP válido.
             *
             * Notem ainda que desta forma tanto conseguem obter um IP válido passando a string "127.0.0.1"
             * como usando um nome, por exemplo, "localhost"
             */

            /**
             * Nas linhas seguintes vamos obter o endereço IP do servidor através dos valores passados
             * como argumentos na linha de comandos.
             *
             * NOTA: a variável serverAddr representa um endereço IP (Internet Protocol) e, como tal,
             *       temos que converter o valor passado na linha de comandos para "algo" que seja reconhecido
             *       como um IP válido. Para isso usamos o método estático getByName da class InetAddress que
             *       permite converter um nome ou uma string em um IP válido.
             *       Desta forma conseguem obter um IP válido passando a string "127.0.0.1" como usando um nome, por exemplo, "localhost"
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
            try (Socket socket = new Socket(serverAddr, serverPort)) {

                /**
                 * Na linha seguinte estamos a especificar um tempo de timeout que o cliente ficará à espera a ler do stream
                 * Nota: este passo é opcional mas deve ser feito para o cliente não ficar à espera infinitamente.
                 * Nota: o timeout é definido em milissegundos, neste exemplo estamos a definir um timeout de 10 segundos.
                 */
                socket.setSoTimeout(TIMEOUT * 1000);

                /**
                 * Nas linhas seguintes vamos escrever dados para o servidor usando a stream de output (OutputStream).
                 *
                 * NOTA: a classe Socket disponibiliza o método getOutputStream() que permite obter o stream usado para escrita
                 *
                 * NOTA2: estamos a usar a classe auxiliar PrintStream (existem outras que fazem o mesmo efeito)
                 *        que nos permite converter caracters em bytes e, desta forma, serem "reconhecidos" pelo OutputStream
                 */
                PrintStream pout = new PrintStream(socket.getOutputStream());
                pout.println(TIME_REQUEST);
                pout.flush();

                /**
                 * Nas linhas seguintes vamos ler dados recebidos pelo servidor usando a stream de input (InputStream)
                 * A classe Socket disponibiliza o método getInputStream() que permite obter o stream usado para leitura
                 *
                 * Mais uma vez, temos que usar classes auxiliares para conseguirmos ler os dados a um nível mais alto (caracteres e strings):
                 * InputStream -> permite ler dados como um array de bytes
                 * InputStreamReader -> permite ler dados como caracteres
                 * BufferedReader -> permite ler dados como strings
                 */
                BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                response = bin.readLine();
                System.out.println("Hora indicada pelo servidor: " + response);

                /*
                 * Nas linhas seguintes estamos a "partir" a mensagem recebida em várias tokens (hora, minutos e segundos)
                 *
                 * NOTA: este passo não tem a ver com sockets!
                 */
                //<editor-fold desc="Exemplo de como retirar os valores da mensagem de texto">
                try {
                    StringTokenizer tokens = new StringTokenizer(response, " :");

                    int hour = Integer.parseInt(tokens.nextToken().trim());
                    int minute = Integer.parseInt(tokens.nextToken().trim());
                    int second = Integer.parseInt(tokens.nextToken().trim());

                    System.out.println("Horas: " + hour + " ; Minutos: " + minute + " ; Segundos: " + second);
                } catch (NumberFormatException e) {
                }
                //</editor-fold>
            }

        /**
         * Nas linhas seguintes estamos a fazer o tratamento das exceções.
         * NOTA: apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto será alvo de avaliação
         *       no trabalho prático e/ou exame)
         *
         * NOTA2: como estamos a usar try-with-resources não precisamos de libertar os recursos, isto é feito automáticamente.
         */
        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket TCP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }
    }

}

