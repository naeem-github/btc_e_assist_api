package com.assist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proxy {

    public static String loadingWithProxy(String url) {
        try {
            String zeroUrl = getZeroString(url);
            String firstUrl = getFirstUrl(zeroUrl);
            String secondUrl = getSecondUrl(firstUrl);
            return getContent(secondUrl);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getZeroString(String originalUrl) {
        return "https://translate.google.ru/translate?hl=ru&sl=en&tl=ru&u=" + originalUrl + "&anno=2";
    }

    private static String getFirstUrl(String zeroUrl) {
        StringBuilder stringBuilder = loadContent(zeroUrl);
        if (stringBuilder == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("src=\"https://translate.*\" name").matcher(stringBuilder);
        if (!matcher.find()) {
            return null;
        }
        StringBuilder link = new StringBuilder(matcher.group());
        link.delete(0, 5).delete(link.length() - 6, link.length());
        return link.toString();
    }

    private static String getSecondUrl(String firstUrl) {
        StringBuilder stringBuilder = loadContent(firstUrl);
        if (stringBuilder == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("https://translate.*\"></head><body ").matcher(stringBuilder);
        if (!matcher.find()) {
            return null;
        }
        StringBuilder link = new StringBuilder(matcher.group());
        link.delete(link.length() - 15, link.length());
        return link.toString().replace("&amp;", "&");
    }

    private static String getContent(String secondUrl) {
        StringBuilder stringBuilder = loadContent(secondUrl);
        if (stringBuilder == null) {
            return null;
        }
        Matcher matcher = Pattern.compile("</iframe>\\{&quot;.*<script>_addload").matcher(stringBuilder);
        if (matcher.find()) {
            String content = matcher.group();
            return content.replace("</iframe>","").replace("<script>_addload", "").replace("&quot;", "\"");
        }
        matcher = Pattern.compile("> \\{&quot;.*</span><script>_addload").matcher(stringBuilder);
        if (matcher.find()) {
            String content = matcher.group();
            return content.replace("> {&quot;", "{&quot;").
                    replace("</span><script>_addload", "").replace("&quot;", "\"");
        } else {
            return null;
        }
    }

    private static StringBuilder loadContent(String link) {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Test");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder;
        } catch (IOException e) {
            return null;
        }
    }
}
