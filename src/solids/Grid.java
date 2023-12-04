package solids;

import lwjglutils.OGLBuffers;

public class Grid extends Solid {

    /**
     * @param m - počet vrcholů v řádku
     * @param n - počet vrcholů ve sloupci
     */
    public Grid(final int m, final int n) {
        // vb
        float[] vb = new float[4 * m * n];
        // ib
        int[] ib = new int[6 * (m - 1) * (n - 1)];
        int index = 0;
        for (int i = 0; i < n; i++) {
            float x = i / (float) (n - 1);
            for (int j = 0; j < m; j++) {
                float y = j / (float) (m - 1);
                vb[index++] = x;
                vb[index++] = y;


                // Texture cords
                vb[index++] = x;
                vb[index++] = y;
            }
        }

        index = 0;
        for (int i = 0; i < n - 1; i++) {
            int offset = i * m;
            for (int j = 0; j < m - 1; j++) {
                // j=0, m=4
                ib[index++] = j + offset;
                ib[index++] = j + m + offset;
                ib[index++] = j + 1 + offset;

                ib[index++] = j + 1 + offset;
                ib[index++] = j + m + offset;
                ib[index++] = j + m + 1 + offset;

            }
        }

        lwjglutils.OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2),
                new lwjglutils.OGLBuffers.Attrib("inTexture", 2),
        };

        buffers = new OGLBuffers(vb, attributes, ib);
    }
}
