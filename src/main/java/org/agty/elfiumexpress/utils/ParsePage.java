package org.agty.elfiumexpress.utils;

import org.agty.connector.Connector;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsePage {
    private final String pageContent;
    private Map<String, String> parsedData = new HashMap<>();

    public ParsePage(String uri) {
        this.pageContent = ParsePage.getPage(uri);
        parseData();
    }

    static String getPage(String uri) {
        if (!uri.matches("^http(s)?://.*$")) return null;
        Connector connector = new Connector(uri);
        connector.createConnection();
        if (connector.isError()) System.out.println(connector.getError());
        return connector.getContent();
    }

    private void parseData() {
        if (pageContent == null) return;

        //TODO: определять кодировку и переводить в нее

        Pattern pattern = Pattern.compile("<title[^>]*?>(.*?)<\\/title>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(this.pageContent);
        if (matcher.find()) {
            this.parsedData.put("title", StringEscapeUtils.unescapeHtml4(matcher.group(1)));
        }

        /*Pattern patternBody = Pattern.compile("<title[^>]*?>(.*?)<\\/title>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcherBody = patternBody.matcher(this.pageContent);
        if (matcherBody.find()) {
            this.parsedData.put("body", matcherBody.group(1));
        }*/
    }

    public String getTitle() {
        return this.parsedData.get("title");
    }
}
