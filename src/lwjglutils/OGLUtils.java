package lwjglutils;

import static lwjglutils.ShaderUtils.COMPUTE_SHADER_SUPPORT_VERSION;
import static lwjglutils.ShaderUtils.GEOMETRY_SHADER_SUPPORT_VERSION;
import static lwjglutils.ShaderUtils.TESSELATION_SUPPORT_VERSION;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.*;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;


public class OGLUtils {

	/**
	 * Print version, vendor and extensions of current OpenGL
	 * 
	 */
	public static void printOGLparameters() {
		System.out.println("GL vendor: " + glGetString(GL_VENDOR));
		System.out.println("GL renderer: " + glGetString(GL_RENDERER));
		System.out.println("GL version: " + glGetString(GL_VERSION));
		System.out.println("GL shading language version: " + glGetString(GL_SHADING_LANGUAGE_VERSION)
				+ " (#version " + getVersionGLSL() + ")");
		System.out.println("GL extensions: " + getExtensions());
		System.out.println("GLSL version: " + getVersionGLSL());
		System.out.println("OpenGL extensions: " + getVersionOpenGL());

	}

	/**
	 * Get extensions of actual OpenGL
	 * 
	 */
	public static String getExtensions() {
		String extensions;
		if (getVersionGLSL() < getVersionOpenGL()) {
			// Deprecated in newer versions
			extensions = glGetString(GL_EXTENSIONS);
		} else {
			int[] numberExtensions = new int[1];
			glGetIntegerv(GL_NUM_EXTENSIONS, numberExtensions);
			extensions = glGetStringi(GL_EXTENSIONS, 1);
			for (int i = 1; i < numberExtensions[0]; i++) {
				extensions = extensions + " " + glGetStringi(GL_EXTENSIONS, i);
			}
		}
		return extensions;
	}

	/**
	 * Get supported GLSL version
	 * 
	 * @return version as integer number multiplied by 100, for GLSL 1.4 return
	 *         140, for GLSL 4.5 return 450, ...
	 */
	public static int getVersionGLSL() {
		String version = glGetString(GL_SHADING_LANGUAGE_VERSION);
		String[] parts = version.split(" ");
		parts = parts[0].split("\\.");
		int versionNumber = Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
		return versionNumber;
	}

