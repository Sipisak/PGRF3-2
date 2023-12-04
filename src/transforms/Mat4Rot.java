package transforms;

import java.util.Optional;

/**
 * A 4x4 matrix of right-handed rotation about general axis
 *
 * @author PGRF FIM UHK
 * @version 2020
 */
public class Mat4Rot extends Mat4Identity {

    /**
     * Creates a 4x4 transformation matrix equivalent to right-handed rotation
     * about general axis
     *
     * @param alpha rotation angle in radians
     * @param x     x coordinate of rotation axis
     * @param y     y coordinate of rotation axis
     * @param z     z coordinate of rotation axis
     */
    public Mat4Rot(final double alpha, final double x, final double y, final double z) {
        this(Math.sin(alpha), Math.cos(alpha), new Vec3D(x, y, z));
    }

    /**
     * Creates a 4x4 transformation matrix equivalent to right-handed rotation
     * about general axis
     *
     * @param sinAlpha sin of rotation angle
     * @param cosAlpha cos of rotation angle
     * @param rotAxis  rotation axis
     */

    public Mat4Rot(final double sinAlpha, final double cosAlpha, final Vec3D rotAxis) {
        Optional<Vec3D> norm = rotAxis.normalized();
        if (norm.isPresent()) {
            double ac = 1.0 - cosAlpha;
            Vec3D axis = norm.get();

            mat[0][0] = axis.getX() * axis.getX() * ac + cosAlpha;
            mat[0][1] = axis.getX() * axis.getY() * ac + axis.getZ() * sinAlpha;
            mat[0][2] = axis.getX() * axis.getZ() * ac - axis.getY() * sinAlpha;

            mat[1][0] = axis.getY() * axis.getX() * ac - axis.getZ() * sinAlpha;
            mat[1][1] = axis.getY() * axis.getY() * ac + cosAlpha;
            mat[1][2] = axis.getY() * axis.getZ() * ac + axis.getX() * sinAlpha;

            mat[2][0] = axis.getZ() * axis.getX() * ac + axis.getY() * sinAlpha;
            mat[2][1] = axis.getZ() * axis.getY() * ac - axis.getX() * sinAlpha;
            mat[2][2] = axis.getZ() * axis.getZ() * ac + cosAlpha;
        }
    }

    /**
     * Creates a 4x4 transformation matrix equivalent to right-handed rotation
     * about general axis
     *
     * @param alpha rotation angle in radians
     * @param axis  rotation axis
     */
    public Mat4Rot(final double alpha, final Vec3D axis) {
        this(alpha, axis.getX(), axis.getY(), axis.getZ());
    }
}