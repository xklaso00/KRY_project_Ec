package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver {
    private JPanel mainPanelReceiver;
    private JButton listenButton;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;



    public Receiver() {
            JFrame frame = new JFrame("Hello2");
            frame.setBounds(100, 100, 450, 300);
            frame.setContentPane(new Receiver().mainPanelReceiver);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

        listenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try(ServerSocket serverSocket = new ServerSocket(5000)){
                    System.out.println("listening to port:5000");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(clientSocket+" connected.");
                    dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    receiveFile("NewFile1.pdf");
                    receiveFile("NewFile2.pdf");

                    dataInputStream.close();
                    dataOutputStream.close();
                    clientSocket.close();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private static void receiveFile(String fileName) throws Exception{
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong();     // read file size
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }
}
