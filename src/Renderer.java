import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import solids.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.*;

import java.awt.*;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.SystemColor.window;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL20.*;

/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

	private Camera camera;
	private Mat4 projection;
	private float time;
	private OGLTexture2D texture;
	private double ox, oy;
	private boolean mouseButton1 = false;
	private final Mat4 model = new Mat4Identity();


	@Override
	public void init() {
		// cam, proj
		camera = new Camera()
				.withPosition(new Vec3D(-1.5f, -1.5f, 3.f))
				.withAzimuth(Math.toRadians(45))
				.withZenith(Math.toRadians(-45))

				.withFirstPerson(true);
		projection = new Mat4PerspRH(Math.PI / 4, height / (float) width, 0.1f, 100.f);

		// texture
		try {
			texture = new OGLTexture2D("textures/bricks.jpg");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	}

	@Override
	public void display() {

		time += 0.01f;

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

	}


	private final GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			switch (key) {
				case GLFW_KEY_W -> // W
						camera = camera.forward(0.1f);
				case GLFW_KEY_A -> // A
						camera = camera.left(0.1f);
				case GLFW_KEY_S -> // S
						camera = camera.backward(0.1f);
				case GLFW_KEY_D -> // D
						camera = camera.right(0.1f);
				case GLFW_KEY_Q -> // Q
						camera = camera.up(0.1f);
				case GLFW_KEY_E -> // E
						camera = camera.down(0.1f);

			}

			if (action == GLFW_PRESS) {
				switch (key) {

					case GLFW_KEY_B -> // B
							glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
					case GLFW_KEY_N -> // N
							glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
					case GLFW_KEY_M -> { // M
						glEnable(GL_POINT_SMOOTH);
						glPointSize(5.0f);
						glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);

					}
				}

			}
		}
	};

	private final GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
		@Override
		public void invoke(long window, int w, int h) {
		}
	};

	private final GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

			if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
				mouseButton1 = true;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				ox = xBuffer.get(0);
				oy = yBuffer.get(0);
			}

			if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
				mouseButton1 = false;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double x = xBuffer.get(0);
				double y = yBuffer.get(0);
				camera = camera.addAzimuth(Math.PI * (ox - x) / width)
						.addZenith(Math.PI * (oy - y) / width);

				ox = x;
				oy = y;
			}
		}

	};

	private final GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double x, double y) {
			if (mouseButton1) {
				camera = camera.addAzimuth(Math.PI * (ox - x) / width)
						.addZenith(Math.PI * (oy - y) / width);

				ox = x;
				oy = y;
			}
		}
	};

	private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		@Override
		public void invoke(long window, double dx, double dy) {
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