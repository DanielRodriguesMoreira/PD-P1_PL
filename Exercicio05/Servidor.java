import java.net.*;
import java.io.*;
import java.util.*;

public class Servidor {
    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        int listeningPort;
        DatagramSocket socket = null;
        DatagramPacket packet;
        String receivedMsg, timeMsg;
        Calendar calendar;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        if (args.length != 1) {
            System.out.println("Sintaxe: java Servidor listeningPort");
            return;
        }

        try {

            /**
             * Na linha seguinte estamos a criar um DatagramSocket. É através deste que vamos enviar/receber datagramas.
             *
             * NOTA: estamos a usar um construtor com um parâmetros pois queremos especificar que porto queremos usar.
             *       Ao contrário do cliente, no servidor queremos dizer que porto queremos usar especificamente, se fosse
             *       automático, depois o cliente não saberia para que porto teria que enviar os datagramas.
             */
            listeningPort = Integer.parseInt(args[0]);
            socket = new DatagramSocket(listeningPort);

            System.out.println("UDP Time Server iniciado...");

            while (true) {

                /**
                 * Nas linhas seguintes estamos à espera de datagramas usando o método receive() do socket.
                 *
                 * NOTA: o método receive() é bloqueante, ou seja, o código ficará parado nesta linha até receber um datagrama.
                 *       NOTEM que no servidor não estamos a especificar um tempo de timeout pois a ideia do servidor é ficar
                 *       à espera infinitamente por mensagems para fazer o tratamente e enviar a resposta.
                 *
                 * NOTA2: reparem que aqui no servidor, ao contrário do que acontece no cliente, não estamos a especificar o
                 *        IP e porto quando criamos o DatagramPacket. Isto porque, essa informação estará preenchida no
                 *        packet quando este for recebido.
                 *        Ou seja, quando recebermos o pacote este terá encapsulado o endereço IP e porto de onde veio a mensagem.
                 */
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                /**
                 * Nas linhas seguintes estamos a mostrar a mensagem que recebemos do cliente.
                 *
                 * NOTA: estamos a criar uma instância da classe String com os dados do pacote recebido lá dentro pois os dados vêm
                 *       codificados (codificação é diferente de encriptação!) e a classe String permite descodificar e converter
                 *       para algo que seja possível apresentar no println.
                 *
                 * NOTA2: ao mostrar a resposta estamos também a mostrar o IP e porto de onde o datagram foi recebido.
                 *        Essa informação está encapsulada no datagram e pode ser obtido:
                 *        IP -> packet.getAddress().getHostAddress()
                 *        Porto -> paget.getPort()
                 */
                receivedMsg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Recebido \"" + receivedMsg + "\" de " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                /**
                 * A linha seguinte é apenas uma verificação para não ignorar e não enviar resposta caso a mensagem recebida não seja igual a "TIME"
                 */
                if (!receivedMsg.equalsIgnoreCase(TIME_REQUEST)) {
                    continue;
                }

                /**
                 * Nas linhas seguintes estamos a obter a hora do calendário e a colocar essa informação na variável (timeMsg)
                 * que irá conter os dados que queremos enviar para o cliente
                 */
                calendar = Calendar.getInstance();
                timeMsg = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

                /**
                 * Nas linhas seguintes estamos a atualizar o datagram com os dados que queremos enviar.
                 *
                 * NOTA: ao especificarem os dados de um datagram devem também especificar o tamanho dos dados que querem enviar.
                 */
                packet.setData(timeMsg.getBytes());
                packet.setLength(timeMsg.length());

                /**
                 * Na linha seguinta vamos utilizar o método send() do nosso socket para efetivamente enviar o datagram packet.
                 *
                 * NOTA: o endereço IP e porto já se encontram definidos no DatagramPacket (ver explicação na linha 51)
                 */
                socket.send(packet); // Nota: o ip e porto de destino já se encontram definidos em packet (pois é um DatagramPacket)
            }

        /**
         * Nas linhas seguintes estamos a fazer o tratamento de exceções.
         */
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        } finally {
            /**
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
