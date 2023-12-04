package lwjglutils;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Vec2D;

public class OGLTextureVolume implements OGLTexture {
	private final int volumeTextureID;
	private final int width, height, depth;
	
	public static class Viewer extends OGLTexture2D.Viewer {
		private static final String[] shaderVertSrc = {
				"#version 330\n",
				"in vec2 inPosition;", 
				"in vec2 inTexCoord;", 
				"uniform mat4 matTrans;",
				"out vec2 texCoord;", 
				"void main() {",
				"	gl_Position = matTrans * vec4(inPosition , 0.0f, 1.0f);",
				"   texCoord = inTexCoord;",
				"}"
			};
		
		private static final String[] shaderFragSrc = {
				"#version 330\n",
				"in vec2 texCoord;", 
				"out vec4 fragColor;", 
				"uniform sampler3D drawTexture;",
				"uniform int level;",
				"void main() {",
				"	vec3 coord;", 
				"	int row = 4;",
				"	int column = 4;",
				"	int i = int(texCoord.x * column);",
				"	int j = int(texCoord.y * row);",
				"	coord.x = (texCoord.x * column) - i;",
				"	coord.y = (texCoord.y * row) - j;",
				"	coord.z = (i + j*column)/float(row*column);",
				"	//fragColor = vec4( coord.xyz, 1.0);",
				" 	if (level >= 0)",
				" 		fragColor = textureLod(drawTexture, coord, level);",
				" 	else",
				"		fragColor = texture(drawTexture, coord);",
				"}" 
			};

		public Viewer() {
			super(ShaderUtils.loadProgram(shaderVertSrc, shaderFragSrc, null, null, null, null ));
		}

		@Override
		public void view(int textureID, Vec2D xy, Vec2D scale, int level) {
			if (glIsProgram(shaderProgram)) {
				glPushAttrib(GL_DEPTH_BUFFER_BIT|GL_ENABLE_BIT);
				int[] sp = {'0'};
				glGetIntegerv(GL_CURRENT_PROGRAM, sp);
				glUseProgram(shaderProgram);
				glActiveTexture(GL_TEXTURE0);
				glEnable(GL_TEXTURE_3D);
				glUniformMatrix4fv(locMat, false, ToFloatArray
						.convert(new Mat4Scale(scale.getX(), scale.getY(), 1).mul(new Mat4Transl(xy.getX(), xy.getY(), 0))));
				glUniform1i(locLevel, level);
				glBindTexture(GL_TEXTURE_3D, textureID);
				glUniform1i(glGetUniformLocation(shaderProgram, "drawTexture"), 0);
				buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
				glDisable(GL_TEXTURE_3D);
				glUseProgram(sp[0]);
				glPopAttrib();
			}
		}
		
		@Override
		public void finalize() throws Throwable {
			super.finalize();
			//if (glIsProgram(shaderProgram))
			//	glDeleteProgram(shaderProgram);
		}
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLTextureVolume(OGLTexImageType volume) {
		this.width = volume.getWidth();
		this.height = volume.getHeight();
		this.depth = volume.getDepth();
		Buffer buffer = volume.getDataBuffer();
		volumeTextureID = glGenTextures();
		glBindTexture(GL_TEXTURE_3D, volumeTextureID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		if (volume.getFormat() instanceof OGLTexImageByte.Format) {
			glTexImage3D(GL_TEXTURE_3D, 0, volume.getFormat().getInternalFormat(), volume.getWidth(),
					volume.getHeight(), volume.getDepth(), 0, volume.getFormat().getPixelFormat(),
					volume.getFormat().getPixelType(), (ByteBuffer) buffer);
		}
		
		if (volume.getFormat() instanceof OGLTexImageFloat.Format) {
			glTexImage3D(GL_TEXTURE_3D, 0, volume.getFormat().getInternalFormat(), volume.getWidth(),
					volume.getHeight(), volume.getDepth(), 0, volume.getFormat().getPixelFormat(),
					volume.getFormat().getPixelType(), (FloatBuffer) buffer);
		}

		glGenerateMipmap(GL_TEXTURE_3D);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	}


	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format, Buffer buffer) {
		glBindTexture(GL_TEXTURE_3D, volumeTextureID);
		glTexSubImage3D(GL_TEXTURE_3D, 0, 0, 0, 0, 
				width, height, depth, 
				format.getPixelFormat(), format.getPixelType(), (ByteBuffer) buffer);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> Buffer getTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format) {
		glBindTexture(GL_TEXTURE_3D, volumeTextureID);
		Buffer buffer = format.newBuffer(width, height, depth);
		glGetTexImage(GL_TEXTURE_3D, 0, format.getPixelFormat(), format.getPixelType(), (ByteBuffer) buffer);
		return buffer;
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTexImage(OGLTexImageType volume) {
		setTextureBuffer(volume.getFormat(), volume.getDataBuffer());
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLTexImageType getTexImage(
			OGLTexImage.Format<OGLTexImageType> format) {
		OGLTexImageType image = format.newTexImage(width, height, depth);
		image.setDataBuffer(getTextureBuffer(format));
		return image;
	}
	
	@Override
	public void bind(int shaderProgram, String name, int slot) {
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_3D, volumeTextureID);
		glUniform1i(glGetUniformLocation(shaderProgram, name), slot);
	}
	
	@Override
	public void bind(int shaderProgram, String name) {
		bind(shaderProgram, name, 0);
	}
	
	@Override
	public int getTextureId(){
		return volumeTextureID; 
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	
	@Override
	public String toString() {
		String text = String.format(Locale.US,
				 "OGLTexture2D: " + "ID: " + getTextureId() + ", [" + getWidth() + "x" +
						 getHeight() + "x" + getDepth() + "] \n");
		return  text;
	}
		
	@Override
    protected void finalize() throws Throwable {
		super.finalize();
		//if (glIsTexture(volumeTextureID))
		//	glDeleteTextures(volumeTextureID);
	}
}
