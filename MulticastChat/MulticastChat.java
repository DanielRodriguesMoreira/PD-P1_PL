package exercicio12;

/*
   Multicast IPv4 em Mac OS
   Na lina de comando: -Djava.net.preferIPv4Stack=true
   No codigo: System.setProperty("java.net.preferIPv4Stack", "true");
*/

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

class Msg implements Serializable {
    public static final long serialVersionUID = 1010L;

    protected String nickname;
    protected String msg;

    public Msg(String nickname, String msg) {
        this.nickname = nickname;
        this.msg = msg;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMsg() {
        return msg;
    }

}

public class MulticastChat extends Thread {
    public static final String LIST = "LIST";
    public static String EXIT = "EXIT";
    public static int MAX_SIZE = 1000;

    protected String username;
    protected MulticastSocket s;
    protected boolean running;

    public MulticastChat(String username, MulticastSocket s) {
        this.username = username;
        this.s = s;
        running = true;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        Object obj;
        DatagramPacket pkt;
        Msg msg;

        if (s == null || !running) {
            return;
        }

        try {

            while (running) {

                pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                s.receive(pkt);

                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()))) {

                    obj = in.readObject();

                    if (obj instanceof Msg) {

                        msg = (Msg) obj;

                        if (msg.getMsg().toUpperCase().contains(LIST.toUpperCase())) {

                            try (ByteArrayOutputStream buff = new ByteArrayOutputStream();
                                 ObjectOutputStream out = new ObjectOutputStream(buff)) {

                                out.writeObject(username);

                                pkt.setData(buff.toByteArray());
                                pkt.setLength(buff.size());
                            }

                            s.send(pkt);
                            continue;
                        }

                        System.out.println();
                        System.out.print("(" + pkt.getAddress().getHostAddress() + ":" + pkt.getPort() + ") ");
                        System.out.println(msg.getNickname() + ": " + msg.getMsg() + " (" + msg.getClass() + ")");

                    } else if (obj instanceof String) {

                        System.out.println((String) obj + " (" + obj.getClass() + ")");
                    }

                    System.out.println();
                    System.out.print("> ");

                } catch (ClassNotFoundException e) {
                    System.out.println();
                    System.out.println("Mensagem recebida de tipo inesperado! " + e);
                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida! " + e);
                } catch (Exception e) {
                    System.out.println();
                    System.out.println("Excepcao: " + e);
                }

            }

        } catch (IOException e) {
            if (running) {
                System.out.println(e);
            }
            if (!s.isClosed()) {
                s.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        InetAddress group;
        int port;
        MulticastSocket socket = null;
        DatagramPacket dgram;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        NetworkInterface nif;

        MulticastChat t = null;

        if (args.length != 4) {
            System.out.println("Sintaxe: java MulticastChat <nickname> <groupo multicast> <porto> <NIC multicast>");
            return;
        }

        try {
            group = InetAddress.getByName(args[1]);
            port = Integer.parseInt(args[2]);

            try {
                nif = NetworkInterface.getByInetAddress(InetAddress.getByName(args[3])); //e.g., 127.0.0.1, 192.168.10.1, ...
            } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex) {
                nif = NetworkInterface.getByName(args[3]); //e.g., lo, eth0, wlan0, en0, ...
            }

            socket = new MulticastSocket(port);

            socket.joinGroup(new InetSocketAddress(group, port), nif);

            t = new MulticastChat(args[0], socket);
            t.start();

            System.out.print("> ");

            while (true) {
                msg = in.readLine();

                if (msg.equalsIgnoreCase(EXIT)) {
                    break;
                }

                try (ByteArrayOutputStream buff = new ByteArrayOutputStream();
                     ObjectOutputStream out = new ObjectOutputStream(buff)) {

                    out.writeObject(new Msg(args[0], msg));
                    dgram = new DatagramPacket(buff.toByteArray(), buff.size(), group, port);
                }

                socket.send(dgram);
            }

        } finally {
            if (t != null) {
                t.terminate();
            }

            if (socket != null) {
                socket.close();
            }
        }

    }
}
