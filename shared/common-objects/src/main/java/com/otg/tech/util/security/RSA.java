package com.otg.tech.util.security;

import com.otg.tech.enums.ErrorExceptionCodes;
import com.otg.tech.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class RSA {

    @Value("${rsa.encryption.transformation:RSA/ECB/PKCS1Padding}")
    protected String transformation;
    @Value("${rsa.encryption.algorithmConfigName:RSA}")
    protected String algorithmConfigName;

    public PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance(algorithmConfigName);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error while generating public key ", e);
        }
        return null;
    }

    public PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(algorithmConfigName);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error while generating private key ", e);
        }
        return privateKey;
    }

    @SuppressWarnings("unused")
    public byte[] encrypt(String data, String publicKey) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            log.error("Error while encrypting payload {} ,  ", data, e);
            throw new BusinessException(ErrorExceptionCodes.AES0500.getCode(), ErrorExceptionCodes.AES0500.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public String decrypt(String data, String base64PrivateKey) {
        try {
            return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            log.error("Error while decrypting payload {} ,  ", data, e);
            throw new BusinessException(ErrorExceptionCodes.AES0500.getCode(), ErrorExceptionCodes.AES0500.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
