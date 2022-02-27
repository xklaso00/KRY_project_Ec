package vutbr.feec.eccProjekt.core;


import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Receiver {
    private JPanel mainPanelReceiver;
    private JPanel jPFile;
    private JButton listenButton;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    static ArrayList<MyFile> myFiles = new ArrayList<>();


    public static void main(String[] args) {
        int fileId = 0;

        JFrame frame = new JFrame("Hello2");
        frame.setBounds(100, 100, 450, 300);
        frame.setContentPane(new Receiver().mainPanelReceiver);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        new Receiver();


        //}
    }



    public static  String getFileExtension(String fileName) { //je mozne posielat iba simple files, ako .txt, .pdf
        int i = fileName.lastIndexOf('.');
        if(i> 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }

    public Receiver() {


       /* listenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try(ServerSocket serverSocket = new ServerSocket(5000)){
                    System.out.println("listening to port:5000");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(clientSocket+" connected.");
                    dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    receiveFile("NewFile1.pdf");
                    //receiveFile("NewFile2.pdf");

                    dataInputStream.close();
                    dataOutputStream.close();
                    clientSocket.close();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });*/
    }

    private static void receiveFile(String fileName) throws Exception{
        System.out.println("File received");
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
