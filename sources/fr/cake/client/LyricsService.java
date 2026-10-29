/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package fr.cake.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cake.client.CakeClient;
import fr.cake.client.LrcTimeline;
import fr.cake.client.PlaybackClock;
import fr.cake.client.Settings;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;

public final class LyricsService {
    private static final PlaybackClock CLOCK = new PlaybackClock();
    private static final Gson GSON = new Gson();
    private static volatile Media media;
    private static volatile BufferedImage artwork;
    private static volatile LrcTimeline timeline;
    private static volatile String status;
    private static volatile Process process;
    private static volatile boolean running;
    private static volatile long generation;
    private static Future<?> pendingLookup;
    private static long lookupGeneration;
    private static long nextStart;
    private static final ExecutorService FETCH;
    private static final Map<String, LrcTimeline> CACHE;

    public static BufferedImage artwork() {
        return artwork;
    }

    public static String status() {
        return status;
    }

    public static Media media() {
        return media;
    }

    public static LrcTimeline timeline() {
        return timeline;
    }

    public static void settingsChanged() {
        if (!CakeClient.settings.lyrics && !CakeClient.settings.visualizer) {
            LyricsService.stop();
            status = "Module d\u00e9sactiv\u00e9.";
        } else {
            nextStart = 0L;
            status = "Recherche de Spotify\u2026";
        }
    }

    public static void retry() {
        LyricsService.stop();
        nextStart = 0L;
        CACHE.clear();
        LyricsService.settingsChanged();
    }

