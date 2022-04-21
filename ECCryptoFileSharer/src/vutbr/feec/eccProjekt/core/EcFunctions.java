package vutbr.feec.eccProjekt.core;

import org.bouncycastle.jce.spec.IEKeySpec;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;

public class EcFunctions {
    private byte[] d;
    private byte[]e;

    public byte[] getD() {
        return d;
    }

    public void setD(byte[] d) {
        this.d = d;
    }

    public byte[] getE() {
        return e;
    }

    public void setE(byte[] e) {
        this.e = e;
    }

    //function for generating keys over secp256r1
    public static KeyPair generateKeyPair(){

        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator g = null;
        try {
            g = KeyPairGenerator.getInstance("EC","BC");
            g.initialize(ecSpec, new SecureRandom());
            KeyPair keypair = g.generateKeyPair();
            return keypair;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //function for signing byte[] with private key, returns the signature, byte[] because most stuff can be converted to byte[]
    public static byte[] signByteArray(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature ecdsaSignature = Signature.getInstance("SHA256withECDSA");
        ecdsaSignature.initSign(privateKey);
        ecdsaSignature.update(data);
        byte[] signature = ecdsaSignature.sign();
        return signature;
    }
    //function to verify the signature of byte array
    public static boolean verifySignedByteArray(byte[]data,byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);
        return ecdsaVerify.verify(signature);

    }
    //function to generate key for AES not used as it is not needen ECIES does this itself
     public static SecretKey generateSharedKey(PrivateKey APrivateKey, PublicKey BPublicKey) {
         KeyAgreement keyAgreement = null;
         try {
             keyAgreement = KeyAgreement.getInstance("ECDH","BC");
             keyAgreement.init(APrivateKey);
             keyAgreement.doPhase(BPublicKey, true);

             SecretKey key = keyAgreement.generateSecret("AES");

             return key;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }

     }
    //function to encrypt byte array with shared key
     public byte[] encryptByteArray(PrivateKey privateKey, PublicKey publicKey, byte[] data)  {
         try {
             // get ECIES cipher object
             Cipher cipher = Cipher.getInstance("ECIES","BC");
             //  generate derivation and encoding vectors
             d = new SecureRandom().generateSeed(8);
             e = new SecureRandom().generateSeed(8);
             IESParameterSpec param = new IESParameterSpec(d, e, 256);
             cipher.init(Cipher.ENCRYPT_MODE, new IEKeySpec(privateKey,publicKey), param);
             return cipher.doFinal(data);
         }
         catch (Exception exception){
             exception.printStackTrace();
             return null;
         }
     }
     //function to decrypt byte arr with created KDFed shared key
     public static byte[] decryptByteArray(PrivateKey privateKey,PublicKey publicKey, byte[] dataToDecrypt,byte[] e, byte []d) {
        try {

            Cipher cipher = Cipher.getInstance("ECIES","BC");
            IESParameterSpec param = new IESParameterSpec(d, e, 256);
            cipher.init(Cipher.DECRYPT_MODE, new IEKeySpec(privateKey,publicKey), param);

            return cipher.doFinal(dataToDecrypt);
        }
        catch (Exception exception){
            exception.printStackTrace();
            return null;
        }

     }


    //a function for signing a file with private key
    public static byte[] SignFile(File file,PrivateKey privateKey){
        try {
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            byte[] fileContentBytes = new byte[(int) file.length()];
            fileInputStream.read(fileContentBytes);
            return signByteArray(fileContentBytes,privateKey);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
