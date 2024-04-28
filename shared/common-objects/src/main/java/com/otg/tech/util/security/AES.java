package com.otg.tech.util.security;

import com.otg.tech.enums.ErrorExceptionCodes;
import com.otg.tech.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import static com.otg.tech.constant.CommonConstant.EMPTY;
import static com.otg.tech.constant.CommonConstant.HYPHEN;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class AES {
    @Value("${aes.encryption.encoded.iv:AAAAAAAAAAAAAAAAAAAAAA==}")
    protected String encodedIV;
    @Value("${aes.encryption.keyLength:256}")
    protected int keyLength;
    @Value("${aes.encryption.iterationCount:100}")
    protected int iterationCount;
    @Value("${aes.encryption.transformation:AES/CBC/PKCS5Padding}")
    protected String transformation;
    @Value("${aes.encryption.secretKeyFactory:PBKDF2WithHmacSHA1}")
    protected String secretKeyFactory;
    @Value("${aes.encryption.algorithmConfigName:AES}")
    protected String algorithmConfigName;

    private IvParameterSpec getIvs() {
        byte[] iv = Base64.getDecoder().decode(encodedIV);
        return new IvParameterSpec(iv);
    }

    private SecretKeySpec generateKey(String secret, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyFactory);
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), hex(salt.replace(HYPHEN, EMPTY)),
                    iterationCount, keyLength);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), algorithmConfigName);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Exception in generating security spec : {}", e.getMessage());
            throw new SecurityException(e);
        }
    }

    public String encrypt(String secret, String strToEncrypt, String salt) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(secret, salt), getIvs());
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException
                 | BadPaddingException e) {
            log.error("Encrypt error {} ", e.getMessage());
            throw new BusinessException(ErrorExceptionCodes.AES0500.getCode(), ErrorExceptionCodes.AES0500.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public byte[] decrypt(String secret, String strToDecrypt, String salt) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(secret, salt), getIvs());
            return cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException
                 | BadPaddingException e) {
            throw new BusinessException(ErrorExceptionCodes.AES0500.getCode(), ErrorExceptionCodes.AES0500.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public byte[] decryptWithoutSalt(String encrypted, String key) {
        try {
            IvParameterSpec iv = getIvs();

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithmConfigName);

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            return cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encrypted));
        } catch (Exception e) {
            throw new BusinessException(ErrorExceptionCodes.AES0500.getCode(), ErrorExceptionCodes.AES0500.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public String encryptWithoutSalt(String secret, String strToEncrypt) {
        try {
            IvParameterSpec iv = getIvs();
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), algorithmConfigName);

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException
                 | BadPaddingException e) {
            log.error("Encrypt without salt error {} ", e.getMessage());
            throw new BusinessException(ErrorExceptionCodes.AES0500.getCode(), ErrorExceptionCodes.AES0500.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    public byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }
}
