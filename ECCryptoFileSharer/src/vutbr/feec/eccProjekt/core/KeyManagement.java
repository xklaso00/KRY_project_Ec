package vutbr.feec.eccProjekt.core;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//for now just testing here key storing etc.
public class KeyManagement {

    public  KeyManagement(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    //function for generating the CA, this needs to be used just once I guess? if you see CAKS file in cert folder it has been created
    public void generateAndSaveCAKeys(){
        try {
            KeyPair keyPair= EcFunctions.generateKeyPair();
            //ECPrivateKey privateKey= (ECPrivateKey) keyPair.getPrivate();
            X509Certificate c = generateCertificate(keyPair.getPublic(),keyPair.getPrivate(),"CN=mainappcert");
            //System.out.println("Private Key before entry "+privateKey.getS());
            //System.out.println("Public Key before: "+keyPair.getPublic());
            printCert(c);
            KeyStore keyStore = KeyStore.getInstance("BKS");
            char[] password = "B3tt3rP4ssW0rd".toCharArray();
            //String path = "/certs";
            //java.io.FileInputStream fis = new FileInputStream(path);
            keyStore.load(null, null);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);

            X509Certificate[] certChain = new X509Certificate[1];
            certChain[0] = c;

            KeyStore.PrivateKeyEntry privateKeyEntry= new KeyStore.PrivateKeyEntry(keyPair.getPrivate(),certChain);
            keyStore.setEntry("caEntry",privateKeyEntry,protectionParam);

            java.io.FileOutputStream fos = null;
            fos = new java.io.FileOutputStream("certs/CAKS");
            keyStore.store(fos, password);
            System.out.println("data stored");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //function to create X509 cert. You need public key of owner of the cert, PrivateKey of CA and subject name
    private X509Certificate generateCertificate(PublicKey publicKey, PrivateKey signingKey, String subject) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException, SignatureException {

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();

        X509V3CertificateGenerator  cert = new X509V3CertificateGenerator();
        cert.setSerialNumber(BigInteger.valueOf(1));   //or generate a random number
        cert.setSubjectDN(new X509Principal(subject));  //see examples to add O,OU etc
        cert.setIssuerDN(new X509Principal("CN=mainappcert")); //same since it is self-signed
        cert.setPublicKey(publicKey);
        cert.setNotBefore(today);
        cert.setNotAfter(nextYear);
        cert.setSignatureAlgorithm("SHA256withECDSA");
        //PrivateKey signingKey = keyPair.getPrivate();
        return cert.generate(signingKey, "BC");
    }

   /* protected X509Certificate getCACert() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("BKS");
        char[] password = "B3tt3rP4ssW0rd".toCharArray();
        String path = "certs/CAKS";
        java.io.FileInputStream fis = new FileInputStream(path);
        keyStore.load(fis, password);
        KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
        KeyStore.PrivateKeyEntry privateKeyEntry= (KeyStore.PrivateKeyEntry) keyStore.getEntry("caEntry",protectionParam);
        return (X509Certificate) privateKeyEntry.getCertificate();
    }*/
    //function that gets you the privateKeyEntry from a file. you need path to file, alias of the entry and a password
    //it will return null if you have wrong password or something, this can also be uset to loging in as you can check password with this
    public static KeyStore.PrivateKeyEntry getKeyStoreEntry(String path, String alias, char[] password){
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            java.io.FileInputStream fis = new FileInputStream(path);
            keyStore.load(fis, password);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry privateKeyEntry= (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,protectionParam);
            return  privateKeyEntry;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /*protected KeyPair loadCAKeys(){
        try {
            KeyStore keyStore = KeyStore.getInstance("BKS");
            char[] password = "B3tt3rP4ssW0rd".toCharArray();
            String path = "certs/CAKS";
            java.io.FileInputStream fis = new FileInputStream(path);
            keyStore.load(fis, password);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry privateKeyEntry= (KeyStore.PrivateKeyEntry) keyStore.getEntry("caEntry",protectionParam);
            ECPrivateKey pk = (ECPrivateKey) privateKeyEntry.getPrivateKey();
            PublicKey pubK=privateKeyEntry.getCertificate().getPublicKey();

            //System.out.println("PrivateKey "+ pk.getS());
            //System.out.println("Public key "+ pubK);
            //KeyStore.Entry ke= keyStore.getEntry("caEntry",protectionParam);
            KeyPair kp = new KeyPair(pubK,pk);
            return kp;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }*/

    //a function for saving client key to file, you need password that will be used to "lock" the file and a name
    public int saveClientKey(KeyPair keyPair,char[] password, String entityName, char[] masterPassword){
        StringBuilder sb= new StringBuilder();
        sb.append("CN=");
        sb.append(entityName);
        String CNEntName=sb.toString();
        try {
            KeyStore.PrivateKeyEntry CAKeyEntry= getKeyStoreEntry("certs/CAKS","caEntry",masterPassword);
            X509Certificate clientCert=generateCertificate(keyPair.getPublic(),CAKeyEntry.getPrivateKey(),CNEntName);
            printCert(clientCert);

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(null, null);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password);

            X509Certificate[] certChain = new X509Certificate[2];
            certChain[0] = clientCert;
            certChain[1]= (X509Certificate) CAKeyEntry.getCertificate();

            KeyStore.PrivateKeyEntry privateKeyEntry= new KeyStore.PrivateKeyEntry(keyPair.getPrivate(),certChain);
            keyStore.setEntry(entityName,privateKeyEntry,protectionParam);

            java.io.FileOutputStream fos = null;
            fos = new java.io.FileOutputStream(String.join("","certs/",entityName));
            keyStore.store(fos, password);

            saveCert(clientCert,entityName);


            System.out.println("data stored");
            return 0;



        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }
    //function for saving cert to certs folder
    public void saveCert(X509Certificate certificate, String name){
        try {

            String keyFile = String.join("","certs/",name,"cert.ser");
            FileOutputStream fileOut = new FileOutputStream(keyFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(certificate);
            out.close();
            fileOut.close();
            System.out.println("cert saved to file:  "+keyFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //a function for loading cert files
    public static X509Certificate loadCert(String keyFile){
        //String keyFile = String.join("","certs/",name,"cert.ser");

        try
        {
            FileInputStream fileIn = new FileInputStream(keyFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            X509Certificate certificate= (X509Certificate) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Cert has been loaded from  "+keyFile);
            return  certificate;

        }
        catch (IOException | ClassNotFoundException e){
            System.out.println(e.toString());
            return null;
        }
    }




    //verify the cert with ca key
    public static boolean verifyCert(X509Certificate c){
        //KeyStore.PrivateKeyEntry CAKeyEntry=getKeyStoreEntry("certs/CAKS","caEntry","B3tt3rP4ssW0rd".toCharArray());


        X509Certificate CACert= loadCert("certs/CAKScert.ser");
        try {
            c.verify(CACert.getPublicKey());
            //saveCert((X509Certificate) CAKeyEntry.getCertificate(),"CAKS");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //just a function to print some basic things about a cert for testing
    public void printCert(X509Certificate c){
       /* try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //X509Certificate c = (X509Certificate) cf.generateCertificate(fr);

        System.out.println("\tCertificate for: " + c.getSubjectDN());
        System.out.println("\tCertificate issued by: " + c.getIssuerDN());
        System.out.println("\tThe certificate is valid from " + c.getNotBefore() + " to "
                + c.getNotAfter());
        System.out.println("\tCertificate SN# " + c.getSerialNumber());
        System.out.println("\tGenerated with " + c.getSigAlgName());
        System.out.println("\tPublic Key: "+c.getPublicKey());
    }


}
