import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianbojia on 12/18/16.
 */
public class ChatServer {
    boolean started = false;
    ServerSocket ss = null;

    List<Client> clientList = new ArrayList<>();

    public static void main(String[] args) {
        new ChatServer().start();
    }

    public void start() {
        try {
            ss = new ServerSocket(8888);
            started = true;
        } catch (BindException e) {
            System.out.println("Port in use");
            System.out.println("Please close the program and reboot server");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (started) {
                Socket socket = ss.accept();
                Client client = new Client(socket);
                clientList.add(client);
                System.out.println("A client connected");
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class Client implements Runnable {

        private Socket socket;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;
        private boolean connected = false;

        public Client(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String str) {
            try {
                dos.writeUTF(str);
            } catch (SocketException e) {
                clientList.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (connected) {
                    String str = dis.readUTF();
                    System.out.println(str);

                    for (int i = 0; i < clientList.size(); i++) {
                        Client client = clientList.get(i);
                        client.send(str);
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                System.out.println("Client closed!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }

                    if (dos != null) {
                        dos.close();
                    }

                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}