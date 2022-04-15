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

    //function for generating keys, the keys can later be converted into byte[] for transportation
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
    //function to verify the signature
    public static boolean verifySignedByteArray(byte[]data,byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);
        return ecdsaVerify.verify(signature);

    }
    //function to generate key for AES with ECDH we need our PrivateKey and PublicKey of the person we want to share it with
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
     public byte[] encryptByteArray(PrivateKey privateKey, PublicKey publicKey, byte[] data, byte [] iv)  {
         //IvParameterSpec ivSpec = new IvParameterSpec(iv);
         try {
        /* Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");

         cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
         byte[] encrypted = new byte[cipher.getOutputSize(data.length)];
         int length= cipher.update(data,0,data.length,encrypted);
         System.out.println("the length var is: "+length);
         cipher.doFinal(encrypted,length);
         return encrypted;*/
             // get ECIES cipher objects
             Cipher acipher = Cipher.getInstance("ECIES","BC");

             //  generate derivation and encoding vectors
             d = new SecureRandom().generateSeed(8);;

             e = iv;
             IESParameterSpec param = new IESParameterSpec(d, e, 256);

             // encrypt the plaintext using the public key
             acipher.init(Cipher.ENCRYPT_MODE, new IEKeySpec(privateKey,publicKey), param);

             return acipher.doFinal(data);


         }
         catch (Exception e){
             e.printStackTrace();
             return null;
         }
     }
     //function to decrypt byte arr with shared key
     public byte[] decryptByteArray(PrivateKey privateKey,PublicKey publicKey, byte[] dataToDecrypt,byte[] iv, byte []d) {
        try {
         /*Key decryptionKey = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
         IvParameterSpec ivSpec = new IvParameterSpec(iv);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");
         cipher.init(Cipher.DECRYPT_MODE, decryptionKey, ivSpec);

         byte[] decryptedData=new byte[cipher.getOutputSize(dataToDecrypt.length)];
         int length=cipher.update(dataToDecrypt, 0, dataToDecrypt.length, decryptedData, 0);
         System.out.println("the length var is: "+length);
         cipher.doFinal(decryptedData, length);
         return decryptedData;*/
            Cipher cipher = Cipher.getInstance("ECIES","BC");

            //  generate derivation and encoding vectors
            this.d = d;
            e = iv;

            IESParameterSpec param = new IESParameterSpec(d, e, 256);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, new IEKeySpec(privateKey,publicKey), param);

            return cipher.doFinal(dataToDecrypt);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

     }


    public static KeyPair generateSecKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator g = KeyPairGenerator.getInstance("EC","BC");
        String  curveName="secp256r1";

        g.initialize(new ECGenParameterSpec(curveName), new SecureRandom());
        KeyPair aKeyPair = g.generateKeyPair();
        //ECPrivateKey SecKeyA= (ECPrivateKey)aKeyPair.getPrivate();

        //ECPublicKey PubKeyA= (ECPublicKey)aKeyPair.getPublic();
        return aKeyPair;


    }
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
