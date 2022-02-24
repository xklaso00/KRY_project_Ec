package vutbr.feec.eccProjekt.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TestMain {
    public static void main(String[] args) {

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
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
