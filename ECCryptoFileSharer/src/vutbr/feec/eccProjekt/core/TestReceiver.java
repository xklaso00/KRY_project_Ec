package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class TestReceiver {
    ServerSocket serverSocket;
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    private byte[] signature=null;
    private String username;
    private byte [] fileBytes=null;
    protected String lastFilename;
    int fileId = 0;
    public TestReceiver(){

    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getUsername() {
        return username;
    }

    public static void saveDecryptedFile(String destination, byte[] fileBytes){
        File directory = new File("files/");
        if (! directory.exists()){
            directory.mkdir();

        }
        try (FileOutputStream stream = new FileOutputStream(destination)) {
            stream.write(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(){
        String extension= myFiles.get(fileId-1).getFileExtension();
        System.out.println(extension);
        String fileDest;
        File directory = new File("files/");
        if (! directory.exists()){
            directory.mkdir();

        }
        if(extension.equals("ser")){
            fileDest= String.join("","certs/",myFiles.get(fileId-1).getName());
        }
        else
            fileDest= String.join("","files/",myFiles.get(fileId-1).getName());

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
    public boolean Receive(boolean Signed){
        while (true) {
            try {
                serverSocket = new ServerSocket(5000);
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength]; //aky velky subor to bude
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);
                    lastFilename=fileName;
                    int fileContentLength = dataInputStream.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        fileId++;
                        fileBytes=fileContentBytes;
                        System.out.println("dostal jsem file bro");

                        if(Signed){
                            int SignLength= dataInputStream.readInt();
                            byte[] SignatureBytes= new byte[SignLength];
                            dataInputStream.readFully(SignatureBytes, 0, SignatureBytes.length);
                            System.out.println("Signature obtained ");
                            this.signature=SignatureBytes;

                            int usernameLength= dataInputStream.readInt();
                            byte[] usernameBytes= new byte[usernameLength];
                            dataInputStream.readFully(usernameBytes, 0, usernameBytes.length);
                            System.out.println("username obtained ");
                            this.username=new String(usernameBytes);


                        }
                        serverSocket.close();
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
    public ArrayList<byte[]> getReceivedEncryptedData(){
        while (true) {
            try {
                serverSocket = new ServerSocket(5000);
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                int usernameLength = dataInputStream.readInt();

                if (usernameLength > 0) {
                    byte[] usernameBytes = new byte[usernameLength];
                    dataInputStream.readFully(usernameBytes, 0, usernameBytes.length);
                    //String fileName = new String(fileNameBytes);
                    System.out.println("username: "+new String(usernameBytes));

                    int fileNameLength= dataInputStream.readInt();
                    byte[] fileNameBytes= new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    System.out.println("filename: "+new String(fileNameBytes));

                    int encMsgLength= dataInputStream.readInt();
                    byte[] encMsgBytes = new byte[encMsgLength];
                    dataInputStream.readFully(encMsgBytes, 0, encMsgBytes.length);
                    System.out.println("length of bytes is "+encMsgLength);

                    int ivLength= dataInputStream.readInt();
                    byte[] ivBytes= new byte[ivLength];
                    dataInputStream.readFully(ivBytes, 0, ivBytes.length);
                    System.out.println("length of iv is "+ivLength);

                    int dLength= dataInputStream.readInt();
                    byte[] dBytes= new byte[dLength];
                    dataInputStream.readFully(dBytes, 0, dBytes.length);
                    System.out.println("length of d is "+dLength);

                    ArrayList<byte[]> returnList= new ArrayList<>();
                    returnList.add(usernameBytes);
                    returnList.add(fileNameBytes);
                    returnList.add(encMsgBytes);
                    returnList.add(ivBytes);
                    returnList.add(dBytes);
                    serverSocket.close();
                    return  returnList;


                }
            } catch (IOException er) {

                er.printStackTrace();
                return null;
            }
        }



    }
    public boolean receiveEncrypted(PrivateKey loggedUserPrivateKey){

        ArrayList<byte[]> received=getReceivedEncryptedData();
        if (received==null)
            return false;
        String entityName=new String(received.get(0));
        String filename= new String(received.get(1));
        lastFilename=filename;
        username=entityName;
        X509Certificate cert= KeyManagement.loadCert(String.join("","certs/",entityName,"cert.ser"));
        //secretKey=EcFunctions.generateSharedKey(loggedUserPrivateKey,cert.getPublicKey());
        byte[] decryptedBytes= EcFunctions.decryptByteArray(loggedUserPrivateKey,cert.getPublicKey(),received.get(2),received.get(3),received.get(4));
        String destination= String.join("","files/",filename);
        TestReceiver.saveDecryptedFile(destination,decryptedBytes);
        return true;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }
}
