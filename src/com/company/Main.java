package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Main {
    public static ArrayList usernames = new ArrayList();
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Server started on port " + serverSocket.getLocalPort());

        while (true) {
            socket = serverSocket.accept();
            Client client = new Client(socket);
            client.start();
        }
    }
    static class Client extends Thread {
        private Socket s = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;

        public Client(Socket socket) {
            this.s = socket;
        }

        public void run() {
            System.out.println("Conexion recibida desde " + s.getInetAddress());
            try {
                ois = new ObjectInputStream(s.getInputStream());
                oos = new ObjectOutputStream(s.getOutputStream());

                //guardo el nombre de usuario y lo agrego a la lista
                String nombreUsuario = (String)ois.readObject();
                usernames.add(nombreUsuario);

                //envio saludo
                String saludo = "Hola manin " + nombreUsuario;
                oos.writeObject(saludo);

                //confirmo en el servidor que se ha enviado el saludo
                System.out.println("Saludo enviado a " + nombreUsuario + " desde " + s.getInetAddress());

                for (Object username : usernames) {
                    System.out.println("Usuarios registrados:");
                    System.out.println(username);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(oos != null)oos.close();
                    if(ois != null)ois.close();
                    if(s != null) s.close();
                    System.out.println("Se finí");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
