package vutbr.feec.eccProjekt.core;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TestGUI {
    private JPanel MainPanel;
    private JButton registerButton;
    private JButton verifyCertificateButton;
    private JLabel resultOfVerifyLabel;
    private JButton sendFileButton;
    private JButton recieveButton;
    private JLabel sendStatusLabel;
    private JLabel receiveWasSuccessful;
    private JTextField usernameField;
    private JPasswordField userPasswordField;
    private JButton logInButton;
    private JLabel userLogedInLabel;
    private JLabel loginResultLabel;
    private JButton sendEncryptedFileButton;
    private JButton receiveEncryptedFileButton;
    private JLabel encSendLabel;
    private JLabel encRecLabel;
    private JTextField IPtextField;
    private JButton confirmAddressButton;
    private JLabel TargetIPLabel;
    private JButton sendSignedFileButton;
    private JButton receiveSignedFileButton;
    private JLabel signedSendLabel;
    private JLabel signedReceiveLabel;
    private JFrame frame;
    RegisterForm registerForm;
    KeyManagement keyManagement;
    //private TestProcess process;
    private String loggedUserName;
    private char[] loggedUserPassword;
    private PrivateKey loggedUserPrivateKey;
    private byte[] lastIV;
    SecretKey secretKey;
    private String lastFileName;
    private String targetIPaddress ="localhost";
    EcFunctions ecFunctions;
    TestReceiver testReceiver;
    public TestGUI(){
        this.initialize();


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerForm =new RegisterForm();
            }
        });
        verifyCertificateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory((new File(".")));
                fileChooser.setDialogTitle("Choose a Certificate to verify");

                int respone = fileChooser.showSaveDialog(null);
                if(respone == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    X509Certificate certificate= keyManagement.loadCert(file.getAbsolutePath());
                    boolean isItLegit=keyManagement.verifyCert(certificate);
                    System.out.println(isItLegit);
                    if (isItLegit)
                        resultOfVerifyLabel.setText("Verify result: Legit Certificate");
                    else
                        resultOfVerifyLabel.setText("Verify result: Verification Failed");
                }
            }
        });
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToSend= getFileFromPC("Choose file to send");
                TestSender testSender= new TestSender(fileToSend.getAbsolutePath());
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return testSender.sendFile(fileToSend, targetIPaddress,null,loggedUserName);
                    }

                    // GUI can be updated from this method.
                    protected void done() {
                        boolean status;
                        try {
                            status=get();
                            if(status) {
                                sendStatusLabel.setText("Sending was successful");
                            }
                            else
                                sendStatusLabel.setText("Sending failed");
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                };
                worker.execute();
            }
        });
        recieveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TestReceiver testReceiver= new TestReceiver();
                receiveWasSuccessful.setText("Receiving in progress");
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        System.out.println("Receiving");
                        return testReceiver.Receive(false);
                    }

                    // GUI can be updated from this method.
                    protected void done() {
                        boolean status;
                        try {
                            status=get();
                            if(status) {
                                receiveWasSuccessful.setText("Receive was successful");
                                testReceiver.saveFile();
                            }
                            else
                                receiveWasSuccessful.setText("Receive failed");
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                };
                worker.execute();
            }
        });
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logInUser();
            }
        });
        sendEncryptedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                byte[] encrypted= createEncryptedFileBytes();

                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return TestSender.sendEncFile(lastFileName,encrypted,lastIV,loggedUserName, targetIPaddress,ecFunctions.getD());
                    }

                    // GUI can be updated from this method.
                    protected void done() {
                        boolean status;
                        try {
                            status=get();
                            if(status) {
                                encSendLabel.setText("File Sent Successfully");
                            }
                            else
                                encSendLabel.setText("Something went wrong");
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                };
                worker.execute();
            }
        });
        receiveEncryptedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encRecLabel.setText("Receiving encrypted...");
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        System.out.println("Receiving Encrypted");
                        return receiveEncrypted();
                    }

                    // GUI can be updated from this method.
                    protected void done() {
                        boolean status;
                        try {
                            status=get();
                            if(status) {
                                encRecLabel.setText("Receive was successful");

                            }
                            else
                                encRecLabel.setText("Receive failed");
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                };
                worker.execute();
            }
        });
        confirmAddressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetIPaddress =IPtextField.getText();
                TargetIPLabel.setText("Target IP: "+ targetIPaddress);
                IPtextField.setText("");
            }
        });
        sendSignedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToSend= getFileFromPC("Choose file to send");
                TestSender testSender= new TestSender(fileToSend.getAbsolutePath());
                byte []signature=EcFunctions.SignFile(fileToSend,loggedUserPrivateKey);
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return testSender.sendFile(fileToSend, targetIPaddress,signature,loggedUserName);
                    }

                    // GUI can be updated from this method.
                    protected void done() {
                        boolean status;
                        try {
                            status=get();
                            if(status) {
                                signedSendLabel.setText("Sending was successful");
                            }
                            else
                                signedSendLabel.setText("Sending failed");
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            signedSendLabel.setText("Sending failed");
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            signedSendLabel.setText("Sending failed");
                        }
                    }

                };
                worker.execute();
            }
        });
        receiveSignedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signedReceiveLabel.setText("Receiving signed file...");
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        System.out.println("Receiving Signed");
                        return receiveSigned();
                    }

                    // GUI can be updated from this method.
                    protected void done() {
                        boolean status;
                        try {
                            status=get();
                            if(status) {
                                signedReceiveLabel.setText("Receive was successful");
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame, String.format("Signature of the file from %s is legit, the file was saved to /files/%s ", testReceiver.getUsername(),testReceiver.lastFilename));
                            }
                            else
                                signedReceiveLabel.setText("Receive failed");
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                };
                worker.execute();
            }
        });
    }

    private void initialize(){
        this.frame= new JFrame();
        frame.setBounds(300,400,650,450);
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //process= new TestProcess();
        frame.setVisible(true);
        keyManagement= new KeyManagement();
        ecFunctions= new EcFunctions();
    }

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        new TestGUI();
    }
    public File getFileFromPC(String textToSay){
        File file;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory((new File(".")));
        fileChooser.setDialogTitle(textToSay);
        int response = fileChooser.showSaveDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            return file;
        }
        return null;
    }
    public void logInUser()
    {
        String usernameTemp=usernameField.getText();
        char[] passTemp= userPasswordField.getPassword();
        String path= String.join("","certs/",usernameTemp);
        KeyStore.PrivateKeyEntry pke=KeyManagement.getKeyStoreEntry(path,usernameTemp,passTemp);
        if(pke==null){
            loginResultLabel.setText("Login Failed");
            return;
        }
        else {
            loggedUserName=usernameTemp;
            loggedUserPassword=passTemp;
            loggedUserPrivateKey= pke.getPrivateKey();
            loginResultLabel.setText("Login Successful");
            userLogedInLabel.setText("Logged In as: "+loggedUserName);
            userPasswordField.setText("");
            usernameField.setText("");
        }
    }
    public byte[] createEncryptedFileBytes(){
        File fileToEnc= getFileFromPC("Choose file to send");
        File certFile= getFileFromPC("Choose certificate of the user you are sending data to");
        lastFileName=fileToEnc.getName();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileToEnc.getAbsolutePath());
            byte[] fileContentBytes = new byte[(int) fileToEnc.length()];
            fileInputStream.read(fileContentBytes);


            X509Certificate cert= keyManagement.loadCert(certFile.getAbsolutePath());
            PublicKey publicKey= cert.getPublicKey();
            //secretKey= EcFunctions.generateSharedKey(loggedUserPrivateKey,publicKey);
            lastIV=new SecureRandom().generateSeed(8);
            byte[] encryptedBytes= ecFunctions.encryptByteArray(loggedUserPrivateKey,publicKey,fileContentBytes,lastIV);
            return encryptedBytes;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
    public boolean receiveEncrypted(){
        testReceiver= new TestReceiver();
        ArrayList<byte[]> received=testReceiver.receiveEncrypted();
        if (received==null)
            return false;
        String entityName=new String(received.get(0));
        String filename= new String(received.get(1));
        X509Certificate cert= keyManagement.loadCert(String.join("","certs/",entityName,"cert.ser"));
        //secretKey=EcFunctions.generateSharedKey(loggedUserPrivateKey,cert.getPublicKey());
        byte[] decryptedBytes= ecFunctions.decryptByteArray(loggedUserPrivateKey,cert.getPublicKey(),received.get(2),received.get(3),received.get(4));
        String destination= String.join("","files/",filename);
        TestReceiver.saveDecryptedFile(destination,decryptedBytes);
        return true;
    }
    public boolean receiveSigned(){
        testReceiver = new TestReceiver();
        boolean received =testReceiver.Receive(true);
        if(!received)
            return false;
        String entityName= testReceiver.getUsername();
        X509Certificate cert= keyManagement.loadCert(String.join("","certs/",entityName,"cert.ser"));
        byte []fileBytes=testReceiver.getFileBytes();

        try {
            boolean legitSignature = EcFunctions.verifySignedByteArray(fileBytes,testReceiver.getSignature(),cert.getPublicKey());
            if(legitSignature){
                testReceiver.saveFile();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

}
