package solids;


import transforms.Col;

public class Axis extends Solid {
    public Axis(float x, float y, float z, Col color) {
        // vertex buffer
        float xOffset = -1.5f;
        float[] vertices = {
                0.f + xOffset, 0.f, 0.f, (float)color.getR(), (float)color.getG(), (float)color.getB(),
                x + xOffset, y, z, (float)color.getR(), (float)color.getG(), (float)color.getB()
        };

        // index buffer
        int[] indices = {
                0, 1
        };

        // popsat kartě data
        lwjglutils.OGLBuffers.Attrib[] attributes = {
                new lwjglutils.OGLBuffers.Attrib("inPosition", 3), // 2 floats
                new lwjglutils.OGLBuffers.Attrib("inPosition", 3)
        };

        buffers = new lwjglutils.OGLBuffers(vertices, attributes, indices);
    }
}
