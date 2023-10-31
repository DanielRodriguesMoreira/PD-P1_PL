package sqlite_example;

import sqlite_example.resources.Dto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SqliteExampleClient {
    public static void main(String[] args) {
        InetAddress serverAddr;
        int serverPort;

        if (args.length != 2) {
            System.out.println("Sintaxe: java SqliteExampleClient <server ip> <server port>");
            return;
        }

        try {
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            try (Socket socket = new Socket(serverAddr, serverPort)) {

                try (ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream oin = new ObjectInputStream(socket.getInputStream())) {

                    oout.writeObject(new Dto("GET"));
                    oout.flush();

                    Dto dto = (Dto) oin.readObject();
                    System.out.println(dto.getResponse());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Ocorreu um erro ao nivel da serialização:\n\t" + e);
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
