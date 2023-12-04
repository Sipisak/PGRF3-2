package lwjglutils;

import transforms.Mat4RotZ;
import transforms.Mat4Scale;
import transforms.Mat4Transl;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.glPopAttrib;
import static org.lwjgl.opengl.GL33.glPushAttrib;

public class OGLTextRenderer {
	private int width;
	private int height;
	private Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	private Color bgColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	private final Font font;

	private double rotationAngle = 0;
	private double scale = 1;
	int textureID;
	BufferedImage img;
	//private boolean doClear = false;
	private final Viewer viewer;
	//private ByteBuffer clearTexBuffer = BufferUtils.createByteBuffer(4);


	private static class Viewer {
		protected final int shaderProgram;
		protected final OGLBuffers buffers;
		protected final int locMat;


		private OGLBuffers createBuffers() {
			float[] vertexBufferData = {
					0, 0, 0, 1,
					1, 0, 1, 1,
					0, 1, 0, 0,
					1, 1, 1, 0 };
			int[] indexBufferData = { 0, 1, 2, 3 };

			OGLBuffers.Attrib[] attributes = {
					new OGLBuffers.Attrib("inPosition", 2),
					new OGLBuffers.Attrib("inTexCoord", 2) };

			return new OGLBuffers(vertexBufferData, attributes, indexBufferData);
		}

		private Viewer() {
			buffers = createBuffers();
			String[] SHADER_FRAG_SRC = {
					"#version 330\n",
					"in vec2 texCoords;",
					"out vec4 fragColor;",
					"uniform sampler2D drawTexture;",
					"void main() {",
					" 	fragColor = texture(drawTexture, texCoords);",
					"// 	if (length(fragColor.rgb) <= 0)",
					"// 		fragColor.a = 0.5;",
					"}"
			};
			String[] SHADER_VERT_SRC = {
					"#version 330\n",
					"in vec2 inPosition;",
					"in vec2 inTexCoord;",
					"uniform mat4 matTrans;",
					"out vec2 texCoords;",
					"void main() {",
					"	gl_Position = matTrans*vec4(inPosition , 0.0f, 1.0f);",
					"   texCoords = inTexCoord;",
					"}"
			};

			this.shaderProgram = ShaderUtils.loadProgram(SHADER_VERT_SRC, SHADER_FRAG_SRC, null, null, null, null);
			locMat = glGetUniformLocation(shaderProgram, "matTrans");
		}

		private void view(int textureID, double x, double y, double w, double h, double rotationAngle, double scale) {
			if (glIsProgram(shaderProgram)) {
				glPushAttrib(GL_DEPTH_BUFFER_BIT|GL_ENABLE_BIT);
				int[] sp = {'0'};
				glGetIntegerv(GL_CURRENT_PROGRAM, sp);
				glUseProgram(shaderProgram);
				glActiveTexture(GL_TEXTURE0);
				glEnable(GL_TEXTURE_2D);
				glEnable(GL_BLEND);
				glDisable(GL_DEPTH_TEST);
				glDisable(GL_CULL_FACE);
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				glUniformMatrix4fv(locMat, false, ToFloatArray
					.convert(new Mat4Scale(w, h, 1)
									.mul(new Mat4Scale(scale))
									.mul(new Mat4RotZ(rotationAngle))
									.mul(new Mat4Transl(x,y,0))
							));
				glBindTexture(GL_TEXTURE_2D, textureID);
				glUniform1i(glGetUniformLocation(shaderProgram, "drawTexture"), 0);
				buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
				glDisable(GL_TEXTURE_2D);
				glDisable(GL_BLEND);
				glUseProgram(sp[0]);
				glPopAttrib();

			}
		}

		@Override
        protected void finalize() throws Throwable {
			super.finalize();
			//if (glIsProgram(shaderProgram))
			//	glDeleteProgram(shaderProgram);
		}
	}

	/**
	 * Create TextRenderer object
	 *
	 * @param width
	 *            width of output rendering frame
	 * @param height
	 *            height of output rendering frame
	 * @param font
	 * 			  font
	 */
	public OGLTextRenderer(int width, int height, Font font) {
		this.font = font;
		int w = width;
		int h = height;
		if (width<8)
			w = 8;
		if (height<8)
			h = 8;
		resize(w, h);
		viewer = new Viewer();
	}

	/**
	 * Create TextRenderer object
	 *
	 * @param width
	 *            width of output rendering frame
	 * @param height
	 *            height of output rendering frame
	 */
	public OGLTextRenderer(int width, int height) {
		this(width, height, new Font("SansSerif", Font.PLAIN, 12));
	}

	/**
	 * Update size of output rendering frame
	 * 
	 * @param width
	 *            updated width of output rendering frame
	 * @param height
	 *            updated height of output rendering frame
	 */
	public void resize(int width, int height) {
		if (width<=0)
			return;
		if (height<=0)
			return;
		this.width = width;
		this.height = height;
		if (glIsTexture(textureID))
			glDeleteTextures(textureID);
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		//glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
	}
	/**
	 * Changes the rotation angle in radians. The default angle is 0.
	 *
	 * @param rotationAngle the rotation angle of the rendering text
	 */
	public void setRotationAngle(double rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	/**
	 * Changes the scale factor.The default scale factor is 1.
	 *
	 * @param scale the scale factor of the rendering text
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * Changes the current color. The default color is opaque white.
	 *
	 * @param color the new color to use for rendering text
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Changes the background current color. The default color is opaque black.
	 *
	 * @param bgColor the new background color
	 */
	public void setBackgroundColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * Invert color and background current color.
	 */
	public void invert() {
		Color color = this.bgColor;
		this.bgColor = this.color;
		this.color = color;
	}

	/**
	 * Draw string on 2D coordinates of the raster frame
	 * 
	 * @param x
	 *            x position of string in range <0, width-1> of raster frame
	 * @param y
	 *            y position of string in range <0, height-1> of raster frame
	 * @param s
	 *            string to draw
	 */
	public void addStr2D(int x, int y, String s) {
		if (s != null){
			Graphics gr = img.getGraphics();
			gr.setFont(font);
			Rectangle2D textBox = gr.getFontMetrics().getStringBounds(s, 0,
					s.length(), gr);
			int x1 = Math.max(0, x + (int)textBox.getMinX());
			x1 = Math.min(width, x1);
			int x2 = Math.max(0, x+(int)textBox.getMaxX());
			x2 = Math.min(width, x2);
			int y1 = Math.max(0, y+ (int)textBox.getMinY());
			y1 = Math.min(height, y1 );
			int y2 = Math.max(0, y+(int)textBox.getMaxY());
			y2 = Math.min(height, y2);
			int w = x2-x1;
			int h = y2-y1;

			if (w>0 && h>0) {
				gr.setColor(bgColor);
				gr.fillRect(x1, y1, w, h);
				gr.setColor(color);
				gr.drawString(s, x, y);

				glBindTexture(GL_TEXTURE_2D, textureID);
				int[] array = new int[w * h];
				img.getRGB(x1, y1, w, h, array, 0, w);
				glBindTexture(GL_TEXTURE_2D, textureID);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

				glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, array);
				glViewport(-width, -height, 2*width, 2*height);

				viewer.view(textureID, x/(double)width, 1-y/(double)height,w/(double)width, h/(double)height, rotationAngle, scale);
			}
		}
	}

	@Deprecated
	public void clear() {
	}

	@Deprecated
	public void draw(){
	}
	
	@Override
    protected void finalize() throws Throwable{
		super.finalize();
		viewer.finalize();
		//if (glIsTexture(textureID))
		//	glDeleteTextures(textureID);

	}
}
