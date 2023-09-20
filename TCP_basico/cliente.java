import java.net.*;
import java.io.*;

/**
 * Esta aplicação serve para usarem como referência para aprenderem/perceberem os passos básicos necessários
 * para utilizarem o protocolo TCP para comunicar entre aplicações.
 *
 * Passos básicos para um cliente TCP:
 * 1 - iniciar conexão com um servidor especificando o IP e porto do servidor
 * 2 - escrever dados usando OutputStream
 * 3 - ler dados usando InputStream
 * 4 - fechar conexão
 */
public class cliente {

    public static void main(String[] args)
    {

        InetAddress serverAddr = null;
        int serverPort = -1;
        Socket socket = null;
        String response;

        try{
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
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            /**
             * Na linha seguinte vamos criar um objeto do tipo Socket que permite a comunicação entre 2 aplicações.
             * Notem que temos que especificar o endereço IP e porto do servidor para criar a conexão
             */
            socket = new Socket(serverAddr, serverPort);

            /**
             * Na linha seguinte estamos a especificar um tempo de timeout que o cliente ficará à espera a ler do stream
             * Nota: este passo é opcional mas deve ser feito para o cliente não ficar à espera infinitamente.
             * Nota: o timeout é definido em milissegundos, neste exemplo estamos a definir um timeout de 10 segundos.
             */
            socket.setSoTimeout(10 * 1000);

            /**
             * Nas linhas seguintes vamos escrever dados para o servidor usando a stream de output (OutputStream).
             * Notem que a classe Socket disponibiliza o método getOutputStream() que permite obter o stream usado para escrita
             * Notem, também, que estamos a usar a classe auxiliar PrintStream (existem outras que fazem o mesmo efeito)
             * que nos permite converter caracters em bytes e, desta forma, serem "reconhecidos" pelo OutputStream
             */
            PrintStream pout = new PrintStream(socket.getOutputStream());
            pout.println("Olá");
            pout.flush();

            /**
             * Nas linhas seguintes vamos ler dados recebidos pelo servidor usando a stream de input (InputStream)
             * Notem que a classe Socket disponibiliza o método getInputStream() que permite obter o stream usado para leitura
             *
             * Mais uma vez, temos que usar classes auxiliares para conseguirmos ler os dados a um nível mais alto (caracteres e strings)
             *
             * InputStream: permite ler dados como um array de bytes
             * InputStreamReader: permite ler dados como caracteres
             * BufferedReader: permite ler dados como strings
             */
            BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = bin.readLine();
            System.out.println("Resposta do servidor: " + response);

        /**
         * Nas linhas seguintes estamos a fazer o tratamento das exceções.
         * Notem que apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto
         * será alvo de avaliação no trabalho prático e/ou exame)
         */
        }catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            /**
             * Na linha seguinte estamos a fechar o closet uma vez que a aplicação cliente vai terminar
             * Notem que devem sempre fechar e limpar recursos.
             */
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) { }
            }
        }
    }

}

