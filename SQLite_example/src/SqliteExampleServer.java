package sqlite_example;

import sqlite_example.db_crud.ManageDb;
import sqlite_example.resources.Dto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SqliteExampleServer {
    public static void main(String[] args) {
        int listeningPort;

        if (args.length != 3) {
            System.out.println("Sintaxe: java SqliteExample <listening port> <SGBD address> <BD name>");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);

        ManageDb manageDb = new ManageDb(args[1], args[2]);

        try (ServerSocket s = new ServerSocket(listeningPort)) {

            while (true) {
                try (Socket toClient = s.accept()) {

                    try (ObjectInputStream oin = new ObjectInputStream(toClient.getInputStream());
                         ObjectOutputStream oout = new ObjectOutputStream(toClient.getOutputStream())) {

                        Dto dto = (Dto) oin.readObject();

                        if (dto.getRequest().equalsIgnoreCase("GET")) {
                            String dbResults = manageDb.getResults();
                            dto.setResponse(dbResults);
                        }

                        oout.writeObject(dto);
                        oout.flush();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        } finally {
            manageDb.clearDb();
        }
    }
}
