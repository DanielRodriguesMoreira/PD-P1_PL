import java.net.*;
import java.io.*;
import java.util.*;

public class Cliente {

    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args)
    {

        InetAddress serverAddr = null;
        int serverPort = -1;
        ... socket = null;
        ... packet = null;
        String response;

        // 1º Testar a sintaxe
        if(args.length != ...){
            System.out.println("Sintaxe: java Cliente serverAddress serverUdpPort");
            return;
        }

        try{

            // 2º Obter endereço do servidor
            serverAddr = ...
            serverPort = Integer.parseInt(...);

            // 3º Criar socket
            socket = ...
            socket.setSoTimeout(TIMEOUT*1000);

            // 4º Criar datagram packet
            packet = new DatagramPacket(...);

            // 5º Enviar datagram
            ...

            // 6º Receber datagram
            ...

            // 7º Mostrar resposta recebida
            response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Hora indicada pelo servidor: " + response);

            //Exemplo de como retirar os valores da mensagem de texto
            try{
                StringTokenizer tokens = new StringTokenizer(response," :");

                int hour = Integer.parseInt(tokens.nextToken().trim());
                int minute = Integer.parseInt(tokens.nextToken().trim());
                int second = Integer.parseInt(tokens.nextToken().trim());

                System.out.println("Horas: " + hour + " ; Minutos: " + minute + " ; Segundos: " + second);
            }catch(NumberFormatException e){}


        // 8º Tratar exceções
        }catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(... e){
            System.out.println("Nao foi recebida qualquer resposta (timeout):\n\t"+e);
        }catch(... e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            // 9º fechar socket
            if(socket != null){
                ...
            }
        }
    }

}

