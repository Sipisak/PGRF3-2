package lwjglutils;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL33.*;

public class OGLRenderTarget {
	protected final int width, height, count;
	protected final int[] drawBuffers;
	protected final int frameBuffer;
	protected final OGLTexture2D[] colorBuffers;
	protected final OGLTexture2D depthBuffer;
	
	public OGLRenderTarget(int width, int height) {
		this(width, height, 1);
	}

	public OGLRenderTarget(int width, int height, int count) {
		this(width, height, count, new OGLTexImageFloat.Format(4));
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLRenderTarget(int width, int height,
			int count, OGLTexImage.Format<OGLTexImageType> format) {
		this(width, height, count, null, format);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLRenderTarget(int count,
			OGLTexImageType texImage) {
		this(texImage.getWidth(), texImage.getHeight(), count, List.of(texImage), texImage.getFormat());
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLRenderTarget(OGLTexImageType[] texImage) {
		this(texImage[0].getWidth(), texImage[0].getHeight(), texImage.length, Arrays.asList(texImage),
				texImage[0].getFormat());
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLRenderTarget(List<OGLTexImageType> texImage) {
		this(texImage.get(0).getWidth(), texImage.get(0).getHeight(), texImage.size(), texImage,
				texImage.get(0).getFormat());
	}

	private <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLRenderTarget(int width, int height,
			int count, List<OGLTexImageType> texImage, OGLTexImage.Format<OGLTexImageType> format) {
		this.width = width;
		this.height = height;
		this.count = count;
		this.colorBuffers = new OGLTexture2D[count];
		this.drawBuffers = new int[count];
		for (int i = 0; i < count; i++) {
			Buffer imageData = texImage == null ? null : texImage.get(i).getDataBuffer();
			colorBuffers[i] = new OGLTexture2D(width, height, 
					format.getInternalFormat(), format.getPixelFormat(), format.getPixelType(), imageData);
			drawBuffers[i] = GL_COLOR_ATTACHMENT0 + i;
		}
		this.depthBuffer = new OGLTexture2D(width, height, 
				GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT,
				GL_FLOAT, null);
		
		frameBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		for (int i = 0; i < count; i++)
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D,
					colorBuffers[i].getTextureId(), 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,
				depthBuffer.getTextureId(), 0);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("There is a problem with the FBO");
		}
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glDrawBuffers(drawBuffers);
		glViewport(0, 0, width, height);
	}

	public void bindColorTexture(int shaderProgram, String name, int slot) {
		bindColorTexture(shaderProgram, name, slot, 0);
	}

	public void bindColorTexture(int shaderProgram, String name, int slot, int bufferIndex) {
		colorBuffers[bufferIndex].bind(shaderProgram, name, slot);
	}

	public void bindDepthTexture(int shaderProgram, String name, int slot) {
		depthBuffer.bind(shaderProgram, name, slot);
	}

	public OGLTexture2D getColorTexture() {
		return getColorTexture(0);
	}

	public OGLTexture2D getColorTexture(int bufferIndex) {
		if (bufferIndex<numberColorTextures()) return colorBuffers[bufferIndex];
		return colorBuffers[0];
	}

	public OGLTexture2D getDepthTexture() {
		return depthBuffer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int numberColorTextures(){ return colorBuffers.length; }
	@Override
	public String toString() {
		String text = "";
		text += String.format(Locale.US, 
				 "OGLRenderTarget: " + "[" + getWidth() + "x" +
						 getHeight() + "] \n");
		text += String.format(Locale.US, 
				 "	depth buffer: " + getDepthTexture().toString());
		for (int i= 0; i<colorBuffers.length; i++){
			text += String.format(Locale.US, 
					 "	" +  i + ". color buffer: " + getColorTexture(i).toString());
			}
		return  text;
	}
	@Override
    protected void finalize() throws Throwable {
		super.finalize();
		//if (glIsFramebuffer(frameBuffer))
		//	glDeleteFramebuffers(frameBuffer);
	}

}
