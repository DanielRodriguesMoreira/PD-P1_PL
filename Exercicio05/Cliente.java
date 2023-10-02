import java.net.*;
import java.io.*;
import java.util.*;

public class Cliente {

    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) {

        InetAddress serverAddr = null;
        int serverPort = -1;
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        String response;

        /*
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        if (args.length != 2) {
            System.out.println("Sintaxe: java Cliente serverAddress serverUdpPort");
            return;
        }

        /*
         * Notem que esta versão não está a utilizar try-with-resources, logo temos que libertar os recursos que utilizámos
         * no bloco de código finally
         */
        try {

            /*
             * Nas linhas seguintes vamos popular as variáveis que irão armazenar o IP e porto do servidor.
             *
             * NOTA: estamos a usar UDP, por isso temos sempre que especificar o endereço de destino para onde queremos
             *       enviar o datagrama. É como se fosse uma carta de correio!
             *
             * NOTA2: a função estática getByName da classe InetAddress é uma função auxiliar que nos permite obter
             *        o endereço IP tanto este seja passado como uma string no formato por exemplo: 192.168.1.70 como
             *        pelo próprio nome, por exemplo: servidorDaniel.com
             *
             * NOTA3: ao usarmos o IP de loopback 127.0.0.1 ou o nome "localhost" estamos a dizer que queremos enviar
             *        um datagram para uma aplicação que está na nossa própria máquina.
             */
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);


            /*
             * Na linha seguinte estamos a criar um DatagramSocket. É através deste que vamos enviar/receber datagramas.
             *
             * NOTA: estamos a usar um construtor sem parâmetros, ou seja, não estamos a definir nenhum porto. Ao não
             *       definir nenhum porto estamos a permitir que o sistema nos atribua um porto automaticamente.
             */
            socket = new DatagramSocket();

            /*
             * Na linha seguinta estamos a especificar um tempo de timeout.
             * Isto faz com o que ao usar a função receive(), esta fique bloqueada à espera de resposta por X tempo
             * (neste caso o timeout é definido pelo valor da constante TIMEOUT)
             */
            socket.setSoTimeout(TIMEOUT * 1000);

            /*
             * Na linha seguinte estamos a criar o DatagramPacket.
             * Na analogia da carta de correio, este é o passo em que estamos a escrever a nossa carta e vamos especificar,
             * além do conteúdo da carta, o tamanho do conteúdo e o endereço IP e porto do destinatário (para onde queremos enviar)
             *
             * NOTA: neste caso a mensagem que estamos a enviar é definida pela constante TIME_REQUEST
             */
            packet = new DatagramPacket(TIME_REQUEST.getBytes(), TIME_REQUEST.length(), serverAddr, serverPort);

            /*
             * Na linha seguinta vamos utilizar o método send() do nosso socket para efetivamente enviar o datagram packet.
             */
            socket.send(packet);

            /*
             * Nas linhas seguintes estamos à espera de uma resposta usando o método receive() do socket.
             *
             * NOTA: o método receive() é bloqueante, ou seja, o código ficará parado nesta linha até receber um datagram
             *       ou até ser atingido o timeout (se especificado)
             *
             * NOTA2: Porque estamos a criar um novo DatagramPacket para receber invés de usar o que utilizámos para enviar?
             *        Isto porque a mensagem que recebemos pode ter um tamanho diferente da mensagem que enviámos.
             *        Ou seja, no caso deste exercício, o datagrama que enviámos tinha a mensagem "TIME" e, por isso, o tamanho
             *        que especificámos foi 4 (que é o length de TIME), no entanto, a mensagem que iremos receber é bem maior,
             *        logo, para não perder informação desnecessária, estamos a criar um novo datagram packet (neste caso com 256)
             */
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            /*
             * Nas linhas seguintes estamos a mostrar a mensagem que recebemos do servidor.
             *
             * NOTA: estamos a criar uma instância da classe String com os dados do pacote recebido lá dentro pois os dados vêm
             *       codificados (codificação é diferente de encriptação!) e a classe String permite descodificar e converter
             *       para algo que seja possível apresentar no println.
             */
            response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Hora indicada pelo servidor: " + response);

            /*
             * Nas linhas seguintes estamos a "partir" a mensagem recebida em várias tokens (hora, minutos e segundos)
             *
             * NOTA: este passo não tem a ver com sockets!
             */
            try {
                StringTokenizer tokens = new StringTokenizer(response, " :");

                int hour = Integer.parseInt(tokens.nextToken().trim());
                int minute = Integer.parseInt(tokens.nextToken().trim());
                int second = Integer.parseInt(tokens.nextToken().trim());

                System.out.println("Horas: " + hour + " ; Minutos: " + minute + " ; Segundos: " + second);
            } catch (NumberFormatException e) {
            }


        /*
         * Nas linhas seguintes estamos a fazer o tratamento de exceções.
         */
        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        } finally {
            /*
             * Nas linha seguintes estamos a libertar os recursos, nesta caso a fechar o socket.
             *
             * NOTA: se usarem try-with-resouces evitam de ter este bloco finally.
             */
            if (socket != null) {
                socket.close();
            }
        }
    }

}

