import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by jianbojia on 12/17/16.
 */
public class ChatClient extends Frame {

    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;
    private boolean connected;

    TextField tfText = new TextField();
    TextArea taContent = new TextArea();

    Thread tRecv = new Thread(new ReceiveThread());

    public static void main(String[] args) {
        new ChatClient().launchFrame();

    }

    public void launchFrame() {
        setLocation(300, 300);
        this.setSize(300, 300);
        add(tfText, BorderLayout.SOUTH);
        add(taContent, BorderLayout.NORTH);
        pack();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                disConnect();
                System.exit(0);
            }
        });
        tfText.addActionListener(new TFListener());
        setVisible(true);
        connect();

        tRecv.start();
    }

    public void connect() {
        try {
            socket = new Socket("127.0.0.1", 8888);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            connected = true;
            System.out.println("Connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disConnect() {
        try {
            dos.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TFListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String str = tfText.getText().trim();
            tfText.setText("");

            try {
                dos.writeUTF(str);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class ReceiveThread implements Runnable {

        @Override
        public void run() {
            try {
                while (connected) {
                    String str = dis.readUTF();
                    System.out.println(str);
                    taContent.setText(taContent.getText() + str + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
