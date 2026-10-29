/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import fr.cake.client.CakeClient;
import fr.cake.client.Settings;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class I18n {
    private static final Map<String, String[]> entries = new LinkedHashMap<String, String[]>();

    public static String tr(String text) {
        if (text == null) {
            return "";
        }
        Settings s = CakeClient.settings;
        int i = s == null || s.language.equals("en") ? 0 : (s.language.equals("fr") ? 1 : 2);
        String[] a = entries.get(text);
        if (a != null) {
            return a[i];
        }
        for (Map.Entry<String, String[]> e : entries.entrySet()) {
            String key = e.getKey();
            if (key.length() <= 4 || !key.endsWith(": ") && !key.endsWith(" \u00b7 ") || !text.startsWith(key)) continue;
            return e.getValue()[i] + I18n.tr(text.substring(key.length()));
        }
        if (text.endsWith(" \u00b7 modifier")) {
            return I18n.tr(text.substring(0, text.length() - 11)) + (new String[]{" \u00b7 edit", " \u00b7 modifier", " \u00b7 \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u044c"})[i];
        }
        return text;
    }

    static {
        try (InputStream in = I18n.class.getResourceAsStream("/assets/cakeclient/lang/ui.tsv");){
            if (in != null) {
                for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                    String[] a = line.split("\\t", -1);
                    if (a.length != 3) continue;
                    entries.put(a[0], a);
                    entries.put(a[1], a);
                    entries.put(a[2], a);
                }
            }
        }
        catch (Exception e) {
            CakeClient.LOG.warn("Translations unavailable", (Throwable)e);
        }
    }
}

