import java.io.*;
import java.net.*;

public class Cliente {
    public static final String REQUEST = "GIVE_ME_THE_RESOURCES";
    public static final int MAX_SIZE = 10000;

    public static void main(String[] args) {
        InetAddress serverAddr;
        int serverPort;
        DatagramPacket packet;
        Recursos response;

        if (args.length != 2) {
            System.out.println("Sintaxe: java Cliente serverAddress serverUdpPort");
            return;
        }

        try (DatagramSocket socket = new DatagramSocket()) {

            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            /*
             * UDP é orientado a Datagramas, logo temos que arranjar forma de escrever/ler array de bytes.
             *
             * Nas linhas seguintes como queremos escrever um objeto serializado temos que usar as classes auxiliares
             * de output: ByteArrayOutputStream e ObjectOutputStream
             *
             * Receita:
             *
             *      1º Instanciar um objeto do tipo ByteArrayOutputStream
             *      2º Instanciar um objeto do tipo ObjectOutputStream usando o objeto criado no passo anterior
             *      3º Escrever o objeto serializado usando o ObjetOutputStream e fazer flush para garantir que se escreve tudo
             *      4º Criar o DatagramPacket e usar o objeto do tipo ByteArrayOutputStream para converter o objeto serializado
             *         (escrito no passo anterior) para um array de bytes que será usado no packet
             */
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
                 ObjectOutputStream oout = new ObjectOutputStream(bout)) {

                oout.writeObject(REQUEST);
                oout.flush();

                packet = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr, serverPort);
            }

            socket.send(packet);

            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            /*
             * UDP é orientado a Datagramas, logo temos que arranjar forma de escrever/ler array de bytes.
             *
             * Nas linhas seguintes como queremos ler um objeto serializado temos que usar as classes auxiliares
             * de input: ByteArrayInputStream e ObjectInputStream
             *
             * Receita:
             *
             *      1º Instanciar um objeto do tipo ByteArrayInputStream com os dados (data e length) recebidos no DatagramPacket
             *      2º Instanciar um objeto do tipo ObjectInputStream usando o objeto criado no passo anterior
             *      3º Ler o objeto deserializado usando o ObjetInputStream e fazer cast para garantir que lemos o objeto corretamente
             */
            try (ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                 ObjectInputStream oin = new ObjectInputStream(bin)) {
                response = (Recursos) oin.readObject();
            }

            System.out.println("Recursos: ");
            System.out.println("Github: " + response.getGithub());
            System.out.println("Nonio: " + response.getNonio());

        } catch (ClassNotFoundException e) {
            System.out.println("O objecto recebido não é do tipo esperado:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t" + e);
        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }
    }
}
