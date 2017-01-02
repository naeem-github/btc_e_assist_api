package com.assist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class help you find BTC-E mirror
 */
public class Mirrors {
    private static final String userAgent = "Assist TradeApi";
    public static final String[] mirrors = {"https://btc-e.nz", "https://btc-e.com"};
    private static int currentMirrorIndex = -1;


    /**
     * @return working mirror or null
     */
    public static String getMirror() {
        if (currentMirrorIndex >= 0) {
            return mirrors[currentMirrorIndex];
        } else {
            return selectMirror();
        }
    }

    private static String selectMirror() {
        Pattern pattern = Pattern.compile("\"server_time\":");
        for (int i = 0; i < mirrors.length; i++) {
            try {
                String mirror = mirrors[i];
                mirror += "/api/3/info";
                String content = loadContent(mirror);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    currentMirrorIndex = i;
                    return mirrors[currentMirrorIndex];
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static String loadContent(String link) {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }

}