	/**
	 * Get supported OpenGL version
	 * 
	 * @return version as integer number multiplied by 100, for OpenGL 3.3
	 *         return 330, ...
	 */
	public static int getVersionOpenGL() {
		String version = glGetString(GL_VERSION);
		String[] parts = version.split(" ");
		parts = parts[0].split("\\.");
		return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]) * 10;
	}

	/**
	 * Print parameters of actual LWJGL
	 * 
	 */
	public static void printLWJLparameters() {
		System.out.println("LWJGL " + Version.getVersion() + "!");
		Package p = Package.getPackage("org.lwjgl.opengl");
		System.out.println("LWJGL specification version: " + p.getSpecificationVersion());
		System.out.println("LWJGL implementation version: " + p.getImplementationVersion());
		System.out.println("LWJGL implementation title: " + p.getImplementationTitle());
		System.out.println("LWJGL implementation vendor: " + p.getImplementationVendor());
		
		GLCapabilities cap = GL.getCapabilities();
		System.out.print("LWJGL capabilities: ");
		 if (cap.OpenGL46) System.out.println("GL46");
	        else if (cap.OpenGL45) System.out.println("GL45");
	        else if (cap.OpenGL40) System.out.println("GL40");
	        else if (cap.OpenGL30) System.out.println("GL30");
	        else if (cap.OpenGL20) System.out.println("GL20");
	        else if (cap.OpenGL11) System.out.println("GL11");
	        else System.out.println("no GL");
	    
	}

	/**
	 * Print parameters of actual JAVA and OS
	 * 
	 */
	public static void printJAVAparameters() {
		System.out.println("Java version: " + System.getProperty("java.version"));
		System.out.println("Java runtime.version: " + System.getProperty("java.runtime.version"));
		System.out.println("Java vendor: " + System.getProperty("java.vendor"));
		System.out.println("Java model: " + System.getProperty("sun.arch.data.model"));
		System.out.println("OS arch: " + System.getProperty("os.arch"));
		System.out.println("OS name: " + System.getProperty("os.name"));
		System.out.println("OS version: " + System.getProperty("os.version"));
	}

	/**
	 * Check OpenGL shaders support
	 * 
	 */
	public static void shaderCheck() {
		String extensions = glGetString(GL_EXTENSIONS);

		if ((OGLUtils.getVersionGLSL() < getVersionOpenGL()) && (extensions.indexOf("GL_ARB_vertex_shader") == -1
				|| extensions.indexOf("GL_ARB_fragment_shader") == -1)) {
			throw new RuntimeException("Shaders are not available.");
		}

		System.out.println("This OpenGL (#version " + getVersionGLSL() + ") supports:\n vertex and fragment shader");

		if ((OGLUtils.getVersionGLSL() >= GEOMETRY_SHADER_SUPPORT_VERSION)
				|| (extensions.indexOf("geometry_shader") != -1))
			System.out.println(" geometry shader");

		if ((OGLUtils.getVersionGLSL() >= TESSELATION_SUPPORT_VERSION)
				|| (extensions.indexOf("tessellation_shader") != -1))
			System.out.println(" tessellation");

		if ((OGLUtils.getVersionGLSL() >= COMPUTE_SHADER_SUPPORT_VERSION)
				|| (extensions.indexOf("compute_shader") != -1))
			System.out.println(" compute shader");
	}
	
	/**
	 * Check GL error
	 * 
	 * @param longReport
	 *            type of report
	 */
	static public void checkGLError(String text, boolean longReport) {
		int err = glGetError();
		String errorName, errorDesc;

		while (err != GL_NO_ERROR) {

			switch (err) {
			case GL_INVALID_ENUM:
				errorName = "GL_INVALID_ENUM";
				errorDesc = "An unacceptable value is specified for an enumerated argument. The offending command is ignored and has no other side effect than to set the error flag.";
				break;
			case GL_INVALID_VALUE:
				errorName = "GL_INVALID_VALUE";
				errorDesc = "A numeric argument is out of range. The offending command is ignored and has no other side effect than to set the error flag.";
				break;
			case GL_INVALID_OPERATION:
				errorName = "GL_INVALID_OPERATION";
				errorDesc = "The specified operation is not allowed in the current state. The offending command is ignored and has no other side effect than to set the error flag.";
				break;
			case GL_INVALID_FRAMEBUFFER_OPERATION:
				errorName = "GL_INVALID_FRAMEBUFFER_OPERATION";
				errorDesc = "The framebuffer object is not complete. The offending command is ignored and has no other side effect than to set the error flag.";
				break;
			case GL_OUT_OF_MEMORY:
				errorName = "GL_OUT_OF_MEMORY";
				errorDesc = "There is not enough memory left to execute the command. The state of the GL is undefined, except for the state of the error flags, after this error is recorded.";
				break;
			default:
				return;
			}
			if (longReport)
				System.err.println(text + " GL error: " + err + " " + errorName + ": " + errorDesc);
			else
				System.err.println(text + " GL error: " + errorName);
			err = glGetError();
		}
	}

	/**
	 * Empty GL error
	 * 
	 */
	static public void emptyGLError() {
		int err = glGetError();
		while (err != GL_NO_ERROR) {
			err = glGetError();
		}
	}

	/**
	 * Check GL error
	 * 
	 */
	static public void checkGLError(String text) {
		checkGLError(text, false);
	}

	/**
	 * Check GL error
	 * 
	 */
	static public void checkGLError() {
		checkGLError("", false);
	}

	static public String getDebugSource(final int code) {
		switch (code) {
		case 0x8246:
			return "GL_DEBUG_SOURCE_API";
		case 0x8247:
			return "GL_DEBUG_SOURCE_WINDOW_SYSTEM";
		case 0x8248:
			return "GL_DEBUG_SOURCE_SHADER_COMPILER";
		case 0x8249:
			return "GL_DEBUG_SOURCE_THIRD_PARTY";
		case 0x824A:
			return "GL_DEBUG_SOURCE_APPLICATION";
		case 0x824B:
			return "GL_DEBUG_SOURCE_OTHER";
		}
		return "GL_DEBUG_SOURCE_UNKNOWN";
	}

	static public String getDebugType(final int code) {
		switch (code) {
		case 0x824C:
			return "GL_DEBUG_TYPE_ERROR";
		case 0x824D:
			return "GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR";
		case 0x824E:
			return "GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR";
		case 0x824F:
			return "GL_DEBUG_TYPE_PORTABILITY";
		case 0x8250:
			return "GL_DEBUG_TYPE_PERFORMANCE";
		case 0x8251:
			return "GL_DEBUG_TYPE_OTHER";
		case 0x8268:
			return "GL_DEBUG_TYPE_MARKER";
		case 0x8269:
			return "GL_DEBUG_TYPE_PUSH_GROUP";
		case 0x826A:
			return "GL_DEBUG_TYPE_POP_GROUP";
		}
		return "GL_DEBUG_TYPE_UNKNOWN";
	}

	static public String getDebugSeverity(final int code) {
		switch (code) {
		case 0x9146:
			return "GL_DEBUG_SEVERITY_HIGH";
		case 0x9147:
			return "GL_DEBUG_SEVERITY_MEDIUM";
		case 0x9148:
			return "GL_DEBUG_SEVERITY_LOW";
		case 0x826B:
			return "GL_DEBUG_SEVERITY_NOTIFICATION";
		}
		return "GL_DEBUG_SEVERITY_UNKNOWN";
	}
}
