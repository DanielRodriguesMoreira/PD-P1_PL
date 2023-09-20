import java.net.*;
import java.io.*;

/**
 * Esta aplicação serve para usarem como referência para aprenderem/perceberem os passos básicos necessários
 * para utilizarem o protocolo TCP para comunicar entre aplicações.
 *
 * Passos básicos para um servidor TCP:
 * 1 - criar um ServerSocket e fazer bind a um porto
 * 2 - esperar por uma conexão e aceitá-la. Isto resultará num socket para comunicação individual com cada cliente
 * 3 - ler dados (a partir do socket criado no ponto 2) usando InputStream
 * 4 - escrever dados (a partir do socket criado no ponto 2) usando OutputStream
 * 5 - fechar conexão
 */
public class servidor {

    public static void main(String[] args) {

        int listeningPort;
        ServerSocket serverSocket = null;
        String receivedMsg;

        try{
            /**
             * Na linha seguinte vamos criar um ServerSocket.
             * Notem que estamos a passar o porto como argumento para o construtor. Ao contrário do cliente
             * aqui precisamos de especificar que porto queremos usar, pois temos que ter essa informação
             * para os clientes terem a possibilidade de se conectar.
             */
            listeningPort = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(listeningPort);

            while(true){

                /**
                 * Na linha seguinte vamos esperar pela conexão de um cliente e quando isso acontecer irá ser criado
                 * um socket para comunicação cliente-servidor.
                 * Notem que será criado um socket por cliente conectado, ou seja, se se ligarem 3 clientes então serão
                 * criados 3 socket e cada um é especifico para comunicação entre esse cliente e o servidor.
                 *
                 * Notem, também, que o método accept() é bloqueante, ou seja, a thread fica bloqueada até haver uma
                 * nova ligação.
                 */
                Socket clientSocket = serverSocket.accept();

                /**
                 * Nas linhas seguintes vamos ler dados recebidos pelo cliente usando a stream de input (InputStream)
                 * Notem que a classe Socket disponibiliza o método getInputStream() que permite obter o stream usado para leitura
                 *
                 * Mais uma vez, temos que usar classes auxiliares para conseguirmos ler os dados a um nível mais alto (caracteres e strings)
                 *
                 * InputStream: permite ler dados como um array de bytes
                 * InputStreamReader: permite ler dados como caracteres
                 * BufferedReader: permite ler dados como strings
                 *
                 * Notem, também, que estamos a fazer print do IP e porto do cliente, isto é possível pois temos essa
                 * informação no Socket criado na linha 44.
                 */
                BufferedReader bin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                receivedMsg = bin.readLine();
                System.out.println("Recebi \"" + receivedMsg + "\" de " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                /**
                 * Nas linhas seguintes vamos escrever dados para o cliente usando a stream de output (OutputStream).
                 * Notem que a classe Socket disponibiliza o método getOutputStream() que permite obter o stream usado para escrita
                 * Notem, também, que estamos a usar a classe auxiliar PrintStream (existem outras que fazem o mesmo efeito)
                 * que nos permite converter caracters em bytes e, desta forma, serem "reconhecidos" pelo OutputStream
                 */
                PrintStream pout = new PrintStream(clientSocket.getOutputStream());
                pout.println("Olá cliente!");
                pout.flush();
            }

        /**
         * Nas linhas seguintes estamos a fazer o tratamento das exceções.
         * Notem que apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto
         * será alvo de avaliação no trabalho prático e/ou exame)
         */
        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do serverSocket TCP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao serverSocket:\n\t"+e);
        }finally{
            /**
             * Na linha seguinte estamos a fechar o closet uma vez que a aplicação cliente vai terminar
             * Notem que devem sempre fechar e limpar recursos.
             */
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}
