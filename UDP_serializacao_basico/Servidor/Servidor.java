import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Servidor {
    public static final String REQUEST = "GIVE_ME_THE_RESOURCES";
    public static final int MAX_SIZE = 10000;

    public static void main(String[] args) {
        Integer listeningPort;
        DatagramPacket packet;
        String request;

        if (args.length != 1) {
            System.out.println("Sintaxe: java Servidor listeningPort");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);
        try (DatagramSocket socket = new DatagramSocket(listeningPort)) {

            while (true) {

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
                    request = (String) oin.readObject();
                }

                System.out.println("Recebido \"" + request + "\" de " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                if (request.equalsIgnoreCase(REQUEST)) {

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

                        Recursos response = new Recursos();
                        oout.writeObject(response);
                        oout.flush();

                        packet.setData(bout.toByteArray());
                        packet.setLength(bout.size());
                    }

                    socket.send(packet);
                }
            }

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
