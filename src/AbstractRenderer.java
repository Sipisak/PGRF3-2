
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLUtils;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;


/**
 *
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public abstract class AbstractRenderer {
	int pass;
	protected int width;
	protected int height;
	protected OGLTextRenderer textRenderer;

	public void init() {
		OGLUtils.printOGLparameters();
		OGLUtils.printLWJLparameters();
		OGLUtils.printJAVAparameters();
		OGLUtils.shaderCheck();
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		textRenderer = new OGLTextRenderer(width, height);
	}

	public void display() {
		glViewport(0, 0, width, height);
		String text = this.getClass().getName() + ": look at console and try keys, mouse, wheel and window interaction ";

		pass++;
		// Set the clear color
		glClearColor((float)(Math.sin(pass/100.)/2+0.5),
				(float)(Math.cos(pass/200.)/2+0.5),
				(float)(Math.sin(pass/300.)/2+0.5), 0.0f);
		// clear the framebuffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//create and draw text
		textRenderer.clear();
		textRenderer.addStr2D(3, 20, text);
		textRenderer.addStr2D(3, 50, "pass " + pass);
		textRenderer.addStr2D(width-90, height-3, " (c) PGRF UHK");
		textRenderer.draw();
	}

	protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				// We will detect this in our rendering loop
				glfwSetWindowShouldClose(window, true);
			if (action == GLFW_RELEASE){
				System.out.println("Key release " + key);
			}
			if (action == GLFW_PRESS){
				System.out.println("Key pressed " + key);
			}
		}
	};

	protected GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
		@Override
		public void invoke(long window, int w, int h) {
			if (w > 0 && h > 0) {
				width = w;
				height = h;
				System.out.println("Windows resize to [" + w + ", " + h + "]");
				if (textRenderer != null)
					textRenderer.resize(width, height);
			}
		}
	};

	protected GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {

		@Override
		public void invoke(long window, int button, int action, int mods) {
			//mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
			DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xBuffer, yBuffer);
			double x = xBuffer.get(0);
			double y = yBuffer.get(0);

			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
				System.out.println("Mouse button 1 is pressed at coursor position [" + x + ", " + y + "]");
			}

			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
				System.out.println("Mouse button 1 is released at coursor position [" + x + ", " + y + "]");
			}
		}

	};

	protected GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double x, double y) {
			System.out.println("Coursor position [" + x + ", " + y + "]");
		}
	};

	protected GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		@Override public void invoke (long window, double dx, double dy) {
			System.out.println("Mouse whell velocity " + dy);
		}
	};

	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	public void dispose(){

	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}