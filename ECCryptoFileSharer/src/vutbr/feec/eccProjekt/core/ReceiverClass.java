package vutbr.feec.eccProjekt.core;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class ReceiverClass {
    ServerSocket serverSocket;
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    private byte[] signature=null;
    private String username;
    private byte [] fileBytes=null;
    protected String lastFilename;
    protected String lastLocation;
    int fileId = 0;
    private int lastReturnCode=0;
    public ReceiverClass(){

    }

    public int getLastReturnCode() {
        return lastReturnCode;
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
    //saves decrypted bytes to file, save file could be used but receiveEncrypted function was designed differently so, sorry :/
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
    //saves the last file in myFiles, if the extension is ser it is a certificate if not save it to files
    public void saveFile(){
        String extension= myFiles.get(fileId-1).getFileExtension();
        System.out.println(extension);
        String fileDest;
        File directory = new File("files/");//if files folder is not there create it
        if (! directory.exists()){
            directory.mkdir();

        }
        if(extension.equals("ser")){
            fileDest= String.join("","certs/",myFiles.get(fileId-1).getName());
            lastLocation="certs";
        }
        else {
            fileDest = String.join("", "files/", myFiles.get(fileId - 1).getName());
            lastLocation="files";
        }
        //System.out.println(Utils.bytesToHex(myFiles.get(0).getData()));
        try (FileOutputStream stream = new FileOutputStream(fileDest)) {
            stream.write(myFiles.get(fileId-1).getData());
            System.out.println("file saved to "+lastLocation+"/"+myFiles.get(fileId-1).getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

                            int CertLength= dataInputStream.readInt();
                            byte[] CertBytes= new byte[CertLength];
                            dataInputStream.readFully(CertBytes, 0, CertBytes.length);
                            System.out.println("Cert obtained");
                            String CertName=String.join("",username,"cert.ser");
                            myFiles.add(new MyFile(fileId, CertName, CertBytes, getFileExtension(CertName)));
                            fileId++;
                            saveFile();
                            fileId--;
                            myFiles.remove(fileId);
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
                    username= new String(usernameBytes);

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

                    int CertLength= dataInputStream.readInt();
                    byte[] CertBytes= new byte[CertLength];
                    dataInputStream.readFully(CertBytes, 0, CertBytes.length);
                    System.out.println("Cert obtained");
                    String CertName=String.join("",username,"cert.ser");
                    myFiles.add(new MyFile(fileId, CertName, CertBytes, getFileExtension(CertName)));
                    fileId++;
                    saveFile();
                    fileId--;
                    myFiles.remove(fileId);

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
        if(!KeyManagement.verifyCert(cert)){
            lastReturnCode=2; //code 2 means cert is not legit
            return false;
        }
        //secretKey=EcFunctions.generateSharedKey(loggedUserPrivateKey,cert.getPublicKey());
        byte[] decryptedBytes= EcFunctions.decryptByteArray(loggedUserPrivateKey,cert.getPublicKey(),received.get(2),received.get(3),received.get(4));
        String destination= String.join("","files/",filename);
        ReceiverClass.saveDecryptedFile(destination,decryptedBytes);
        return true;
    }
    public boolean receiveSigned(){
        boolean received = Receive(true);
        if(!received) {
            lastReturnCode=1;//code one means something went wrong during receiving
            return false;
        }
        X509Certificate cert=KeyManagement.loadCert(String.join("","certs/",username,"cert.ser"));
        if(!KeyManagement.verifyCert(cert)){
            lastReturnCode=2; //code 2 means cert is not legit
            return false;
        }
        try {
            boolean legitSignature = EcFunctions.verifySignedByteArray(fileBytes, signature,cert.getPublicKey());
            if(legitSignature){
                saveFile();
                lastReturnCode=0;
                return true;
            }
            else {
                lastReturnCode=3;//code 3 means nt legit signature
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            lastReturnCode=4;//code4 means error in verify
            return false;
        }
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }
}