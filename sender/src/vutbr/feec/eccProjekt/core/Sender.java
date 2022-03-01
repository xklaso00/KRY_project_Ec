package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Sender {

    public static void main(String[] args) {
        final File[] fileToSend = new File[1];

        JFrame frame = new JFrame("Sender");
        frame.setSize(450, 450);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jTitle = new JLabel("File Sender");
        jTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jTitle.setBorder(new EmptyBorder(20,0,10,0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jFileName = new JLabel("Choose a file to send");
        jFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jFileName.setBorder(new EmptyBorder(20,0,10,0));
        jFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jButton = new JPanel();
        jButton.setBorder(new EmptyBorder(75,0,10,0));

        JButton jSendFile = new JButton("Send File");
        jSendFile.setPreferredSize(new Dimension(150,75));
        jSendFile.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jChooseFile = new JButton("Choose File");
        jChooseFile.setPreferredSize(new Dimension(150,75));
        jChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        jButton.add(jChooseFile);
        jButton.add(jSendFile);

        jChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory((new File(""))); //kde sa otvori chooser
                fileChooser.setDialogTitle("Choose a file to send");

                int respone = fileChooser.showSaveDialog(null);
                if(respone == JFileChooser.APPROVE_OPTION) { //ci sa vlozil subor alebo sa okno zavrelo
                    fileToSend[0] = fileChooser.getSelectedFile();
                    jFileName.setText("Chosen file: " + fileToSend[0].getName());
                }
            }
        });
        jSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0] == null) {
                    jFileName.setText("Choose a file first");
            } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsoluteFile());
                        Socket socket = new Socket("localhost", 5000);

                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

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
        frame.add(jTitle);
        frame.add(jFileName);
        frame.add(jButton);
        frame.setVisible(true);
    }
}
