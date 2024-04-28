package com.otg.tech.util.security;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Slf4j
public class CryptoHelper {
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "/CBC/PKCS5Padding";
    private static final Integer KEY_SIZE = 256;
    private static final Integer IV_SIZE = 16;
    private static final String SPLIT_CHARACTER = "|";
    private final Key key;


    public CryptoHelper(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    @SuppressWarnings("unused")
    public static String generateSymmetricKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(KEY_SIZE);

        SecretKey key = generator.generateKey();

        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String encrypt(String plainText) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return encrypt(generateIV(), plainText);
    }

    private String encrypt(byte[] iv, String plainText) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] decrypted = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encrypt(iv, decrypted);

        String encodedIV = Base64.getEncoder().encodeToString(iv);
        String encodedEncrypted = Base64.getEncoder().encodeToString(encrypted);

        return encodedIV + SPLIT_CHARACTER + encodedEncrypted;
    }

    private byte[] encrypt(byte[] iv, byte[] plainText) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm() + CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(plainText);
    }

    public String decrypt(String cipherText) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String[] parts = cipherText.split("\\" + SPLIT_CHARACTER);

        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encrypted = Base64.getDecoder().decode(parts[1]);
        byte[] decrypted = decrypt(iv, encrypted);

        return new String(decrypted);
    }

    private byte[] decrypt(byte[] iv, byte[] cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm() + CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(cipherText);
    }

    private byte[] generateIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv);

        return iv;
    }
}
