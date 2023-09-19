import java.net.*;
import java.io.*;

public class cliente {
    public static final int MAX_SIZE = 256;

    public static void main(String[] args)
    {
        InetAddress serverAddr = null;
        int serverPort = -1;
        DatagramSocket socket = null;
        DatagramPacket packet = null;

        try{
            // Nas linhas seguintes vamos obter o endereço IP do servidor através dos valores passados
            // como argumentos na linha de comandos.
            // Notem que a variável serverAddr representa um endereço IP (Internet Protocol) e, como tal,
            // temos que converter o valor passado na linha de comandos para "algo" que seja reconhecido
            // como um IP válido. Para isso usamos o método estático getByName da class InetAddress que
            // permite converter um nome ou uma string em um IP válido.
            // Notem ainda que desta forma tanto conseguem obter um IP válido passando a string "127.0.0.1"
            // como usando um nome, por exemplo, "localhost"
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            // Na linha seguinte vamos criar um DatagramSocket.
            // Notem que não estamos a passar nenhum argumento para o construtor o que faz com que seja
            // atribuído um porto automático ao socket.
            socket = new DatagramSocket();

            // Na linha seguinte vamos criar o DatagramPacket.
            // Para isso vamos passar 4 argumentos:
            //  1) array de bytes que contém os dados do pacote (mensagem que queremos enviar)
            //  2) tamanho do array de bytes (ou seja, tamanho da mensagem que queremos enviar)
            //  3) endereço IP do servidor (objeto do tipo InetAddress)
            //  4) porto do servidor
            byte[] msg = "Olá".getBytes();
            packet = new DatagramPacket(msg, msg.length, serverAddr, serverPort);

            // Na linha seguinte vamos usar o DatagramSocket para enviar o DatagramPacket
            socket.send(packet);

            // Nas linhas seguintes vamos criar um novo pacote e usar o DatagramSocket para receber a resposta.
            // P: Porque estamos a criar um novo pacote invés de usar o mesmo?
            // R: No pacote que usámos para enviar a mensagem tivemos que definir um tamanho (vejam linha 38 onde
            // estamos a dizer que o tamanho do pacote é igual ao tamanho da variável msg, ou seja, terá o tamanho
            // será igual a 3 (Olá = 3). Logo, se usarmos o mesmo pacote estamos a limitar o nº de bytes recebidos
            // a 3, ou seja, se a resposta que vier do servidor tiver mais que 3 bytes então será cortada.
            // Portanto, para evitarmos esta situação, devemos criar um novo DatagramPacket com um tamanho maior.
            // Notem também que o método receive é um método bloqueante, ou seja, a aplicação ficará parada na
            // linha 54 até receber um pacote (ou até atingir o timeout, se específicado)
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            // Nas linhas seguintes vamos mostrar a resposta do servidor.
            // Notem que os dados (packet.getData()) que estão no DatagramPacket estão codificados, logo podemos usar
            // o construtor da classe String para fazer o decode.
            // Caso contrário, ao fazer print diretamente do packet.getData() estariamos a mostrar uma mensagem
            // codificada e imperceptivel.
            String response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Resposta do servidor: " + response);

        // Nas linhas seguintes estamos a fazer o tratamento das exceções.
        // Notem que apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto
        // será alvo de avaliação no trabalho prático e/ou exame)
        } catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        } catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        } catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        } finally {
            // Na linha seguinte estamos a fechar o closet uma vez que a aplicação cliente vai terminar
            // Notem que devem sempre fechar e limpar recursos.
            if(socket != null){
                socket.close();
            }
        }
    }

}

