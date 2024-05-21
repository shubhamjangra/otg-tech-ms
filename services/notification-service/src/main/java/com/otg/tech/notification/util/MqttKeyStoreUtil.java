package com.otg.tech.notification.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.List;

@Slf4j
public final class MqttKeyStoreUtil {

    private MqttKeyStoreUtil() {
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(
            final String certificateFile, final String privateKeyFile) {
        return getKeyStorePasswordPair(certificateFile, privateKeyFile, null);
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(
            final String certificateFile, final String privateKeyFile,
            String keyAlgorithm) {
        if (certificateFile == null || privateKeyFile == null) {
            log.info("Certificate or private key file missing");
            return null;
        }

        final PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile, keyAlgorithm);

        final List<Certificate> certChain = loadCertificatesFromFile(certificateFile);

        if (certChain == null || privateKey == null) return null;

        return getKeyStorePasswordPair(certChain, privateKey);
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(
            final List<Certificate> certificates, final PrivateKey privateKey) {
        KeyStore keyStore;
        String keyPassword;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            keyPassword = new BigInteger(128, new SecureRandom()).toString(32);

            Certificate[] certChain = new Certificate[certificates.size()];
            certChain = certificates.toArray(certChain);
            keyStore.setKeyEntry("alias", privateKey, keyPassword.toCharArray(), certChain);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            log.error("Failed to create key store {}", e.getMessage());
            return null;
        }
        KeyStorePasswordPair keyStorePasswordPair = new KeyStorePasswordPair();
        keyStorePasswordPair.setKeyPassword(keyPassword);
        keyStorePasswordPair.setKeyStore(keyStore);
        return keyStorePasswordPair;
    }

    @SuppressWarnings("unchecked")
    private static List<Certificate> loadCertificatesFromFile(final String filename) {
        File file;
        try {
            file = ResourceUtils.getFile(filename);
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }
        if (!file.exists()) {
            log.info("Certificate file: {} is not found.", filename);
            return Collections.emptyList();
        }

        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            log.info("certificated loaded successfully");
            return (List<Certificate>) certFactory.generateCertificates(stream);
        } catch (IOException | CertificateException e) {
            log.error("Failed to load certificate file {}", filename);
        }
        return Collections.emptyList();
    }

    private static PrivateKey loadPrivateKeyFromFile(final String filename, final String algorithm) {
        PrivateKey privateKey = null;

        File file;
        try {
            file = ResourceUtils.getFile(filename);
        } catch (FileNotFoundException e) {
            return null;
        }
        if (!file.exists()) {
            log.info("Private key file not found: {}", filename);
            return null;
        }
        try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            privateKey = PrivateKeyReader.getPrivateKey(stream, algorithm);
        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to load private key from file {}", filename);
        }
        return privateKey;
    }

    @Data
    public static class KeyStorePasswordPair {
        private KeyStore keyStore;
        private String keyPassword;
    }
}
