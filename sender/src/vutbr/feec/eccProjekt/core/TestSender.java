package vutbr.feec.eccProjekt.core;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestSender {
    private String path;
    public TestSender(String path){
        this.path=path;
    }
    public boolean sendFile(File fileToSend){
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Socket socket = new Socket("localhost", 5000);

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
}
