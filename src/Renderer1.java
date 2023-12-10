

import static java.awt.SystemColor.window;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.awt.*;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import transforms.*;

public class Renderer1 extends AbstractRenderer{
    double ox, oy;
    boolean mouseButton1 = false;

    OGLBuffers buffers2, buffers;

    int shaderProgramCube,shaderProgram, locMat;

    Camera cam = new Camera();
    Mat4 proj = new Mat4PerspRH(Math.PI / 4, 1, 0.01, 1000.0);

    OGLTextureCube texture, secondTexture;
    private boolean switchTexture = false;
//    private List<Renderable> objects = new ArrayList<>();

    private boolean renderAdditionalObject = false;

    void createBuffers(float scaleFactor) {
        float[] cube = {
                // bottom (z-) face
                scaleFactor, 0, 0,    0, 0, -1,
                0, 0, 0,             0, 0, -1,
                scaleFactor, scaleFactor, 0, 0, 0, -1,
                0, scaleFactor, 0,    0, 0, -1,
                // top (z+) face
                scaleFactor, 0, scaleFactor,    0, 0, 1,
                0, 0, scaleFactor,             0, 0, 1,
                scaleFactor, scaleFactor, scaleFactor, 0, 0, 1,
                0, scaleFactor, scaleFactor,    0, 0, 1,
                // x+ face
                scaleFactor, scaleFactor, 0,    1, 0, 0,
                scaleFactor, 0, 0,             1, 0, 0,
                scaleFactor, scaleFactor, scaleFactor, 1, 0, 0,
                scaleFactor, 0, scaleFactor,    1, 0, 0,
                // x- face
                0, scaleFactor, 0,    -1, 0, 0,
                0, 0, 0,             -1, 0, 0,
                0, scaleFactor, scaleFactor, -1, 0, 0,
                0, 0, scaleFactor,    -1, 0, 0,
                // y+ face
                scaleFactor, scaleFactor, 0,    0, 1, 0,
                0, scaleFactor, 0,             0, 1, 0,
                scaleFactor, scaleFactor, scaleFactor, 0, 1, 0,
                0, scaleFactor, scaleFactor,    0, 1, 0,
                // y- face
                scaleFactor, 0, 0,    0, -1, 0,
                0, 0, 0,             0, -1, 0,
                scaleFactor, 0, scaleFactor, 0, -1, 0,
                0, 0, scaleFactor,    0, -1, 0
        };

        int[] indexBufferData = new int[36];
        for (int i = 0; i < 6; i++) {
            indexBufferData[i * 6] = i * 4;
            indexBufferData[i * 6 + 1] = i * 4 + 1;
            indexBufferData[i * 6 + 2] = i * 4 + 2;
            indexBufferData[i * 6 + 3] = i * 4 + 1;
            indexBufferData[i * 6 + 4] = i * 4 + 2;
            indexBufferData[i * 6 + 5] = i * 4 + 3;
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(cube, attributes, indexBufferData);
    }
    void createBuffers2() {
        float[] cube = {
                // bottom (z-) face
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                0, 1, 0,	0, 0, -1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,
                0, 0, 1,	0, 0, 1,
                1, 1, 1,	0, 0, 1,
                0, 1, 1,	0, 0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,
                1, 0, 0,	1, 0, 0,
                1, 1, 1,	1, 0, 0,
                1, 0, 1,	1, 0, 0,
                // x- face
                0, 1, 0,	-1, 0, 0,
                0, 0, 0,	-1, 0, 0,
                0, 1, 1,	-1, 0, 0,
                0, 0, 1,	-1, 0, 0,
                // y+ face
                1, 1, 0,	0, 1, 0,
                0, 1, 0,	0, 1, 0,
                1, 1, 1,	0, 1, 0,
                0, 1, 1,	0, 1, 0,
                // y- face
                1, 0, 0,	0, -1, 0,
                0, 0, 0,	0, -1, 0,
                1, 0, 1,	0, -1, 0,
                0, 0, 1,	0, -1, 0
        };

        int[] indexBufferData = new int[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i*6] = i*4;
            indexBufferData[i*6 + 1] = i*4 + 1;
            indexBufferData[i*6 + 2] = i*4 + 2;
            indexBufferData[i*6 + 3] = i*4 + 1;
            indexBufferData[i*6 + 4] = i*4 + 2;
            indexBufferData[i*6 + 5] = i*4 + 3;
        }
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers2 = new OGLBuffers(cube, attributes, indexBufferData);
    }





