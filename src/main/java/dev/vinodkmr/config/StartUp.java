package dev.vinodkmr.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartUp {
    private final AppProperties appProperties;

    @PostConstruct
    public void registerBot(){
        try {
            registerTelegramBot();
            initializeFirebase();
        } catch (Exception e) {
            log.error("Error initializing the bot",e);
        }
    }

    private void initializeFirebase() throws IOException {
        File file = new ClassPathResource("firebase.json").getFile();
        FileInputStream serviceAccount = new FileInputStream(file);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        log.info("Initialized FirebaseApp Successfully");

    }

    private void registerTelegramBot() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new BotIncoming(appProperties.getBotToken(), appProperties.getBotName()));
        log.info("Registered Bot Successfully");
    }
}
