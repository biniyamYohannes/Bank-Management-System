package bank.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket serverConnection = null;
        BufferedReader input = null;
        PrintWriter output = null;
        try {
            serverConnection = new Socket( "localhost", 10001);
            output = new PrintWriter(serverConnection.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));

            String serverMessage = input.readLine();
            System.out.println("Server Said>>>"+ serverMessage);

            output.println("this is the sent out message from client");
        }
        catch (IOException e){
            e.printStackTrace ();
        }
        finally {
            try {
                serverConnection.close();
            }
            catch (IOException|NullPointerException e) {
                e.printStackTrace();
            }
        }
    }



}
