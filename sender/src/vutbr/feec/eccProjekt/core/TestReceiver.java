package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

public class TestReceiver {
    ServerSocket serverSocket;
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    int fileId = 0;
    public TestReceiver(){
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveFile(){
        String fileDest= String.join("","files/",myFiles.get(0).getName());

        //System.out.println(Utils.bytesToHex(myFiles.get(0).getData()));
        try (FileOutputStream stream = new FileOutputStream(fileDest)) {
            stream.write(myFiles.get(0).getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {

            FileOutputStream fileOutputStream= new FileOutputStream(fileDest);
            fileOutputStream.write(myFiles.get(0).getData());
            fileOutputStream.close();

            //Files.createFile()

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    public boolean Receive(){
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength]; //aky velky subor to bude
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        fileId++;
                        System.out.println("dostal jsem file bro");
                        return true;

                    }

                }
            } catch (IOException er) {
                er.printStackTrace();
                return false;
            }
        }
    }
    public static  String getFileExtension(String fileName) { //je mozne posielat iba simple files, ako .txt, .pdf
        int i = fileName.lastIndexOf('.');
        if(i> 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }
}
