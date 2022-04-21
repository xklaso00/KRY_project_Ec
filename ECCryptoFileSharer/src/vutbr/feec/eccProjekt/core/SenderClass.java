package vutbr.feec.eccProjekt.core;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;

public class SenderClass {
    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public SenderClass(String path){
        this.path=path;
    }
    public boolean sendFile(File fileToSend,String address, byte[] Signature, String username){
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Socket socket = new Socket(address, 5000);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String fileName = fileToSend.getName();
            byte[] fileNameBytes = fileName.getBytes();

            byte[] fileContentBytes = new byte[(int) fileToSend.length()];
            fileInputStream.read(fileContentBytes);
            fileInputStream.close();
            //System.out.println(Utils.bytesToHex(fileContentBytes));
            dataOutputStream.writeInt(fileNameBytes.length); //posles na server dlzku dat ktore bude dostavat
            dataOutputStream.write(fileNameBytes);

            dataOutputStream.writeInt(fileContentBytes.length); //posles aktualne data
            dataOutputStream.write(fileContentBytes);
            if(Signature!=null){
                dataOutputStream.writeInt(Signature.length);
                dataOutputStream.write(Signature);

                dataOutputStream.writeInt(username.getBytes().length);
                dataOutputStream.write(username.getBytes());

                String certName= String.join("",username,"cert.ser");

                //fileInputStream= new FileInputStream(String.join("certs/",certName));


                Path pathToCert = Paths.get(String.join("","certs/",certName));
                byte[] certContentBytes= Files.readAllBytes(pathToCert);


                dataOutputStream.writeInt(certContentBytes.length);
                dataOutputStream.write(certContentBytes);



            }
            dataOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public static boolean sendEncFile(String fileName, byte[] encryptedFileBytes, byte[] e, String username, String address, byte []d){

        try {
            Socket socket = new Socket(address, 5000);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            byte[] fileNameBytes = fileName.getBytes();
            byte[] usernameBytes= username.getBytes();

            dataOutputStream.writeInt(usernameBytes.length);
            dataOutputStream.write(usernameBytes);

            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);

            dataOutputStream.writeInt(encryptedFileBytes.length);
            dataOutputStream.write(encryptedFileBytes);

            dataOutputStream.writeInt(e.length);
            dataOutputStream.write(e);

            dataOutputStream.writeInt(d.length);
            dataOutputStream.write(d);

            String certName= String.join("",username,"cert.ser");
            Path pathToCert = Paths.get(String.join("","certs/",certName));
            byte[] certContentBytes= Files.readAllBytes(pathToCert);
            dataOutputStream.writeInt(certContentBytes.length);
            dataOutputStream.write(certContentBytes);

            dataOutputStream.close();


            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
    /*public static boolean sendSignedFile( ){
        try {

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }*/
}
