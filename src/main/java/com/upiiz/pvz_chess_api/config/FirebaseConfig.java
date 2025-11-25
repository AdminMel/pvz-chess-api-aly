package com.upiiz.pvz_chess_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.file}")
    private String firebaseCredentialsPath;

    @PostConstruct
    public void init() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        System.out.println("Firebase inicializado correctamente (Render)");
    }
    @Bean
    public FirebaseMessaging firebaseMessaging() {
        // Usa la app por defecto inicializada en init()
        return FirebaseMessaging.getInstance();
    }
}
