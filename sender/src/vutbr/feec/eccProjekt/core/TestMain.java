package vutbr.feec.eccProjekt.core;

import java.security.*;
import java.security.cert.X509Certificate;

public class TestMain {
    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //testing of ECDSA signature on pdf file, should be able to sign anything you can convert to byte[] (so pretty much everything)
        /*try {
            //generate keys, we will later find a way to safely store those, and probably create master key of the app to sign the pub keys?
            KeyPair keyPair= EcFunctions.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
            //load file that we want to create signature to
            Path pdfPath = Paths.get("testpdf.pdf");
            byte[] pdf = Files.readAllBytes(pdfPath);
            //sign an verify
            byte[] signature= EcFunctions.signByteArray(pdf,privateKey);
            boolean isItLegit=EcFunctions.verifySignedByteArray(pdf,signature,publicKey);
            System.out.println("Signature is legit? "+isItLegit);



            //test of ecdh and encrypt and decrypt with aes with generated key, local for now for testing (i was encrypting the signature cause I am lazy and it is short)
            KeyPair keyPair2 = EcFunctions.generateKeyPair();
            PublicKey publicKey2 = keyPair2.getPublic();
            ECPrivateKey privateKey2 = (ECPrivateKey) keyPair2.getPrivate();
            SecretKey sc1= EcFunctions.generateSharedKey(privateKey,publicKey2);
            SecretKey sc2= EcFunctions.generateSharedKey(privateKey2,publicKey);
            System.out.println("Bytes: "+Utils.bytesToHex(signature));
            byte[] iv = new SecureRandom().generateSeed(16);
            byte[] encrypted=EcFunctions.encryptByteArray(sc1,signature,iv);
            System.out.println("Encrypted Bytes: "+Utils.bytesToHex(encrypted));
            byte[] decrypted= EcFunctions.decryptByteArray(sc2,encrypted,iv);
            System.out.println("Decrypted Bytes: "+Utils.bytesToHex(decrypted));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }*/

        KeyManagement keyManagement= new KeyManagement();

        //keyManagement.generateAndSaveCAKeys();
        //keyManagement.loadCAKeys();
        try {
            //KeyPair kp= EcFunctions.generateKeyPair();
            //keyManagement.saveClientKey(kp,"hello".toCharArray(),"kita");
            KeyStore.PrivateKeyEntry pke= keyManagement.getKeyStoreEntry("certs/kita","kita","hello".toCharArray());
            keyManagement.printCert((X509Certificate) pke.getCertificate());
            keyManagement.verifyCert((X509Certificate) pke.getCertificate());
            //keyManagement.loadCert("CAKScert.ser");

        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}
