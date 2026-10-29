/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package fr.cake.client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class KnifeMesh {
    public final List<Vertex> vertices = new ArrayList<Vertex>();

    public void triangle(Vector3f a, Vector3f b, Vector3f c, int color) {
        Vector3f n = new Vector3f((Vector3fc)b).sub((Vector3fc)a).cross((Vector3fc)new Vector3f((Vector3fc)c).sub((Vector3fc)a)).normalize();
        if (!Float.isFinite(n.x)) {
            return;
        }
        this.vertex(a, n, color);
        this.vertex(b, n, color);
        this.vertex(c, n, color);
    }

    private void vertex(Vector3f p, Vector3f n, int color) {
        this.vertices.add(new Vertex(p.x, p.y, p.z, n.x, n.y, n.z, color));
    }

    public ByteBuffer bytes() {
        ByteBuffer out = ByteBuffer.allocateDirect(this.vertices.size() * 20).order(ByteOrder.nativeOrder());
        for (Vertex v : this.vertices) {
            out.putFloat(v.x).putFloat(v.y).putFloat(v.z).put((byte)(v.rgb >> 16)).put((byte)(v.rgb >> 8)).put((byte)v.rgb).put((byte)-1).put((byte)(v.nx * 127.0f)).put((byte)(v.ny * 127.0f)).put((byte)(v.nz * 127.0f)).put((byte)0);
        }
        out.flip();
        return out;
    }

    public void ellipsoid(float cx, float cy, float cz, float rx, float ry, float rz, int color) {
        int lat = 10;
        int lon = 14;
        for (int i = 0; i < lat; ++i) {
            for (int j = 0; j < lon; ++j) {
                Vector3f a = KnifeMesh.point(cx, cy, cz, rx, ry, rz, Math.PI * (double)i / (double)lat, Math.PI * 2 * (double)j / (double)lon);
                Vector3f b = KnifeMesh.point(cx, cy, cz, rx, ry, rz, Math.PI * (double)(i + 1) / (double)lat, Math.PI * 2 * (double)j / (double)lon);
                Vector3f c = KnifeMesh.point(cx, cy, cz, rx, ry, rz, Math.PI * (double)(i + 1) / (double)lat, Math.PI * 2 * (double)(j + 1) / (double)lon);
                Vector3f d = KnifeMesh.point(cx, cy, cz, rx, ry, rz, Math.PI * (double)i / (double)lat, Math.PI * 2 * (double)(j + 1) / (double)lon);
                this.smooth(a, KnifeMesh.normal(a, cx, cy, cz, rx, ry, rz), b, KnifeMesh.normal(b, cx, cy, cz, rx, ry, rz), c, KnifeMesh.normal(c, cx, cy, cz, rx, ry, rz), color);
                this.smooth(a, KnifeMesh.normal(a, cx, cy, cz, rx, ry, rz), c, KnifeMesh.normal(c, cx, cy, cz, rx, ry, rz), d, KnifeMesh.normal(d, cx, cy, cz, rx, ry, rz), color);
            }
        }
    }

    private static Vector3f normal(Vector3f p, float x, float y, float z, float rx, float ry, float rz) {
        return new Vector3f((p.x - x) / (rx * rx), (p.y - y) / (ry * ry), (p.z - z) / (rz * rz)).normalize();
    }

    private void smooth(Vector3f a, Vector3f na, Vector3f b, Vector3f nb, Vector3f c, Vector3f nc, int color) {
        this.vertex(a, na, color);
        this.vertex(b, nb, color);
        this.vertex(c, nc, color);
    }

    private static Vector3f point(float cx, float cy, float cz, float rx, float ry, float rz, double a, double b) {
        return new Vector3f(cx + rx * (float)(Math.sin(a) * Math.cos(b)), cy + ry * (float)Math.cos(a), cz + rz * (float)(Math.sin(a) * Math.sin(b)));
    }

    public void tube(Vector3f a, Vector3f b, float radiusA, float radiusB, int color) {
        Vector3f axis = new Vector3f((Vector3fc)b).sub((Vector3fc)a).normalize();
        Vector3f side = axis.cross((Vector3fc)new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f());
        if ((double)side.lengthSquared() < 0.01) {
            side = axis.cross((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f());
        }
        side.normalize();
        Vector3f up = new Vector3f((Vector3fc)axis).cross((Vector3fc)side).normalize();
        int n = 14;
        for (int i = 0; i < n; ++i) {
            Vector3f r1 = new Vector3f((Vector3fc)side).mul((float)Math.cos((double)(i * 2) * Math.PI / (double)n)).fma((float)Math.sin((double)(i * 2) * Math.PI / (double)n), (Vector3fc)up);
            Vector3f r2 = new Vector3f((Vector3fc)side).mul((float)Math.cos((double)((i + 1) * 2) * Math.PI / (double)n)).fma((float)Math.sin((double)((i + 1) * 2) * Math.PI / (double)n), (Vector3fc)up);
            Vector3f p1 = new Vector3f((Vector3fc)a).fma(radiusA, (Vector3fc)r1);
            Vector3f p2 = new Vector3f((Vector3fc)b).fma(radiusB, (Vector3fc)r1);
            Vector3f p3 = new Vector3f((Vector3fc)b).fma(radiusB, (Vector3fc)r2);
            Vector3f p4 = new Vector3f((Vector3fc)a).fma(radiusA, (Vector3fc)r2);
            this.smooth(p1, r1, p2, r1, p3, r2, color);
            this.smooth(p1, r1, p3, r2, p4, r2, color);
            this.triangle(a, p4, p1, color);
            this.triangle(b, p2, p3, color);
        }
    }

    public void ring(float x, float y, float z, float radius, float thickness, int color) {
        int n = 44;
        int k = 12;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < k; ++j) {
                Vector3f a = KnifeMesh.torus(x, y, z, radius, thickness, i, j, n, k);
                Vector3f b = KnifeMesh.torus(x, y, z, radius, thickness, i + 1, j, n, k);
                Vector3f c = KnifeMesh.torus(x, y, z, radius, thickness, i + 1, j + 1, n, k);
                Vector3f d = KnifeMesh.torus(x, y, z, radius, thickness, i, j + 1, n, k);
                this.smooth(a, KnifeMesh.torusNormal(i, j, n, k), b, KnifeMesh.torusNormal(i + 1, j, n, k), c, KnifeMesh.torusNormal(i + 1, j + 1, n, k), color);
                this.smooth(a, KnifeMesh.torusNormal(i, j, n, k), c, KnifeMesh.torusNormal(i + 1, j + 1, n, k), d, KnifeMesh.torusNormal(i, j + 1, n, k), color);
            }
        }
    }

    private static Vector3f torusNormal(int i, int j, int n, int k) {
        double a = (double)(i * 2) * Math.PI / (double)n;
        double b = (double)(j * 2) * Math.PI / (double)k;
        return new Vector3f((float)(Math.cos(a) * Math.cos(b)), (float)(Math.sin(a) * Math.cos(b)), (float)Math.sin(b));
    }

    private static Vector3f torus(float x, float y, float z, float r, float t, int i, int j, int n, int k) {
        double a = (double)(i * 2) * Math.PI / (double)n;
        double b = (double)(j * 2) * Math.PI / (double)k;
        return new Vector3f(x + (float)(((double)r + (double)t * Math.cos(b)) * Math.cos(a)), y + (float)(((double)r + (double)t * Math.cos(b)) * Math.sin(a)), z + (float)((double)t * Math.sin(b)));
    }

    public void polygon(float[][] p, float thickness, int face, int edge) {
        int n = p.length;
        List<int[]> tris = KnifeMesh.triangulate(p);
        float z = thickness * 0.5f;
        for (int[] t : tris) {
            this.triangle(KnifeMesh.v(p[t[0]], z), KnifeMesh.v(p[t[1]], z), KnifeMesh.v(p[t[2]], z), face);
            this.triangle(KnifeMesh.v(p[t[2]], -z), KnifeMesh.v(p[t[1]], -z), KnifeMesh.v(p[t[0]], -z), face);
        }
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            this.triangle(KnifeMesh.v(p[i], z), KnifeMesh.v(p[i], -z), KnifeMesh.v(p[j], -z), edge);
            this.triangle(KnifeMesh.v(p[i], z), KnifeMesh.v(p[j], -z), KnifeMesh.v(p[j], z), edge);
        }
    }

    public void blade(float[][] outline, float thickness, int face, int edge) {
        int n = outline.length;
        float cx = 0.0f;
        float cy = 0.0f;
        for (float[] p : outline) {
            cx += p[0];
            cy += p[1];
        }
        cx /= (float)n;
        cy /= (float)n;
        float[][] inner = new float[n][2];
        for (int i = 0; i < n; ++i) {
            inner[i][0] = cx + (outline[i][0] - cx) * 0.9f;
            inner[i][1] = cy + (outline[i][1] - cy) * 0.94f;
        }
        float z = thickness / 2.0f;
        for (int[] t : KnifeMesh.triangulate(inner)) {
            this.triangle(KnifeMesh.v(inner[t[0]], z), KnifeMesh.v(inner[t[1]], z), KnifeMesh.v(inner[t[2]], z), face);
            this.triangle(KnifeMesh.v(inner[t[2]], -z), KnifeMesh.v(inner[t[1]], -z), KnifeMesh.v(inner[t[0]], -z), face);
        }
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            this.triangle(KnifeMesh.v(outline[i], 0.0f), KnifeMesh.v(inner[i], z), KnifeMesh.v(inner[j], z), edge);
            this.triangle(KnifeMesh.v(outline[i], 0.0f), KnifeMesh.v(inner[j], z), KnifeMesh.v(outline[j], 0.0f), edge);
            this.triangle(KnifeMesh.v(outline[j], 0.0f), KnifeMesh.v(inner[j], -z), KnifeMesh.v(inner[i], -z), edge);
            this.triangle(KnifeMesh.v(outline[j], 0.0f), KnifeMesh.v(inner[i], -z), KnifeMesh.v(outline[i], 0.0f), edge);
        }
    }

    private static Vector3f v(float[] p, float z) {
        return new Vector3f(p[0], p[1], z);
    }

    public static List<int[]> triangulate(float[][] p) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        double area = 0.0;
        for (int i = 0; i < p.length; ++i) {
            ids.add(i);
            int j = (i + 1) % p.length;
            area += (double)(p[i][0] * p[j][1] - p[j][0] * p[i][1]);
        }
        if (area < 0.0) {
            Collections.reverse(ids);
        }
        ArrayList<int[]> triangles = new ArrayList<int[]>();
        int guard = 0;
        while (ids.size() > 3 && guard++ < p.length * p.length) {
            boolean cut = false;
            for (int i = 0; i < ids.size(); ++i) {
                int c;
                int b;
                int a = (Integer)ids.get((i + ids.size() - 1) % ids.size());
                if (KnifeMesh.cross(p[a], p[b = ((Integer)ids.get(i)).intValue()], p[c = ((Integer)ids.get((i + 1) % ids.size())).intValue()]) <= 1.0E-9) continue;
                boolean inside = false;
                Iterator iterator = ids.iterator();
                while (iterator.hasNext()) {
                    int d = (Integer)iterator.next();
                    if (d == a || d == b || d == c || !(KnifeMesh.cross(p[a], p[b], p[d]) >= -1.0E-9) || !(KnifeMesh.cross(p[b], p[c], p[d]) >= -1.0E-9) || !(KnifeMesh.cross(p[c], p[a], p[d]) >= -1.0E-9)) continue;
                    inside = true;
                    break;
                }
                if (inside) continue;
                triangles.add(new int[]{a, b, c});
                ids.remove(i);
                cut = true;
                break;
            }
            if (cut) continue;
            throw new IllegalArgumentException("Knife polygon cannot be triangulated");
        }
        if (ids.size() == 3) {
            triangles.add(new int[]{(Integer)ids.get(0), (Integer)ids.get(1), (Integer)ids.get(2)});
        }
        return triangles;
    }

    private static double cross(float[] a, float[] b, float[] c) {
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]);
    }

    public record Vertex(float x, float y, float z, float nx, float ny, float nz, int rgb) {
    }
}

