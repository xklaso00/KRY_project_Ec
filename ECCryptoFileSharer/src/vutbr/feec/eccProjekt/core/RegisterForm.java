package vutbr.feec.eccProjekt.core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RegisterForm {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JPasswordField masterPasswordField;
    private JLabel resultOfReg;
    private JFrame frame;
    KeyManagement keyManagement;

    public RegisterForm(){
        this.initialize();

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameString= usernameField.getText();
                System.out.println(usernameString);
                char[] password= passwordField.getPassword();
                char [] masterPassword = masterPasswordField.getPassword();
                KeyPair keyPair = EcFunctions.generateKeyPair();
                int result=keyManagement.saveClientKey(keyPair,password,usernameString,masterPassword);
                if (result==0)
                    resultOfReg.setText("Successful");
                else
                    resultOfReg.setText("Failed");
            }
        });
    }





    private void initialize(){
        this.frame= new JFrame();
        frame.setBounds(500,200,500,400);
        frame.setContentPane(mainPanel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //process= new TestProcess();
        keyManagement= new KeyManagement();
        frame.setVisible(true);
    }
}




