package solids;

public class Triangle extends Solid {

    public Triangle() {
        float zOffset = 2.0f;
        // vertex buffer
        float[] vertices = {
                0.f,  0.f + zOffset,   1.f, 0.f, 0.f,
                1.f,  0.f + zOffset,   0.f, 1.f, 0.f,
                0.f,  1.f + zOffset,   0.f, 0.f, 1.f,
        };

        // index buffer
        int[] indices = {
                0, 1, 2
        };

        // popsat kartě data
        lwjglutils.OGLBuffers.Attrib[] attributes = {
                new lwjglutils.OGLBuffers.Attrib("inPosition", 2), // 2 floats
                new lwjglutils.OGLBuffers.Attrib("inColor", 3) // 3 floats
        };

        buffers = new lwjglutils.OGLBuffers(vertices, attributes, indices);
    }
}
