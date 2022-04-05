package vutbr.feec.eccProjekt.core;

import org.bouncycastle.math.ec.ECPoint;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;

public class EcFunctions {

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
     public static byte[] encryptByteArray(SecretKey key, byte[] data, byte [] iv)  {
         IvParameterSpec ivSpec = new IvParameterSpec(iv);
         try {
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");

         cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
         byte[] encrypted = new byte[cipher.getOutputSize(data.length)];
         int length= cipher.update(data,0,data.length,encrypted);
         System.out.println("the length var is: "+length);
         cipher.doFinal(encrypted,length);
         return encrypted;
         }
         catch (Exception e){
             e.printStackTrace();
             return null;
         }
     }
     //function to decrypt byte arr with shared key
     public static byte[] decryptByteArray(SecretKey key, byte[] dataToDecrypt,byte[] iv) {
        try {
         Key decryptionKey = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
         IvParameterSpec ivSpec = new IvParameterSpec(iv);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");
         cipher.init(Cipher.DECRYPT_MODE, decryptionKey, ivSpec);

         byte[] decryptedData=new byte[cipher.getOutputSize(dataToDecrypt.length)];
         int length=cipher.update(dataToDecrypt, 0, dataToDecrypt.length, decryptedData, 0);
         System.out.println("the length var is: "+length);
         cipher.doFinal(decryptedData, length);
         return decryptedData;
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


}
