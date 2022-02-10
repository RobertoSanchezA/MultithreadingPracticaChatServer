package com.company;

import com.company.monitor.Monitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static Scanner scan = new Scanner(System.in);
    static Monitor messages = new Monitor();

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

    public Main(Monitor messages) {
        Main.messages = messages;
    }

    static class Client extends Thread {
        private Socket s = null;
        private ObjectInputStream ois = null;
        private ObjectOutputStream oos = null;
        public Client(Socket socket) {
            this.s = socket;
        }

        public boolean msgReceived(String msg){
            String message = msg.substring(0, 8);
            return message.equals("message:");
        }
        public boolean clientDisconnected(String msg){
            if (msg.equals("bye")) return true;
            else return false;
        }

        public synchronized void run() {
            System.out.println("Conexion recibida desde " + s.getInetAddress());

            try {
                ois = new ObjectInputStream(s.getInputStream());
                oos = new ObjectOutputStream(s.getOutputStream());

                String username = "";
                boolean validMsg = true;
                boolean justConnected = true;

                while (validMsg) {
                    //leo nombre de usuario
                    if(justConnected){
                        username = ois.readObject().toString();
                        oos.writeObject(messages.get());
                        justConnected = false;
                    }

                    String newMsg = (String) ois.readObject();
                    if(msgReceived(newMsg)) {

                        //elimino la parte de 'message:' y guardo
                        // el mensaje en el arrayList
                        newMsg = newMsg.substring(8);
                        messages.put(newMsg);
                        //envio todos los mensajes al cliente
                        oos.writeObject(messages.get());

                    }else if(clientDisconnected(newMsg)){
                        oos.writeObject("good bye");
                        validMsg = false;
                    } else {
                        //si el cliente no escribe bye o message:
                        //envio mensaje de error
                        oos.writeObject("mensaje erróneo");
                        System.out.println("error");
                        validMsg = false;
                    }
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
