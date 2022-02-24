package vutbr.feec.eccProjekt.core;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

public class TestMain {
    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //testing of ECDSA signature on pdf file, should be able to sign anything you can convert to byte[] (so pretty much everything)
        try {
            //generate keys, we will later find a way to safely store those, and probably create master key of the app to sign the pub keys?
            KeyPair keyPair= EcFunctions.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
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
            PrivateKey privateKey2 = keyPair2.getPrivate();
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
        }

    }
}
