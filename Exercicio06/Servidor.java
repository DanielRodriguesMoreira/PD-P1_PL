import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Servidor {

    public static final int MAX_SIZE = 4000;

    public static void main(String[] args) {

        File localDirectory;
        String requestedFileName, requestedCanonicalFilePath = null;
        FileInputStream requestedFileInputStream = null;
        DatagramSocket socket = null;
        int listeningPort;
        DatagramPacket packet;
        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        if (args.length != 2) {
            System.out.println("Sintaxe: java Servidor listeningPort localRootDirectory");
            return;
        }

        localDirectory = new File(args[1].trim());

        /**
         * Nas linhas seguintes estamos métodos da classe File que permitem fazer validações ao nível da directoria.
         * 1º Se a directoria existe
         * 2º se é uma directoria
         * 3º se temos permissões para escrita
         */
        if (!localDirectory.exists()) {
            System.out.println("A directoria " + localDirectory + " nao existe!");
            return;
        }
        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
            return;
        }
        if (!localDirectory.canRead()) {
            System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
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
                 * Na linha seguinte estamos a popular a variável requestedFileName com a mensagem que recebemos do cliente,
                 * que neste caso é o nome do ficheiro que o cliente quer obter.
                 */
                requestedFileName = new String(packet.getData(), 0, packet.getLength()).trim();

                System.out.println("Recebido pedido para \"" + requestedFileName + "\" de " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                /**
                 * Nas linhas seguintes estamos a verificar se temos acesso ao ficheiro que o cliente pretende obter.
                 */
                requestedCanonicalFilePath = new File(localDirectory + File.separator + requestedFileName).getCanonicalPath();
                if (!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                    System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                    System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath() + "!");
                    continue;
                }

                /**
                 * Na linha estamos a abrir o ficheiro para leitura (input). Para isso podemos usar a classe FileInputStream em que
                 * usamos o seu construtor para especificar que ficheiro queremos efetivamente abrir para leitura.
                 */
                requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath);
                System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");

                /**
                 * Este ciclo do...while() irá ser executado enquanto houverem bytes para serem lidos do ficheiro, ou seja,
                 * enquanto não tivermos lido todo o ficheiro.
                 */
                do {
                    /**
                     * Na linha seguinte vamos ler um chunk do ficheiro, neste caso especificámos que os chunks serão de
                     * 4000 bytes no máximo.
                     */
                    nbytes = requestedFileInputStream.read(fileChunk);

                    /**
                     * O if seguinte é usado porque ao tentar ler um chunk que já não existe, o nbytes será -1 logo
                     * estamos a meter a zero para parar o ciclo e enviar ao cliente a dizer que terminámos de enviar o ficheiro
                     */
                    if (nbytes == -1) {
                        nbytes = 0;
                    }

                    /**
                     * Nas linhas seguintes estamos a atualizar o datagram com os dados que queremos enviar.
                     *
                     * NOTA: ao especificarem os dados de um datagram devem também especificar o tamanho dos dados que querem enviar.
                     */
                    packet.setData(fileChunk, 0, nbytes);
                    packet.setLength(nbytes);

                    /**
                     * Na linha seguinta vamos utilizar o método send() do nosso socket para efetivamente enviar o datagram packet.
                     *
                     * NOTA: o endereço IP e porto já se encontram definidos no DatagramPacket (ver explicação na linha 51)
                     */
                    socket.send(packet);


                } while (nbytes > 0);

                System.out.println("Transferencia concluida");

                requestedFileInputStream.close();
                requestedFileInputStream = null;
            }

        /**
         * Nas linhas seguintes estamos a fazer o tratamento de exceções.
         */
        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu uma excepcao ao nivel do socket UDP:\n\t" + e);
        } catch (FileNotFoundException e) {   //Subclasse de IOException
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro " + requestedCanonicalFilePath + "!");
        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
        } finally {
            /**
             * Nas linha seguintes estamos a libertar os recursos, nesta caso a fechar o socket e o input stream.
             *
             * NOTA: se usarem try-with-resouces evitam de ter este bloco finally.
             */
            if (socket != null) {
                socket.close();
            }
            if (requestedFileInputStream != null) {
                try {
                    requestedFileInputStream.close();
                } catch (IOException ex) {
                }
            }
        } //try

    } // main
}
