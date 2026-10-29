/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LrcTimeline {
    private static final Pattern TIME = Pattern.compile("\\[(\\d{1,3}):(\\d{2})(?:[.:](\\d{1,3}))?\\]");
    public final List<Line> lines;

    private LrcTimeline(List<Line> lines) {
        this.lines = List.copyOf(lines);
    }

    public static LrcTimeline parse(String lrc) {
        long offset = 0L;
        Matcher off = Pattern.compile("\\[offset:([+-]?\\d+)\\]", 2).matcher(lrc);
        if (off.find()) {
            try {
                offset = Long.parseLong(off.group(1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        ArrayList<Line> out = new ArrayList<Line>();
        for (String raw : lrc.split("\\R")) {
            Matcher m = TIME.matcher(raw);
            ArrayList<Long> times = new ArrayList<Long>();
            int end = 0;
            while (m.find()) {
                long mm = Long.parseLong(m.group(1));
                long ss = Long.parseLong(m.group(2));
                if (ss >= 60L) continue;
                String fraction = m.group(3);
                long millis = fraction == null ? 0L : Long.parseLong((fraction + "000").substring(0, 3));
                times.add(Math.max(0L, mm * 60000L + ss * 1000L + millis - offset));
                end = m.end();
            }
            if (times.isEmpty()) continue;
            String text = raw.substring(end).replaceAll("<\\d{1,3}:\\d{2}(?:\\.\\d{1,3})?>", "").trim();
            if (text.length() > 500) {
                text = text.substring(0, 500);
            }
            Iterator iterator = times.iterator();
            while (iterator.hasNext()) {
                long t = (Long)iterator.next();
                out.add(new Line(t, text));
            }
        }
        out.sort(Comparator.comparingLong(Line::start));
        return new LrcTimeline(out);
    }

    public int index(long position) {
        int lo = 0;
        int hi = this.lines.size() - 1;
        int result = -1;
        while (lo <= hi) {
            int m = lo + hi >>> 1;
            if (this.lines.get((int)m).start <= position) {
                result = m;
                lo = m + 1;
                continue;
            }
            hi = m - 1;
        }
        return result;
    }

    public double progress(int index, long position, long duration) {
        if (index < 0 || index >= this.lines.size()) {
            return 0.0;
        }
        long start = this.lines.get((int)index).start;
        long end = index + 1 < this.lines.size() ? this.lines.get((int)(index + 1)).start : (duration > start ? duration : start + 5000L);
        return Math.clamp((double)(position - start) / (double)Math.max(250L, end - start), 0.0, 1.0);
    }

    public record Line(long start, String text) {
    }
}

