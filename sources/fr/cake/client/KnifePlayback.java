/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public final class KnifePlayback {
    private int mode;
    private int next = 3;
    private int queued = -1;
    private long started;
    private long serial;
    private double skipped;

    public void play(int action, long now) {
        this.mode = action;
        this.started = now;
        this.queued = -1;
        this.skipped = 0.0;
        ++this.serial;
    }

    public long serial() {
        return this.serial;
    }

    private double elapsed(long now) {
        return Math.max(0.0, (double)(now - this.started) / 1.0E9) + this.skipped;
    }

    public int mode() {
        return this.mode;
    }

    public double progress(long now, double[] durations) {
        return this.mode == 0 ? 0.0 : Math.clamp(this.elapsed(now) / Math.max(0.01, durations[this.mode]), 0.0, 1.0);
    }

    public boolean skipInspectionHold(long now, double begin, double end) {
        if (begin < 0.0 || end <= begin || this.elapsed(now) < begin || this.elapsed(now) >= end) {
            return false;
        }
        this.skipped += end - begin;
        ++this.serial;
        return true;
    }

    public boolean loopIfFinished(int action, long now, double[] durations) {
        if (this.mode == 0 || this.progress(now, durations) < 1.0) {
            return false;
        }
        double over = Math.min(0.1, Math.max(0.0, this.elapsed(now) - durations[this.mode]));
        this.play(action, now - (long)(over * 1.0E9));
        return true;
    }

    public void attack(boolean heavy, long now, double[] durations) {
        int action;
        int n = action = heavy ? 5 : this.next;
        if (!heavy) {
            int n2 = this.next = this.next == 3 ? 4 : 3;
        }
        if (this.mode >= 3 && this.mode <= 5 && this.progress(now, durations) < 0.85) {
            this.queued = action;
            return;
        }
        this.play(action, now);
    }

    public void advance(long now, double[] durations) {
        if (this.mode != 0 && this.progress(now, durations) >= 1.0) {
            if (this.queued >= 0) {
                int action = this.queued;
                this.play(action, now);
            } else {
                this.mode = 0;
                this.started = now;
                this.skipped = 0.0;
                ++this.serial;
            }
        }
    }

    public double seconds(long now, double[] durations, double speed) {
        return this.mode == 0 ? this.elapsed(now) % durations[0] * speed : Math.min(durations[this.mode], this.elapsed(now)) * speed;
    }
}

