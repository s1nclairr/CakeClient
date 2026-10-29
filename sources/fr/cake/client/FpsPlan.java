/*
 * Decompiled with CFR 0.152.
 */
package fr.cake.client;

public record FpsPlan(int view, int simulation, double entities, int particles, int weather, boolean fastLeaves, boolean improvedTransparency, boolean ao) {
    public static FpsPlan preset(int level, int currentView, int currentSimulation, double currentEntities) {
        int sim;
        int v;
        int n = level == 1 ? 8 : (v = level == 2 ? 5 : 3);
        int n2 = level == 1 ? 6 : (sim = level == 2 ? 5 : 5);
        double e = level == 1 ? 0.75 : (level == 2 ? 0.5 : 0.5);
        return new FpsPlan(Math.max(2, Math.min(currentView, v)), Math.max(5, Math.min(currentSimulation, sim)), Math.max(0.5, Math.min(currentEntities, e)), level == 1 ? 1 : 2, level == 1 ? 5 : 3, true, false, false);
    }
}

