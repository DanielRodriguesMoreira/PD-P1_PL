import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Cliente {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5; //segundos

    public static void main(String[] args) {
        File localDirectory;
        String fileName, localFilePath = null;
        InetAddress serverAddr;
        int serverPort;
        DatagramSocket socket = null;
        DatagramPacket packet;
        FileOutputStream localFileOutputStream = null;
        int contador = 0;

        /**
         * Nas linhas seguintes vamos testar a sintaxe.
         *
         * NOTA: isto não é importante para a realização do exercício, no entanto, é algo que devem fazer na resolução
         *       dos vossos exercícios.
         */
        if (args.length != 4) {
            System.out.println("Sintaxe: java Cliente serverAddress serverUdpPort fileToGet localDirectory");
            return;
        }

        /**
         * Nas linhas seguintes estamos a popular as variáveis fileName e localDirectory com os valores que obtemos
         * quando executamos a aplicação no terminal.
         */
        fileName = args[2].trim();
        localDirectory = new File(args[3].trim());

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
        if (!localDirectory.canWrite()) {
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }

        try {

            try {
                /**
                 * Nas linhas seguintes vamos criar o objeto que irá ser usado para escrever (output) o ficheiro que
                 * irá ser recebido via datagrama.
                 *
                 * NOTA: neste ponto ainda não estamos a escrever nada no ficheiro, apenas o abrimos para escrita e por isso
                 *       este já foi criado na directoria e com o nome que especificámos.
                 */
                localFilePath = localDirectory.getCanonicalPath() + File.separator + fileName;
                localFileOutputStream = new FileOutputStream(localFilePath);

                System.out.println("Ficheiro " + localFilePath + " criado.");
            } catch (IOException e) {
                if (localFilePath == null) {
                    System.out.println("Ocorreu a excepcao {" + e + "} ao obter o caminho canonico para o ficheiro local!");
                } else {
                    System.out.println("Ocorreu a excepcao {" + e + "} ao tentar criar o ficheiro " + localFilePath + "!");
                }
                return;
            }

            try {

                /**
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

                /**
                 * Na linha seguinte estamos a criar um DatagramSocket. É através deste que vamos enviar/receber datagramas.
                 *
                 * NOTA: estamos a usar um construtor sem parâmetros, ou seja, não estamos a definir nenhum porto. Ao não
                 *       definir nenhum porto estamos a permitir que o sistema nos atribua um porto automaticamente.
                 */
                socket = new DatagramSocket();

                /**
                 * Na linha seguinta estamos a especificar um tempo de timeout.
                 * Isto faz com o que ao usar a função receive(), esta fique bloqueada à espera de resposta por X tempo
                 * (neste caso o timeout é definido pelo valor da constante TIMEOUT)
                 */
                socket.setSoTimeout(TIMEOUT * 1000);

                /**
                 * Na linha seguinte estamos a criar o DatagramPacket.
                 * Na analogia da carta de correio, este é o passo em que estamos a escrever a nossa carta e vamos especificar,
                 * além do conteúdo da carta, o tamanho do conteúdo e o endereço IP e porto do destinatário (para onde queremos enviar)
                 *
                 * NOTA: neste caso a mensagem que estamos a enviar é o nome do ficheiro que queremos obter.
                 */
                packet = new DatagramPacket(fileName.getBytes(), fileName.length(), serverAddr, serverPort);

                /**
                 * Na linha seguinta vamos utilizar o método send() do nosso socket para efetivamente enviar o datagram packet.
                 */
                socket.send(packet);

                /**
                 * O ciclo do...while() irá ser executado enquanto o tamanho do datragram recebido for maior que 0.
                 * Foi desenvolvido desta forma porque no enunciado do exercício está especificado que o servidor enviará
                 * dados enquanto não chegar ao fim do ficheiro, quando chegar ao fim do ficheiro enviará um datagram vazio.
                 */
                do {
                    /**
                     * Nas linhas seguintes estamos à espera de uma resposta usando o método receive() do socket.
                     *
                     * NOTA: o método receive() é bloqueante, ou seja, o código ficará parado nesta linha até receber um datagram
                     *       ou até ser atingido o timeout (se especificado)
                     *
                     * NOTA2: Porque estamos a criar um novo DatagramPacket para receber invés de usar o que utilizámos para enviar?
                     *        Isto porque a mensagem que recebemos pode ter um tamanho diferente da mensagem que enviámos.
                     */
                    packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                    socket.receive(packet);

                    /**
                     * Este if está a usar os métodos getPort() e getAddress() do DatagramPacket para verificar se a mansagem
                     * recebida veio efetivamente do servidor.
                     */
                    if (packet.getPort() == serverPort && packet.getAddress().equals(serverAddr)) {
                        System.out.println("Recebido o bloco n. " + ++contador + " com " + packet.getLength() + " bytes.");

                        /**
                         * É na linha seguinte que estamos a escrever os dados recebidos para dentro no nosso ficheiro.
                         */
                        localFileOutputStream.write(packet.getData(), 0, packet.getLength());

                        System.out.println("Acrescentados " + packet.getLength() + " bytes ao ficheiro " + localFilePath + ".");
                    }

                } while (packet.getLength() > 0);

                System.out.println("Transferencia concluida.");

            /**
             * Nas linhas seguintes estamos a fazer o tratamento de exceções.
             */
            } catch (UnknownHostException e) {
                System.out.println("Destino desconhecido:\n\t" + e);
            } catch (NumberFormatException e) {
                System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
            } catch (SocketTimeoutException e) {
                System.out.println("Nao foi recebida qualquer bloco adicional, podendo a transferencia estar incompleta:\n\t" + e);
            } catch (SocketException e) {
                System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + e);
            } catch (IOException e) {
                System.out.println("Ocorreu um erro no acesso ao socket ou ao ficheiro local " + localFilePath + ":\n\t" + e);
            }

        } finally {
            /**
             * Nas linha seguintes estamos a libertar os recursos, nesta caso a fechar o socket e o output stream.
             *
             * NOTA: se usarem try-with-resouces evitam de ter este bloco finally.
             */
            if (socket != null) {
                socket.close();
            }
            if (localFileOutputStream != null) {
                try {
                    localFileOutputStream.close();
                } catch (IOException e) {
                }
            }

        }

    }

}
