import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Scanner;

public class ChatServer {

    public static void main(String[] args) throws Exception {
        try {
            Accepter server = new Accepter();
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class Accepter extends Thread {
    public ServerSocket serverSocket;
    public List<Client> clients;

    public Accepter() {
        this.clients = new ArrayList<>();
    }

    public void run() {
        System.out.println("Starting accepter");
        try {
            this.serverSocket = new ServerSocket(9000);
            while (true) {
                Socket s = serverSocket.accept();
                System.out.println("New client: " + s.getRemoteSocketAddress());
                Client c = new Client(s, clients);
                c.start();
                synchronized (clients) {
                    clients.add(c);
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        }

    }
}

class Client extends Thread {
    Socket s;
    Scanner in;
    String name;
    PrintWriter out;
    List<Client> clients;

    public Client(Socket s, List<Client> clients) {
        this.s = s;
        this.clients = clients;
        try {
            this.in = new Scanner(s.getInputStream());
            this.out = new PrintWriter(s.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            out.println("Your name: ");
            out.flush();
            name = in.nextLine();

            String line;
            while (in.hasNextLine()) {
                line = in.nextLine();
                if (line.toLowerCase().equals("quit")) {
                    for (Client v : clients) {
                        if (v == this) {
                            synchronized (clients) {
                                v.out.println("Leave");
                                v.out.flush();
                                clients.remove(v);
                            }
                            break;
                        }
                    }
                } else {
                    Message(name + ": " + line);
                }
            }
            System.out.println("Client disconnected!" + "(" + name + ")");
            Message("Client disconnected!" + "(" + name + ")");
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() throws Exception {
        out.close();
        in.close();
        s.close();
    }

    private void Message(String message) {
        for (Client v : clients) {
            if (v != this) {
                synchronized (clients) {
                    v.sendMessage(message);
                }
            }
        }
    }

    private void sendMessage(String s) {
        out.println(s);
        out.flush();
    }
}