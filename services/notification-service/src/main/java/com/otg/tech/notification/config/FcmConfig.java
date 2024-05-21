package com.otg.tech.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Slf4j
public class FcmConfig {

    @PostConstruct
    protected void setUpFcm() {
        log.info("Initialing FCM config..............");
        try (InputStream keyFileIn = this.getClass().getClassLoader()
                .getResourceAsStream("service-account/firebaseServiceAccountKey.json")) {
            if (keyFileIn == null) {
                log.error("Private key file is not found in location.");
                return;
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(keyFileIn))
                    .build();
            boolean hasBeenInitialized = false;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    hasBeenInitialized = true;
                }
            }
            if (!hasBeenInitialized) {
                FirebaseApp.initializeApp(options);
            }
            log.info("FCM admin has been initialized successfully.");
        } catch (IOException e) {
            log.error("Exception in initializing FCM admin!", e);
        }
    }
}
