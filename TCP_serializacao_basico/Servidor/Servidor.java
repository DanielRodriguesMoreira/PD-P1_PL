import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Servidor {
    public static final String REQUEST = "GIVE_ME_THE_RESOURCES";

    public static void main(String[] args) {
        int listeningPort;
        String request;

        if (args.length != 1) {
            System.out.println("Sintaxe: java Servidor listeningPort");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {

                    /*
                     * TCP é orientado a stream de dados, logo temos que arranjar forma de escrever/ler objetos.
                     *
                     * TCP é mais simples que UDP pois não temos que converter os objetos em arrays de bytes. Por isso,
                     * basta usar as classes ObjectOutputStream (para escrever) e ObjectInputStream (para ler).
                     *
                     *
                     * Nas linhas seguintes vamos usar o try-with-resources para instanciar 2 objetos:
                     *  - ESCRITA: objeto do tipo ObjectOutputStream (usando o output stream do socket)
                     *  - LEITURA: objeto do tipo ObjectInputStream (usando o input stream do socket)
                     *
                     * Depois só temos que usar os objetos criados anteriormente para escrever e ler:
                     *  - LEITURA: linha 45 (devemos fazer cast para garantir que lemos o objeto corretamente)
                     *  - ESCRITA: linhas 52 e 53 (devemos sempre fazer o flush para garantir que se escreve tudo)
                     */
                    try (ObjectOutputStream oout = new ObjectOutputStream(clientSocket.getOutputStream());
                         ObjectInputStream oin = new ObjectInputStream(clientSocket.getInputStream())) {

                        request = (String) oin.readObject();

                        System.out.println("Recebido \"" + request + "\" de " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                        if (request.equalsIgnoreCase(REQUEST)) {
                            Recursos response = new Recursos();
                            oout.writeObject(response);
                            oout.flush();
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("O objecto recebido não é do tipo esperado:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nível do socket TCP:\n\t" + e);
        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        }
    }
}
