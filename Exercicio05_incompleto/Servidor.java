import java.net.*;
import java.io.*;
import java.util.*;

public class Servidor {
    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        int listeningPort;
        ... socket = null;
        ... packet;
        String receivedMsg, timeMsg;
        Calendar calendar;

        // 1º testar sintaxe
        if(args.length != ...){
            System.out.println("Sintaxe: java Servidor listeningPort");
            return;
        }

        try{
            // 2º Criar socket
            listeningPort = Integer.parseInt(args[0]);
            socket = ...

            System.out.println("UDP Time Server iniciado...");

            while(true){

                // 3º receber datagram
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                ...


                receivedMsg = new String(packet.getData(), 0, packet.getLength());
                // mostrar origem da resposta (ip e porto)
                System.out.println("Recebido \"" + receivedMsg + "\" de " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                if(!receivedMsg.equalsIgnoreCase(TIME_REQUEST)){
                    continue;
                }

                // 4º criar/preparar resposta
                calendar = Calendar.getInstance();
                timeMsg = calendar.get(Calendar.HOUR_OF_DAY)+":" + calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);

                packet.setData(timeMsg.getBytes());
                packet.setLength(timeMsg.length());

                // 5º enviar resposta
                ...
            }

        // 6º tratar excepções
        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(... e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            // 7º fechar socket
            if(socket != null){
                ...
            }
        }
    }
}
