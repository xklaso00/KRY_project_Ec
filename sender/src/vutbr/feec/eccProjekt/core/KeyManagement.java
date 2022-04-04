package vutbr.feec.eccProjekt.core;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.Calendar;
import java.util.Date;

//for now just testing here key storing etc.
public class KeyManagement {

    public  KeyManagement(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    public void generateAndSaveKeys(){
        try {
            KeyPair keyPair= EcFunctions.generateKeyPair();
            ECPrivateKey privateKey= (ECPrivateKey) keyPair.getPrivate();
            X509Certificate c = generateCertificate(keyPair);
            System.out.println("Private Key before entry "+privateKey.getS());
            System.out.println("Public Key before: "+keyPair.getPublic());


            CertificateFactory cf = CertificateFactory.getInstance("X509");
            //X509Certificate c = (X509Certificate) cf.generateCertificate(fr);
            System.out.println("\tCertificate for: " + c.getSubjectDN());
            System.out.println("\tCertificate issued by: " + c.getIssuerDN());
            System.out.println("\tThe certificate is valid from " + c.getNotBefore() + " to "
                    + c.getNotAfter());
            System.out.println("\tCertificate SN# " + c.getSerialNumber());
            System.out.println("\tGenerated with " + c.getSigAlgName());


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

    public X509Certificate generateCertificate(KeyPair keyPair) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException, SignatureException {

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1);
        Date nextYear = cal.getTime();

        X509V3CertificateGenerator  cert = new X509V3CertificateGenerator();
        cert.setSerialNumber(BigInteger.valueOf(1));   //or generate a random number
        cert.setSubjectDN(new X509Principal("CN=mainappcert"));  //see examples to add O,OU etc
        cert.setIssuerDN(new X509Principal("CN=mainappcert")); //same since it is self-signed
        cert.setPublicKey(keyPair.getPublic());
        cert.setNotBefore(today);
        cert.setNotAfter(nextYear);
        cert.setSignatureAlgorithm("SHA256withECDSA");
        PrivateKey signingKey = keyPair.getPrivate();
        return cert.generate(signingKey, "BC");
    }

    protected KeyPair loadKeys(){
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

            System.out.println("PrivateKey "+ pk.getS());
            System.out.println("Public key "+ pubK);
            //KeyStore.Entry ke= keyStore.getEntry("caEntry",protectionParam);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}
