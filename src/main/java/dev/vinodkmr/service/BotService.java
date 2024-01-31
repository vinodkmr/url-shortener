package dev.vinodkmr.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import dev.vinodkmr.config.AppProperties;
import dev.vinodkmr.model.BotResponse;
import dev.vinodkmr.util.BotRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class BotService {

    public static final String ABOUT = "This Bot is specifically designed to shorten the urls\nDeveloper - Vinod\nhttps://vinodkmr.dev/";
    public static final String SHA_256 = "SHA-256";
    public static final int MAX_LENGTH = 4;
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String COLLECTION_NAME = "short_url";
    private static AppProperties appProperties;

    public static AppProperties getAppProperties() {
        return appProperties;
    }
    @Autowired
    public void setAppProperties(AppProperties appProperties) {
        BotService.appProperties = appProperties;
    }


    public static BotResponse getResByBotRequest(BotRequest botRequest) {
        return switch (botRequest) {
            case SHRINK -> buildShrinkResponse();
            case ABOUT -> buildAboutResponse();
        };
    }

    private static BotResponse buildAboutResponse() {
        return new BotResponse(ABOUT);
    }

    private static BotResponse buildShrinkResponse() {
        return new BotResponse("Enter the url to shorten");
    }

    public static BotResponse shrinkUrl(String url) {
        try {
            log.info("Url to shorten : {}",url);
            String shortenedUrl = shortenUrl(url);
            if(!isShortenedUrlPresent(shortenedUrl)){
                saveToDB(shortenedUrl,url);
            }
            return new BotResponse(appProperties.getDomain()+shortenedUrl);
        } catch (Exception e) {
            log.error("Error hashing the url",e);
            return new BotResponse(INTERNAL_SERVER_ERROR);
        }
    }

    private static boolean isShortenedUrlPresent(String shortenedUrl) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference document = db.collection(COLLECTION_NAME).document(shortenedUrl);
        boolean exists = document.get().get().exists();
        log.info("Document exist for {}?:{}",shortenedUrl,exists);
        return exists;
    }

    private static void saveToDB(String shortenedUrl, String url) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(shortenedUrl);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        ApiFuture<WriteResult> resultApiFuture = docRef.set(data);
        log.info("Saved successfully to DB :{}",resultApiFuture.get().getUpdateTime());
    }

    private static String shortenUrl(String url) throws NoSuchAlgorithmException {
        String hash = hash(url);
        String encode = encode(hash);
        return trim(encode);
    }

    private static String hash(String url) throws NoSuchAlgorithmException {
        //Create MessageDigest object for SHA_256
        MessageDigest digest = MessageDigest.getInstance(SHA_256);
        //Update input string in message digest
        digest.update(url.getBytes(), 0, url.length());
        //Converts message digest value in base 16 (hex)
        return new BigInteger(1, digest.digest()).toString(16);
    }

    private static String encode(String hash) {
        return Base64.getEncoder().encodeToString(hash.getBytes(StandardCharsets.UTF_8));
    }

    private static String trim(String encode) {
        return encode.substring(0, MAX_LENGTH);
    }

    public String getLongUrlByShortUrl(String shortUrl) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference document = db.collection(COLLECTION_NAME).document(shortUrl);
            DocumentSnapshot documentSnapshot = document.get().get();
            String url = documentSnapshot.getString("url");
            log.info("Short Url : {}, Long Url : {} ", shortUrl, url);
            return url;
        } catch (Exception e) {
            log.error("Error getting the long url by short ",e);
            return "";
        }
    }
}
