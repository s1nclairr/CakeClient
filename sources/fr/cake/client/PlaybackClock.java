/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class PlaybackClock {
    private boolean initialized;
    private boolean playing;
    private long sampled;
    private long lastRaw;
    private long lastStamp;
    private double position;
    private double rate = 1.0;

    public long update(long raw, long stamp, long age, long now, boolean active, double playbackRate, long duration, boolean trackChanged) {
        double nextRate = Double.isFinite(playbackRate) && playbackRate > 0.0 ? playbackRate : 1.0;
        double predicted = this.position + (this.playing ? Math.max(0.0, (double)(now - this.sampled) / 1000000.0) * this.rate : 0.0);
        double candidate = (double)raw + (active && (!this.initialized || trackChanged || stamp != this.lastStamp) ? (double)Math.clamp(age, 0, 3600000) * nextRate : 0.0);
        if (!this.initialized || trackChanged) {
            this.position = candidate;
            this.initialized = true;
        } else {
            double error;
            this.position = raw != this.lastRaw ? (Math.abs(error = candidate - predicted) > 1500.0 ? candidate : predicted + Math.clamp(error * 0.18, -60.0, 60.0)) : predicted;
        }
        this.position = duration > 0L ? Math.clamp(this.position, 0.0, (double)duration) : Math.max(0.0, this.position);
        this.playing = active;
        this.sampled = now;
        this.lastRaw = raw;
        this.lastStamp = stamp;
        this.rate = nextRate;
        return Math.round(this.position);
    }

    public void reset() {
        this.initialized = false;
        this.position = 0.0;
        this.playing = false;
    }
}

