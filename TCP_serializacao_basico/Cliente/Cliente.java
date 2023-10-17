import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Cliente {
    public static final String REQUEST = "GIVE_ME_THE_RESOURCES";

    public static void main(String[] args) {
        InetAddress serverAddr;
        int serverPort;
        Recursos response;

        if (args.length != 2) {
            System.out.println("Sintaxe: java Cliente serverAddress serverPort");
            return;
        }

        try {
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            try (Socket socket = new Socket(serverAddr, serverPort)) {

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
                 *  - ESCRITA: linhas 46 e 47 (devemos sempre fazer o flush para garantir que se escreve tudo)
                 *  - LEITURA: linha 49 (devemos fazer cast para garantir que lemos o objeto corretamente)
                 */
                try (ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

                    oout.writeObject(REQUEST);
                    oout.flush();

                    response = (Recursos) oin.readObject();

                    System.out.println("Recursos: ");
                    System.out.println("Github: " + response.getGithub());
                    System.out.println("Nonio: " + response.getNonio());
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
