package com.Assignment2;

import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    static List<ClientHandler> ls = new ArrayList<>();

    public static void main(String args[]) throws IOException {
        ServerSocket server = new ServerSocket(2031);
        System.out.println("Server started");
        while(true) {
            try {


                //int clientNo = 8;
                //System.out.println("Waiting for client");
                Socket socket = server.accept();
                System.out.println("New connection from " + socket);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("Assigning new thread for the client");
                ClientHandler clientHandler;
                clientHandler = new ClientHandler(socket,inputStream,outputStream);
                ls.add(clientHandler);
                clientHandler.start();

            } catch (IOException e) {
                System.out.println("Unable to start server. Exception: " + e);
            }
        }

    }
     public static class ClientHandler extends Thread{
        private Socket socket;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private  boolean isActive;
        ClientHandler(Socket so, DataInputStream dis, DataOutputStream dos)
        {
            this.socket=so;
            this.dataInputStream=dis;
            this.dataOutputStream=dos;
            this.isActive = true;
        }
        @Override
        public void run() {
            //Scanner scanner = new Scanner(System.in);
            try{
                while(true){
                    //String msg = scanner.nextLine();
                    String inputMsg = this.dataInputStream.readUTF();
                    String[] parseString = inputMsg.split(":");
                    String msg = parseString[1];
                    if(msg.equals("exit")){
                        this.isActive = false;
                        System.out.println("Exiting connection from client "+ parseString[0]);
                        for(ClientHandler clientHandler:Server.ls){
                            if(clientHandler.isActive && clientHandler.socket!=this.socket){
                                clientHandler.dataOutputStream.writeUTF(parseString[0] + ":is offline");
                            }
                        }
                        this.dataInputStream.close();
                        this.dataOutputStream.close();
                        this.socket.close();
                        System.out.println("Connection closed");
                        break;
                    }
                    System.out.println(inputMsg);
                    // sending msg to all clients except the one that sends the message
                    for(ClientHandler clientHandler:Server.ls){
                        if(clientHandler.isActive && clientHandler.socket!=this.socket){
                            clientHandler.dataOutputStream.writeUTF(inputMsg);
                        }
                    }
                    //this.dataOutputStream.writeUTF(msg);
                }
            }catch (Exception e){
                System.out.println("An exception occurred. Exception : "+e);
            }

        }
    }
}
