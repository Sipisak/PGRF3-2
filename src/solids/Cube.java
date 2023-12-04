package solids;

public class Cube extends Solid {

    public Cube() {
        // vertex buffer
        float[] vertices = {
                // Front face
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f,

                // Back face
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                // Right face
                1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

                // Left face
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f,

                // Top face
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
        };

        // index buffer
        int[] indices = {
                // Front face
                0, 1, 2, 0, 2, 3,

                // Back face
                4, 5, 6, 4, 6, 7,

                // Right face
                8, 9, 10, 8, 10, 11,

                // Left face
                12, 13, 14, 12, 14, 15,

                // Top face
                16, 17, 18, 16, 18, 19,

                // Bottom face
                20, 21, 22, 20, 22, 23,
        };

        // describe the data
        lwjglutils.OGLBuffers.Attrib[] attributes = {
                new lwjglutils.OGLBuffers.Attrib("inPosition", 3), // 3 floats
                new lwjglutils.OGLBuffers.Attrib("inTexCoord", 2) // 2 floats for texture coordinates
        };

        buffers = new lwjglutils.OGLBuffers(vertices, attributes, indices);
    }
}
