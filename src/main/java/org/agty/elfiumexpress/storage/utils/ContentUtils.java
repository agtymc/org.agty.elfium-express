package org.agty.elfiumexpress.storage.utils;

import org.apache.commons.text.StringEscapeUtils;

public class ContentUtils {
    public static String nl2br(String text) {
        if (text == null) {
            return "";
        }
        return StringEscapeUtils.escapeHtml4(text).replace("\n", "<br />");
    }

    public static String nl2p(String text) {
        if (text == null) {
            return "";
        }
        return StringEscapeUtils.escapeHtml4(text).replace("\n\n", "<p />");
    }

    public static String escapeHtml(String text) {
        return text == null ? "" : StringEscapeUtils.escapeHtml4(text);
    }
}