    @Override
    public void init() {
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        createBuffers2();
        createBuffers(10.0f);
        shaderProgram = ShaderUtils.loadProgram(("/"));
        shaderProgramCube = ShaderUtils.loadProgram("/skybox");

        glUseProgram(this.shaderProgramCube);

        locMat = glGetUniformLocation(shaderProgramCube, "mat");

        cam = cam.withPosition(new Vec3D(0, 0, 0))
                .withAzimuth(0)
                .withZenith(0)
                .withFirstPerson(false)
                .withRadius(0.2);


        try {
            texture = new OGLTextureCube("textures/sky_.jpg", OGLTextureCube.SUFFICES_RIGHT_LEFT);
            secondTexture = new OGLTextureCube("textures/snow_.jpg", OGLTextureCube.SUFFICES_POSITIVE_NEGATIVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void display() {
        glViewport(0, 0, width, height);

        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        // set the current shader to be used
        glUseProgram(shaderProgramCube);

//         Render the skybox (already rendered object)
        glUniformMatrix4fv(locMat, false,
                ToFloatArray.convert(new Mat4Transl(-0.5, -0.5, -0.5)
                        .mul(new Mat4RotX(Math.PI / 2))
                        .mul(cam.getViewMatrix()).mul(proj)));

        // bind and draw the skybox
        if (switchTexture) {
            // Switch to the second texture
            secondTexture.bind(shaderProgramCube, "texture", 0);
        } else {
            // Use the default texture
            texture.bind(shaderProgramCube, "texture", 0);
        }
        buffers.draw(GL_TRIANGLES, shaderProgramCube);


    }
    private static class Renderable {
        private final OGLBuffers buffers;
        private final OGLTextureCube texture;

        public Renderable(OGLBuffers buffers, OGLTextureCube texture) {
            this.buffers = buffers;
            this.texture = texture;
        }

        public OGLBuffers getBuffers() {
            return buffers;
        }

        public OGLTextureCube getTexture() {
            return texture;
        }
    }





    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key) {
                    case GLFW_KEY_W:
                        cam = cam.forward(1);
                        break;
                    case GLFW_KEY_D:
                        cam = cam.right(1);
                        break;
                    case GLFW_KEY_S:
                        cam = cam.backward(1);
                        break;
                    case GLFW_KEY_A:
                        cam = cam.left(1);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        cam = cam.down(1);
                        break;
                    case GLFW_KEY_LEFT_SHIFT:
                        cam = cam.up(1);
                        break;
                    case GLFW_KEY_R:
                        cam = cam.mulRadius(0.9f);
                        break;
                    case GLFW_KEY_F:
                        cam = cam.mulRadius(1.1f);
                        break;
                    case GLFW_KEY_M:
                        switchTexture = !switchTexture;
                        break;
//                    case GLFW_KEY_SPACE:
//                        // Toggle the rendering of the additional object
//                        if (renderAdditionalObject) {
//                            // Add the additional object to the list
//                            objects.add(new Renderable(buffers2, texture2));
//                        } else {
//                            // Remove the additional object from the list
//                            objects.remove(objects.size() - 1);
//                        }
//                        renderAdditionalObject = !renderAdditionalObject;
//                        break;
                }
            }
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 &&
                    (w != width || h != height)) {
                width = w;
                height = h;
                proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
                if (textRenderer != null)
                    textRenderer.resize(width, height);
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                cam = cam.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
            if (dy<0)
                cam = cam.mulRadius(0.9f);
            else
                cam = cam.mulRadius(1.1f);

        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }
}