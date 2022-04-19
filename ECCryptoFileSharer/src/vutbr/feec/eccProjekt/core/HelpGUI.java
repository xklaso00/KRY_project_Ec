package vutbr.feec.eccProjekt.core;

import javax.swing.*;

public class HelpGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JTextPane textPane1;
    private JTextPane textPane2;
    private JTextPane textPane3;
    private JTextPane textPane4;

    public HelpGUI(){
        this.initialize();
        textPane1.setText("You need to have opened 2 applications. \n" +
                "In one you tap the Receive button and in the other the send file button and choose a file from your PC.\n" +
                "This can be used for sending your certificate to the other side.\n" +
                "If you don't specify the IP localhost will be used. \n");
        textPane1.setEditable(false);
        textPane2.setText("If you want to register new user you will need master password so your certificate can be signed by CA.\n" +
                "MasterPassword is B3tt3rP4ssW0rd\n" +
                "Your private key and certificate will be saved to certs folder");
        textPane2.setEditable(false);
        textPane3.setText("For sending and receiving encrypted file you must be logged in.\n" +
                "First make sure you have the certificate of the other site in certs folder of both applications. (You can use the send file option to send cert to the other side).\n" +
                "On the receiving side tap the Receive encrypted button.\n" +
                "On the sending side tap the Send encrypted button, then choose a file to send, and then choose the certificate of the receiver.\n" +
                "You should receive the file on the other side and it should be decrypted.");
        textPane3.setEditable(false);
        textPane4.setText("You must be logged in to send signed files.\n" +
                "Then tap the Receive Signed File button on the receiving side.\n" +
                "Then tap the Send Signed File on the sender Side.\n" +
                "You should get the file on the receiving side with conformation of the signature.");
        textPane4.setEditable(false);
    }
    private void initialize(){
        this.frame= new JFrame("Help");
        frame.setBounds(200,200,800,600);
        frame.setContentPane(mainPanel);

        frame.setVisible(true);
    }
}
