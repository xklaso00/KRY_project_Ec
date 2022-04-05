package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.Security;
import java.security.cert.X509Certificate;
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
    private JFrame frame;
    RegisterForm registerForm;
    KeyManagement keyManagement;
    //private TestProcess process;

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
                        return testSender.sendFile(fileToSend);
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

                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        System.out.println("Receiving");
                        return testReceiver.Receive();
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
    }

    private void initialize(){
        this.frame= new JFrame();
        frame.setBounds(300,400,650,450);
        frame.setContentPane(MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //process= new TestProcess();
        frame.setVisible(true);
        keyManagement= new KeyManagement();
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
        int respone = fileChooser.showSaveDialog(null);
        if(respone == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            return file;
        }
        return null;
    }
}
