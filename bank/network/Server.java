package bank.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(10001, 5);
            Socket clientConnection = null;
            PrintWriter output = null;
            BufferedReader input = null;
            while (true) {
                try {


                    System.out.println("Waiting for a connection.....");
                    clientConnection = serverSocket.accept();
                    System.out.println("Connection accepted from " + clientConnection.getInetAddress().getHostName());

                    System.out.println("Getting Data Streams");
                    output = new PrintWriter(clientConnection.getOutputStream(), true);
                    input = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));

                    Thread.sleep(3000);
                    output.println("Connected to the Server");//send to server

                    String clientMessage = input.readLine();
                    System.out.println("CLIENT SAID  >>>>>>>" + clientMessage);

                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } finally {
                    try {
                        input.close();
                        output.close();
                        clientConnection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (IOException ioe) {
            System.out.println("\n++++++ CANNOT OPEN THE SERVER  ++++++++");
            ioe.printStackTrace();
        }
    }


}
