package vutbr.feec.eccProjekt.core;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class EcFunctions {

    //function for generating keys, the keys can later be converted into byte[] for transportation
    public static KeyPair generateKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        KeyPair keypair = g.generateKeyPair();

        return keypair;
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
     public static SecretKey generateSharedKey(PrivateKey APrivateKey, PublicKey BPublicKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
         KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH","BC");
         keyAgreement.init(APrivateKey);
         keyAgreement.doPhase(BPublicKey, true);

         SecretKey key = keyAgreement.generateSecret("AES");
         return key;
     }
    //function to encrypt byte array with shared key
     public static byte[] encryptByteArray(SecretKey key, byte[] data, byte [] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, ShortBufferException, BadPaddingException, IllegalBlockSizeException {
         IvParameterSpec ivSpec = new IvParameterSpec(iv);
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");

         cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
         byte[] encrypted = new byte[cipher.getOutputSize(data.length)];
         int length= cipher.update(data,0,data.length,encrypted);
         System.out.println("the length var is: "+length);
         cipher.doFinal(encrypted,length);
         return encrypted;
     }
     //function to decrypt byte arr with shared key
     public static byte[] decryptByteArray(SecretKey key, byte[] dataToDecrypt,byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, ShortBufferException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
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
}
