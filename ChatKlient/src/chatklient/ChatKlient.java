package chatklient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatKlient {

    public static void main(String[] args) {
        String hostname = "localhost"; //args[0];
        int serverPort =  9000; //Integer.parseInt(args[1]);

        try (
                Socket echoSocket = new Socket(hostname, serverPort);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream());
                Scanner in = new Scanner(echoSocket.getInputStream());
                Scanner stdIn = new Scanner(System.in); 
            )
        {
                ReaderFromServer rfs = new ReaderFromServer(echoSocket, in);
                WriterToServer wts = new WriterToServer(echoSocket, out, stdIn);
                rfs.start();
                wts.start();
                
                try{
                   rfs.join();
                   wts.join();
                }catch(InterruptedException e) {
                    System.out.println("InterruptedException");
                }   
                
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReaderFromServer extends Thread {
    private Socket socket;
    private Scanner input;

    public ReaderFromServer(Socket socket, Scanner input) {
        this.socket = socket;
        this.input = input;
    }

    public void run() {
        while(input.hasNextLine()) {
            String line = input.nextLine();
            System.out.println("received: " + line);
        }
    }
          
}

class WriterToServer extends Thread {
    private Socket socket;
    private PrintWriter output;
    private Scanner fromconsole;
    private int id = 1;

    public WriterToServer(Socket socket, PrintWriter output, Scanner fromconsole) {
        this.socket = socket;
        this.output = output;
        this.fromconsole = fromconsole;
    }

    public void run() {
        while(true) {
            String msg = fromconsole.nextLine();
            output.println(msg);
            output.flush();
            System.out.println("sent: " + msg);
        }   
    }
}


