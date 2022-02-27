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

    private File[] fileToSend;


    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello");
        frame.setSize(450, 450);
        frame.setContentPane(new Sender().mainPanelSender);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        new Sender();

    }


    public Sender() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == browseButton) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory((new File(""))); //kde sa otvori chooser

                    int respone = fileChooser.showSaveDialog(null);
                    if(respone == JFileChooser.APPROVE_OPTION) { //ci sa vlozil subor alebo sa okno zavrelo
                        //File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                        fileToSend[0] = fileChooser.getSelectedFile();
                    }
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0] != null) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsoluteFile());
                        Socket socket = new Socket("localhost", 5000);

                        dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        String fileName = fileToSend[0].getName();

                        byte[] fileNameBytes = fileName.getBytes();

                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        dataOutputStream.writeInt(fileNameBytes.length); //posles na server dlzku dat ktore bude dostavat
                        dataOutputStream.write(fileNameBytes);

                        dataOutputStream.writeInt(fileContentBytes.length); //posles aktualne data
                        dataOutputStream.write(fileContentBytes);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }


            }
        });
    }
}
