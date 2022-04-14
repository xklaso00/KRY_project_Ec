package vutbr.feec.eccProjekt.core;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestSender {
    private String path;
    public TestSender(String path){
        this.path=path;
    }
    public boolean sendFile(File fileToSend,String address){
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Socket socket = new Socket(address, 5000);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String fileName = fileToSend.getName();
            byte[] fileNameBytes = fileName.getBytes();

            byte[] fileContentBytes = new byte[(int) fileToSend.length()];
            fileInputStream.read(fileContentBytes);
            //System.out.println(Utils.bytesToHex(fileContentBytes));
            dataOutputStream.writeInt(fileNameBytes.length); //posles na server dlzku dat ktore bude dostavat
            dataOutputStream.write(fileNameBytes);

            dataOutputStream.writeInt(fileContentBytes.length); //posles aktualne data
            dataOutputStream.write(fileContentBytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public static boolean sendEncFile(String fileName, byte[] encryptedFileBytes, byte[] iv, String username, String address, byte []d){

        try {
            Socket socket = new Socket(address, 5000);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            byte[] fileNameBytes = fileName.getBytes();
            byte[] usernameBytes= username.getBytes();

            dataOutputStream.writeInt(usernameBytes.length);
            dataOutputStream.write(usernameBytes);

            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);

            dataOutputStream.writeInt(encryptedFileBytes.length); //posles aktualne data
            dataOutputStream.write(encryptedFileBytes);

            dataOutputStream.writeInt(iv.length); //posles aktualne data
            dataOutputStream.write(iv);

            dataOutputStream.writeInt(d.length); //posles aktualne data
            dataOutputStream.write(d);
            dataOutputStream.close();


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
