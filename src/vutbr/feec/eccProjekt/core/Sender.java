package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Sender {
    private JFrame frame;
    private JPanel mainPanelSender;
    private JButton browseButton;
    private JButton sendButton;
    private PrintWriter pw;

    private File fileToSend;


    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello");
        frame.setBounds(100, 100, 450, 300);
        frame.setContentPane(new Sender().mainPanelSender);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        new Sender();

        new Receiver();
    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

    public Sender() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == browseButton) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory((new File("")));

                    int respone = fileChooser.showSaveDialog(null);
                    if(respone == JFileChooser.APPROVE_OPTION) {
                        File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                        fileToSend = file;
                    }
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try(Socket socket = new Socket("localhost",5000)) {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    sendFile(String.valueOf(fileToSend));

                    dataInputStream.close();
                    dataInputStream.close();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }
}
