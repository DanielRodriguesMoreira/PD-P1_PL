import java.net.*;
import java.io.*;

public class servidor {
    public static final int MAX_SIZE = 256;

    public static void main(String[] args) {

        int listeningPort;
        DatagramSocket socket = null;
        DatagramPacket packet;
        String receivedMsg;

        try {
            // Na linha seguinte vamos criar um DatagramSocket.
            // Notem que estamos a passar o porto como argumento para o construtor. Ao contrário do cliente
            // aqui precisamos de especificar que porto queremos usar, pois temos que ter essa informação
            // para os clientes terem a possibilidade de enviar DatagramPackets.
            listeningPort = Integer.parseInt(args[0]);
            socket = new DatagramSocket(listeningPort);

            while(true){

                // Nas linhas seguintes vamos criar um DatagramPacket e usar o DatagramSocket (criado anteriormente)
                // para receber. Mais uma vez notem que o método receive() é uma operação bloqueante.
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                // Nas linhas seguintes vamos mostrar os dados recebidos.
                // Notem que, mais uma vez, estamos a usar a classe String para fazer decode dos dados.
                // Notem, também, que estamos a fazer print do endereço (IP e porto) da origem do DatagramPacket recebido.
                // Isto é possível porque o DatagramPacket contém informação da origem do pacote e pode ser acedido
                // através dos métodos getAddress() e getPort() disponíveis na classe DatagramPacket.
                receivedMsg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Recebi \"" + receivedMsg + "\" de " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                // Nas linhas seguintes estamos a usar o mesmo DatagramPacket para enviar uma resposta ao cliente.
                // Notem que usamos os métodos setData() e setLength() para atribuir os dados/mensagem e o tamanho.
                // Notem, também, que ao não definir o tamanho (linha 48) então o DatagramPacket terá o tamanho
                // que foi definido inicialmente (linha 26), o que faz com que seja enviado "lixo" na mensagem.
                // Exemplo:
                //      - definimos o tamanho inicial de 256 bytes
                //      - a seguir dizemos que a mensagem é "Olá cliente!"
                //      - ao não definirmos o tamanho novamente, então o DatagramPacket terá 256 bytes mas a mensagem
                //      tem apenas 12 bytes (Olá cliente!), logo os restantes bytes terão "lixo"
                byte[] responseMsg = "Olá cliente!".getBytes();
                packet.setData(responseMsg);
                packet.setLength(responseMsg.length);

                // Na linha seguinte usamos o DatagramSocket para enviar o DatagramPacket.
                // Notem que não definimos em lugar nenhum o endereço IP do cliente, pois essa informação já está no
                // DatagramPacket quando o recebemos (linha 27)
                socket.send(packet);
            }

        // Nas linhas seguintes estamos a fazer o tratamento das exceções.
        // Notem que apesar de ser uma coisa "chata" faz todo o sentido e deve ser feito (diria que isto
        // será alvo de avaliação no trabalho prático e/ou exame)
        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            // Na linha seguinte estamos a fechar o closet uma vez que a aplicação cliente vai terminar
            // Notem que devem sempre fechar e limpar recursos.
            if(socket != null){
                socket.close();
            }
        }
    }
}
