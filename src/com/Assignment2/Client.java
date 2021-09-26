package com.Assignment2;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
        private String name;
        public Client(String name){
            this.name = name;
    }

    public static void main(String args[]) throws EOFException {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your name");
            String name = sc.nextLine();
            System.out.println("Type a message to send");
        try {
            InetAddress ia;
            ia = InetAddress.getByName("localhost");
            Socket client = new Socket(ia, 2031);
            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            DataInputStream inputStream = new DataInputStream(client.getInputStream());
            outputStream.writeUTF(name+":is online");
            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String msgRecieved = inputStream.readUTF();
                            System.out.println(msgRecieved);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Thread writeMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                while (true){
                    String msgToBeSent = sc.nextLine();
                    if(msgToBeSent.equals("exit")){

                        try {
                            outputStream.writeUTF(name+":exit");
                            readMessage.stop();
                            break;

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    try {
                        outputStream.writeUTF(name+":"+msgToBeSent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                }
            });
            readMessage.start();
            writeMessage.start();


        } catch (Throwable t) {
            System.out.println("Exception occurred in client "+name+" "+t);
        }
    }
}
