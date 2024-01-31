package dev.vinodkmr.util;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {

    public static final String URL_REGEX = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static boolean isNotNullOrBlank(String str){
        return str != null && !str.trim().isEmpty();
    }

    public static boolean checkForValidURL(String text) {
        // Create a pattern object
        Pattern pattern = Pattern.compile(URL_REGEX);

        // Create a matcher object
        Matcher matcher = pattern.matcher(text);

        // Check if any match is found
        return matcher.find();
    }
}