    public static void tick() {
        if (!CakeClient.settings.lyrics && !CakeClient.settings.visualizer) {
            if (running) {
                LyricsService.stop();
            }
            return;
        }
        if (running || System.nanoTime() < nextStart) {
            return;
        }
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            status = "La d\u00e9tection Spotify automatique n\u00e9cessite Windows 10 ou 11.";
            nextStart = Long.MAX_VALUE;
            return;
        }
        nextStart = System.nanoTime() + 15000000000L;
        running = true;
        long token = ++generation;
        Thread thread = new Thread(() -> LyricsService.bridge(token), "Cake-Spotify-Media");
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void bridge(long token) {
        try (InputStream stream = LyricsService.class.getResourceAsStream("/assets/cakeclient/bridge/spotify.ps1");){
            if (stream == null) {
                throw new IOException("Missing media bridge");
            }
            String source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            String encoded = Base64.getEncoder().encodeToString(source.getBytes(StandardCharsets.UTF_16LE));
            Process child = new ProcessBuilder("powershell.exe", "-NoLogo", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden", "-EncodedCommand", encoded).redirectError(ProcessBuilder.Redirect.DISCARD).start();
            if (token != generation) {
                child.destroy();
                return;
            }
            process = child;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream(), StandardCharsets.UTF_8));){
                String line;
                while (token == generation && (line = reader.readLine()) != null) {
                    if (line.length() >= 1500000) continue;
                    LyricsService.accept(line, token);
                }
            }
            finally {
                child.destroy();
            }
        }
        catch (Exception e) {
            if (token == generation) {
                status = "Session m\u00e9dia Windows indisponible. V\u00e9rifiez Spotify et les contr\u00f4les multim\u00e9dias Windows.";
                CakeClient.LOG.warn("Spotify media bridge unavailable: {}", (Object)e.getClass().getSimpleName());
            }
        }
        finally {
            LyricsService.bridgeEnded(token);
        }
    }

    private static synchronized void bridgeEnded(long token) {
        if (token != generation) {
            return;
        }
        media = null;
        artwork = null;
        timeline = null;
        CLOCK.reset();
        running = false;
        process = null;
        LyricsService.cancelLookup();
        status = "Session Spotify interrompue. Nouvelle tentative automatique\u2026";
        nextStart = System.nanoTime() + 15000000000L;
    }

    private static synchronized void accept(String raw, long token) {
        if (token != generation) {
            return;
        }
        try {
            Media next;
            JsonObject j = JsonParser.parseString((String)raw).getAsJsonObject();
            if (!j.has("ready") || !j.get("ready").getAsBoolean()) {
                media = null;
                artwork = null;
                timeline = null;
                CLOCK.reset();
                LyricsService.cancelLookup();
                status = j.has("error") ? "Windows ne fournit pas la session Spotify. Relancez la d\u00e9tection." : "En attente d'une chanson dans Spotify\u2026";
                return;
            }
            String title = LyricsService.str(j, "title");
            String artist = LyricsService.str(j, "artist");
            if (title.isBlank() || artist.isBlank()) {
                media = null;
                timeline = null;
                status = "M\u00e9tadonn\u00e9es du morceau indisponibles.";
                return;
            }
            String album = LyricsService.str(j, "album");
            String key = (artist + "\n" + title + "\n" + album).toLowerCase(Locale.ROOT);
            Media old = media;
            boolean changed = old == null || !old.key.equals(key);
            long now = System.nanoTime();
            long duration = Math.max(0L, j.get("duration").getAsLong());
            boolean playing = j.get("playing").getAsBoolean();
            long rawPosition = Math.max(0L, j.get("position").getAsLong());
            long stamp = j.has("updatedAt") ? j.get("updatedAt").getAsLong() : 0L;
            long age = j.has("anchorAge") ? j.get("anchorAge").getAsLong() : 0L;
            double rate = j.has("rate") ? j.get("rate").getAsDouble() : 1.0;
            long position = CLOCK.update(rawPosition, stamp, age, now, playing, rate, duration, changed);
            if (changed) {
                artwork = null;
            }
            if (j.has("artwork") && !j.get("artwork").getAsString().isBlank()) {
                try {
                    BufferedImage img;
                    byte[] bytes = Base64.getDecoder().decode(j.get("artwork").getAsString());
                    if (bytes.length < 1000000 && (img = ImageIO.read(new ByteArrayInputStream(bytes))) != null && img.getWidth() <= 2048 && img.getHeight() <= 2048) {
                        artwork = img;
                    }
                }
                catch (Exception bytes) {
                    // empty catch block
                }
            }
            media = next = new Media(key, title, artist, album, position, duration, playing, now);
            if (!changed && old.duration <= 0L && duration > 0L) {
                CACHE.remove(key);
            }
            if (changed || old.duration <= 0L && duration > 0L) {
                LyricsService.cancelLookup();
                long request = lookupGeneration;
                timeline = null;
                status = "Recherche de paroles synchronis\u00e9es\u2026";
                if (CakeClient.settings.lyrics) {
                    pendingLookup = FETCH.submit(() -> LyricsService.lookup(next, token, request));
                }
            } else if (timeline != null) {
                status = next.playing ? "Spotify d\u00e9tect\u00e9 \u00b7 paroles synchronis\u00e9es." : "Spotify en pause.";
            }
        }
        catch (RuntimeException e) {
            status = "Donn\u00e9es de lecture Spotify indisponibles.";
        }
    }

    private static String str(JsonObject j, String key) {
        String s = j.has(key) && !j.get(key).isJsonNull() ? j.get(key).getAsString() : "";
        return s.length() > 250 ? s.substring(0, 250) : s;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void lookup(Media track, long token, long request) {
        try {
            LrcTimeline result = CACHE.get(track.key);
            if (result == null) {
                String safe = (track.artist + " - " + track.title).replaceAll("[\\\\/:*?\"<>|]", "_");
                Path local = Settings.DIR.resolve("lyrics").resolve(safe + ".lrc");
                result = Files.isRegularFile(local, new LinkOption[0]) && Files.size(local) < 1000000L ? LrcTimeline.parse(Files.readString(local)) : LyricsService.fetch(track);
                if (result != null) {
                    if (CACHE.size() > 24) {
                        CACHE.clear();
                    }
                    CACHE.put(track.key, result);
                }
            }
            Class<LyricsService> clazz = LyricsService.class;
            synchronized (LyricsService.class) {
                if (token != generation || request != lookupGeneration || media == null || !LyricsService.media.key.equals(track.key)) {
                    // ** MonitorExit[var6_5] (shouldn't be in output)
                    return;
                }
                timeline = result;
                status = result != null && !result.lines.isEmpty() ? "Spotify d\u00e9tect\u00e9 \u00b7 paroles synchronis\u00e9es." : "Pas de paroles synchronis\u00e9es disponibles pour ce morceau.";
                // ** MonitorExit[var6_5] (shouldn't be in output)
                return;
            }
        }
        catch (Exception e) {
            Class<LyricsService> clazz = LyricsService.class;
            synchronized (LyricsService.class) {
                if (token != generation || request != lookupGeneration || media == null || !LyricsService.media.key.equals(track.key)) return;
                status = "Recherche de paroles indisponible. V\u00e9rifiez Internet puis relancez la d\u00e9tection.";
                CakeClient.LOG.debug("Lyrics lookup failed: {}", (Object)e.getClass().getSimpleName());
                // ** MonitorExit[var6_6] (shouldn't be in output)
                return;
            }
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static LrcTimeline fetch(Media track) throws Exception {
        String query;
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).followRedirects(HttpClient.Redirect.NORMAL).build();
        JsonElement json = LyricsService.get(client, "https://lrclib.net/api/get?" + (query = "track_name=" + LyricsService.enc(track.title) + "&artist_name=" + LyricsService.enc(track.artist) + (String)(track.album.isBlank() ? "" : "&album_name=" + LyricsService.enc(track.album)) + (String)(track.duration > 0L ? "&duration=" + Math.round((double)track.duration / 1000.0) : "")));
        if (json != null && json.isJsonObject() && LyricsService.matches(json.getAsJsonObject(), track)) {
            return LrcTimeline.parse(LyricsService.strLyrics(json.getAsJsonObject()));
        }
        json = LyricsService.get(client, "https://lrclib.net/api/search?track_name=" + LyricsService.enc(track.title) + "&artist_name=" + LyricsService.enc(track.artist));
        JsonObject best = null;
        double score = -1.7976931348623157E308;
        if (json != null && json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                JsonObject j = e.getAsJsonObject();
                if (!LyricsService.matches(j, track)) continue;
                double d = !track.album.isBlank() && LyricsService.str(j, "albumName").equalsIgnoreCase(track.album) ? 100 : 0;
                double d2 = track.duration > 0L && j.has("duration") ? Math.abs(j.get("duration").getAsDouble() - (double)track.duration / 1000.0) : 0.0;
                double rank = d - d2;
                if (!(rank > score)) continue;
                best = j;
                score = rank;
            }
        }
        return best == null ? null : LrcTimeline.parse(LyricsService.strLyrics(best));
    }

    private static String strLyrics(JsonObject j) {
        return j.has("syncedLyrics") && !j.get("syncedLyrics").isJsonNull() ? j.get("syncedLyrics").getAsString() : "";
    }

    private static boolean matches(JsonObject j, Media track) {
        return LyricsService.str(j, "trackName").equalsIgnoreCase(track.title) && LyricsService.str(j, "artistName").equalsIgnoreCase(track.artist) && !LyricsService.strLyrics(j).isBlank() && (track.duration <= 0L || j.has("duration") && Math.abs(j.get("duration").getAsDouble() - (double)track.duration / 1000.0) <= 2.0);
    }

    public static long calibration() {
        Media m = media;
        return m == null ? 0L : CakeClient.settings.lyricCalibrations.getOrDefault(m.key, 0L);
    }

    public static void calibrateLine(int step) {
        Media m = media;
        LrcTimeline t = timeline;
        if (m == null || t == null || t.lines.isEmpty()) {
            return;
        }
        long pos = m.currentPosition();
        long offset = (long)((CakeClient.settings.lyricOffset - 0.5) * 30000.0);
        int line = Math.clamp((long)(t.index(pos + offset + LyricsService.calibration()) + step), 0, t.lines.size() - 1);
        CakeClient.settings.lyricCalibrations.put(m.key, Math.clamp(t.lines.get(line).start() - pos - offset, -120000L, 120000L));
        CakeClient.settings.save();
    }

    public static void resetCalibration() {
        Media m = media;
        if (m != null) {
            CakeClient.settings.lyricCalibrations.remove(m.key);
            CakeClient.settings.save();
        }
    }

    public static String currentLine() {
        Media m = media;
        LrcTimeline t = timeline;
        if (m == null || t == null) {
            return "En attente des paroles";
        }
        int i = t.index(m.currentPosition() + (long)((CakeClient.settings.lyricOffset - 0.5) * 30000.0) + LyricsService.calibration());
        return i >= 0 ? t.lines.get(i).text() : "Avant la premi\u00e8re ligne";
    }

    public static String playbackLabel() {
        Media m = media;
        if (m == null) {
            return "Aucun morceau";
        }
        long ms = m.currentPosition() / 1000L;
        return String.format("%d:%02d \u00b7 correction %+.1f s", ms / 60L, ms % 60L, (double)LyricsService.calibration() / 1000.0);
    }

    private static JsonElement get(HttpClient client, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(9L)).header("User-Agent", "CakeClient/5.0 (Minecraft visual lyrics display)").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() == 404) {
            return null;
        }
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode());
        }
        if (response.body().length() > 2000000) {
            throw new IOException("Lyrics response too large");
        }
        return JsonParser.parseString((String)response.body());
    }

    private static void cancelLookup() {
        ++lookupGeneration;
        if (pendingLookup != null) {
            pendingLookup.cancel(true);
            pendingLookup = null;
        }
    }

    private static synchronized void stop() {
        ++generation;
        running = false;
        Process p = process;
        process = null;
        if (p != null) {
            p.destroy();
        }
        LyricsService.cancelLookup();
        media = null;
        artwork = null;
        timeline = null;
        CLOCK.reset();
    }

    public static void close() {
        LyricsService.stop();
        FETCH.shutdownNow();
    }

    static {
        status = "Module d\u00e9sactiv\u00e9.";
        FETCH = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Cake-Lyrics-Lookup");
            t.setDaemon(true);
            return t;
        });
        CACHE = Collections.synchronizedMap(new LinkedHashMap());
    }

    public record Media(String key, String title, String artist, String album, long position, long duration, boolean playing, long sampled) {
        public Media(String key, String title, String artist, long position, long duration, boolean playing, long sampled) {
            this(key, title, artist, "", position, duration, playing, sampled);
        }

        public long currentPosition() {
            long value = this.position + (this.playing ? Math.max(0L, (System.nanoTime() - this.sampled) / 1000000L) : 0L);
            return this.duration > 0L ? Math.clamp(value, 0L, this.duration) : Math.max(0L, value);
        }
    }
}

