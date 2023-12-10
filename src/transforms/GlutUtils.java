package transforms;

import static org.lwjgl.opengl.GL11.*;

public class GlutUtils {

    public static void glutSolidSphere(double r, int lats, int longs) {
        int i, j;
        for (i = 0; i < lats; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i) / lats);
            double z0 = -Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double) (i + 1) / lats);
            double z1 = -Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            glBegin(GL_QUAD_STRIP);
            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                glTexCoord2f(j / (float) longs, (i) / (float) lats);
                glNormal3d(x * zr0, y * zr0, z0);
                glVertex3d(r * x * zr0, r * y * zr0, r * z0);

                glTexCoord2f(j / (float) longs, (i + 1) / (float) lats);
                glNormal3d(x * zr1, y * zr1, z1);
                glVertex3d(r * x * zr1, r * y * zr1, r * z1);
            }
            glEnd();
        }

    }

    public static void glutWireSphere(double r, int lats, int longs) {
        int i, j;
        glBegin(GL_LINES);
        for (i = 0; i < lats; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i) / lats);
            double z0 = -Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double) (i + 1) / lats);
            double z1 = -Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double lng1 = 2 * Math.PI * (double) (j) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);
                double x1 = Math.cos(lng1);
                double y1 = Math.sin(lng1);

                glVertex3d(r * x * zr0, r * y * zr0, r * z0);
                glVertex3d(r * x * zr1, r * y * zr1, r * z1);
                glVertex3d(r * x * zr0, r * y * zr0, r * z0);
                glVertex3d(r * x1 * zr0, r * y1 * zr0, r * z0);
            }
        }
        glEnd();
    }

    public static void glutWireTorus(double ir, double or, int lats, int longs) {
        int i, j;
        glBegin(GL_LINES);
        for (i = 0; i < lats; i++) {
            double lat0 = 2 * Math.PI * (double) (i) / lats;
            double z0 = -ir * Math.sin(lat0);
            double zr0 = or + ir*Math.cos(lat0);

            double lat1 = 2 * Math.PI * (double) (i + 1) / lats;
            double z1 = -ir * Math.sin(lat1);
            double zr1 = or + ir*Math.cos(lat1);

            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double lng1 = 2 * Math.PI * (double) (j) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);
                double x1 = Math.cos(lng1);
                double y1 = Math.sin(lng1);

                glVertex3d(x * zr0, y * zr0, z0);
                glVertex3d(x * zr1, y * zr1,  z1);
                glVertex3d(x * zr0, y * zr0,  z0);
                glVertex3d(x1 * zr0, y1 * zr0, z0);
            }
        }
        glEnd();
    }

    public static void glutSolidTorus(double ir, double or, int lats, int longs) {
        int i, j;
        for (i = 0; i < lats; i++) {
            double lat0 = 2 * Math.PI * (-0.5 + (double) (i) / lats);
            double z0 = -ir * Math.sin(lat0);
            double zr0 = or + ir*Math.cos(lat0);

            double lat1 = 2 * Math.PI * (-0.5 + (double) (i + 1) / lats);
            double z1 = -ir * Math.sin(lat1);
            double zr1 = or + ir*Math.cos(lat1);

            glBegin(GL_QUAD_STRIP);
            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                glTexCoord2f(j / (float) longs, (i) / (float) lats);
                glNormal3d(x * zr0 - x * (ir + or)/2., y * zr0 - y*(ir + or) / 2, z0);
                glVertex3d(x * zr0,  y * zr0, z0);

                glTexCoord2f(j / (float) longs, (i + 1) / (float) lats);
                glNormal3d(x * zr1 - x * (ir + or)/2., y * zr1 - y*(ir + or) / 2, z1);
                glVertex3d(x * zr1,  y * zr1, z1);
            }
            glEnd();
        }

    }public static void glutWireCylinder(double r,double z, int lats, int longs) {
        int i, j;
        glBegin(GL_LINES);
        for (i = 0; i < lats; i++) {
            double lat0 = (double) i / lats - 0.5;
            double z0 = -z * lat0;

            double lat1 = (double) (i + 1) / lats - 0.5;
            double z1 = -z * lat1 ;

            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double lng1 = 2 * Math.PI * (double) (j) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);
                double x1 = Math.cos(lng1);
                double y1 = Math.sin(lng1);

                glVertex3d(r * x , r * y, z0);
                glVertex3d(r * x, r * y, z1);

                glVertex3d(r * x , r * y, z0);
                glVertex3d(r * x1, r * y1, z0);
            }
        }
        for (j = 0; j <= longs; j++) {
            double lng = 2 * Math.PI * (double) (j - 1) / longs;
            double lng1 = 2 * Math.PI * (double) (j) / longs;
            double x = Math.cos(lng);
            double y = Math.sin(lng);
            double x1 = Math.cos(lng1);
            double y1 = Math.sin(lng1);

            glVertex3d(0 , 0, z / 2f);
            glVertex3d(r * x, r * y, z / 2f);

            glVertex3d(0 , 0, -z / 2f);
            glVertex3d(r * x, r * y, -z / 2f);

            glVertex3d(r * x , r * y, -z / 2f);
            glVertex3d(r * x1, r * y1, -z / 2f);

            glVertex3d(r * x , r * y, z / 2f);
            glVertex3d(r * x1, r * y1, z / 2f);

        }
        glEnd();

    }

    public static void glutSolidCylinder(double r,double z, int lats, int longs) {
        int i, j;
        for (i = 0; i < lats; i++) {
            double lat0 = (double) i / lats - 0.5;
            double z0 = -z * lat0;

            double lat1 = (double) (i + 1) / lats - 0.5;
            double z1 = -z * lat1 ;

            glBegin(GL_QUAD_STRIP);
            for (j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                glTexCoord2f(j / (float) longs, (i) / (float) lats);
                glNormal3d(x, y, 0);
                glVertex3d(r * x , r * y, z0);

                glTexCoord2f(j / (float) longs, (i + 1) / (float) lats);
                glNormal3d(x, y, 0);
                glVertex3d(r * x, r * y, z1);
            }
            glEnd();
        }
        glBegin(GL_TRIANGLE_FAN);
        glTexCoord2f(0.5f, 0.5f);
        glNormal3d(0, 0, 1);
        glVertex3d( 0, 0, z / 2f);
        for (j = 0; j <= longs; j++) {
            double lng = 2 * Math.PI * (double) (j - 1) / longs;
            double x = Math.cos(lng);
            double y = Math.sin(lng);

            glTexCoord2d(x/2 + 0.5, y/2 + 0.5);
            glVertex3d(r * x , r * y, z / 2f);
        }
        glEnd();
        glBegin(GL_TRIANGLE_FAN);
        glTexCoord2f(0.5f, 0.5f);
        glNormal3d(0, 0, -1);
        glVertex3d( 0, 0, -z / 2f);
        for (j = longs; j >= 0; j--) {
            double lng = 2 * Math.PI * (double) (j - 1) / longs;
            double x = Math.cos(lng);
            double y = Math.sin(lng);

            glTexCoord2d(x/2 + 0.5, y/2 + 0.5);
            glVertex3d(r * x , r * y, -z / 2f);
        }
        glEnd();
    }


    public static void glutSolidCube(double size) {
        double[][] n = {
                {-1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {1.0, 0.0, 0.0},
                {0.0, -1.0, 0.0},
                {0.0, 0.0, 1.0},
                {0.0, 0.0, -1.0}
        };
        int[][] faces = {
                {0, 1, 2, 3},
                {3, 2, 6, 7},
                {7, 6, 5, 4},
                {4, 5, 1, 0},
                {5, 6, 2, 1},
                {7, 4, 0, 3}
        };
        float[][] v = new float[8][3];
        v[0][0] = v[1][0] = v[2][0] = v[3][0] = (float) -size / 2;
        v[4][0] = v[5][0] = v[6][0] = v[7][0] = (float) size / 2;
        v[0][1] = v[1][1] = v[4][1] = v[5][1] = (float) -size / 2;
        v[2][1] = v[3][1] = v[6][1] = v[7][1] = (float) size / 2;
        v[0][2] = v[3][2] = v[4][2] = v[7][2] = (float) -size / 2;
        v[1][2] = v[2][2] = v[5][2] = v[6][2] = (float) size / 2;


        for (int i = 5; i >= 0; i--) {
            glBegin(GL_QUADS);
            glNormal3dv(n[i]);
            glTexCoord2f(0, 0);
            glVertex3fv(v[faces[i][0]]);
            glTexCoord2f(0, 1);
            glVertex3fv(v[faces[i][1]]);
            glTexCoord2f(1, 1);
            glVertex3fv(v[faces[i][2]]);
            glTexCoord2f(1, 0);
            glVertex3fv(v[faces[i][3]]);
            glEnd();
        }
    }

    public static void glutWireCube(double size) {
        float[][] v = new float[8][3];
        v[0][0] = v[1][0] = v[2][0] = v[3][0] = (float) -size / 2;
        v[4][0] = v[5][0] = v[6][0] = v[7][0] = (float) size / 2;
        v[0][1] = v[1][1] = v[4][1] = v[5][1] = (float) -size / 2;
        v[2][1] = v[3][1] = v[6][1] = v[7][1] = (float) size / 2;
        v[0][2] = v[3][2] = v[4][2] = v[7][2] = (float) -size / 2;
        v[1][2] = v[2][2] = v[5][2] = v[6][2] = (float) size / 2;

        for (int i = 0; i < 4; i++) {
            glBegin(GL_LINES);
            glVertex3fv(v[i]);
            glVertex3fv(v[(i + 1) % 4]);
            glVertex3fv(v[4 + i]);
            glVertex3fv(v[4 + (i + 1) % 4]);
            glVertex3fv(v[i]);
            glVertex3fv(v[4 + i]);
            glEnd();
        }
    }

}
