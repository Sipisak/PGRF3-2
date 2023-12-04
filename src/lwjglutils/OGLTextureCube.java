package lwjglutils;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import org.lwjgl.BufferUtils;

import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Vec2D;

public class OGLTextureCube implements OGLTexture {
	
	private final int textureID;
	
	private static class TargetSize {
		private final int width, height;
		public TargetSize(int width, int height) {
			this.width = width; this.height = height;
		}
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}

	}
	
	private final TargetSize[] targetSize;
	public static final String[] SUFFICES_POS_NEG = { "posx", "negx", "posy", "negy", "posz", "negz" };
	public static final String[] SUFFICES_POS_NEG_FLIP_Y = { "posx", "negx", "negy", "posy", "posz", "negz" };
	public static final String[] SUFFICES_POSITIVE_NEGATIVE = { "positive_x", "negative_x", "positive_y", "negative_y", "positive_z", "negative_z" };
	public static final String[] SUFFICES_POSITIVE_NEGATIVE_FLIP_Y = { "positive_x", "negative_x", "negative_y", "positive_y", "positive_z", "negative_z" };
	public static final String[] SUFFICES_RIGHT_LEFT = { "right", "left", "top", "bottom", "front", "back" };
	public static final String[] SUFFICES_RIGHT_LEFT_FLIP_Y  = { "right", "left", "top", "bottom", "front", "back" };
	private static final int[] TARGETS = { GL_TEXTURE_CUBE_MAP_POSITIVE_X,
	                                         GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
	                                         GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
	                                         GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
	                                         GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
	                                         GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
	
	public static class Viewer extends OGLTexture2D.Viewer {
		private static final String[]SHADER_VERT_SRC = {
				"#version 330\n",
				"in vec2 inPosition;", 
				"in vec2 inTexCoord;", 
				"uniform mat4 matTrans;",
				"out vec2 texCoord;", 
				"void main() {",
				"	gl_Position = matTrans * vec4(inPosition , 0.0f, 1.0f);",
				"   texCoord.x = inTexCoord.x;",
				"   texCoord.y = 1. - inTexCoord.y;",
				"}"
			};
		
		private static final String[] SHADER_FRAG_SRC = { 
				"#version 330\n",
				"in vec2 texCoord;", 
				"out vec4 fragColor;", 
				"uniform samplerCube drawTexture;",
				"uniform int level;",
				"void main() {",
				" 	//fragColor = vec4( texCoord.xy, 0.0, 1.0);", 
				"	vec2 coord;", 
				//top
				"	if ((texCoord.y <= 1.0) &&(texCoord.y >= 2.0/3.0) && (texCoord.x >= 1.0/4.0) && (texCoord.x <= 2.0/4.0)){", 
				"		coord.y = (texCoord.y - 2.0/3.0) * 3.0 * 2.0 - 1.0;", 
				"		coord.x = (texCoord.x - 1.0/4.0) * 4.0 * 2.0 - 1.0;", 
				"	 	if (level >= 0)",
				" 			fragColor = textureLod(drawTexture, vec3(coord.x, -1.0, -coord.y), level);",
				"		else",
				"			fragColor = texture(drawTexture, vec3(coord.x, -1.0, -coord.y));",
				"	}else",
				"	if ((texCoord.y >= 0.0) &&(texCoord.y <= 1.0/3.0) && (texCoord.x >= 1.0/4.0) && (texCoord.x <= 2.0/4.0)){", 
				"		coord.y = (texCoord.y) * 3.0 * 2.0 - 1.0;", 
				"		coord.x = (texCoord.x - 1.0/4.0) * 4.0 * 2.0 - 1.0;",
				"	 	if (level >= 0)",
				" 			fragColor = textureLod(drawTexture, vec3(coord.x, 1.0, coord.y), level);",
				"		else",
				"			fragColor = texture(drawTexture, vec3(coord.x, 1.0, coord.y));",
				"	}else", 
				//front
				"	if ((texCoord.y <= 2.0/3.0) && (texCoord.y >= 1.0/3.0) && (texCoord.x >= 1.0/4.0) && (texCoord.x <= 2.0/4.0)){", 
				"		coord.y = (texCoord.y - 1.0/3.0) * 3.0 * 2.0 - 1.0;", 
				"		coord.x = (texCoord.x - 1.0/4.0) * 4.0 * 2.0 - 1.0;",
				"	 	if (level >= 0)",
				" 			fragColor = textureLod(drawTexture, vec3(coord.x, -coord.y, +1.0), level);",
				"		else",
				"			fragColor = texture(drawTexture, vec3( coord.x, -coord.y, +1.0));",
				"	}else", 
				"	if ((texCoord.y <= 2.0/3.0) && (texCoord.y >= 1.0/3.0) && (texCoord.x >= 3.0/4.0) && (texCoord.x <= 4.0/4.0)){", 
				"		coord.y = (texCoord.y - 1.0/3.0) * 3.0 * 2.0 - 1.0;", 
				"		coord.x = (texCoord.x - 3.0/4.0) * 4.0 * 2.0 - 1.0;",
				"	 	if (level >= 0)",
				" 			fragColor = textureLod(drawTexture, vec3(-coord.x, -coord.y, -1.0), level);",
				"		else",
				"			fragColor = texture(drawTexture, vec3( -coord.x, -coord.y, -1.0));",
				"	}else", 
		   	    //left
				"	if ((texCoord.y <= 2.0/3) && (texCoord.y >= 1.0/3.0) && (texCoord.x >= 0.0) && (texCoord.x <= 1.0/4.0)){", 
				"		coord.y = (texCoord.y - 1.0/3.0) * 3.0 * 2.0 - 1.0;", 
				"		coord.x = (texCoord.x ) * 4.0 * 2.0 - 1.0;",
				"	 	if (level >= 0)",
				" 			fragColor = textureLod(drawTexture, vec3(-1.0, -coord.y, coord.x), level);",
				"		else",
				"			fragColor = texture(drawTexture, vec3( -1.0, -coord.y, coord.x));",
				"	}else", 
				"	if ((texCoord.y <= 2.0/3.0) && (texCoord.y >= 1.0/3.0) && (texCoord.x >= 1.0/2.0) && (texCoord.x <= 3.0/4.0)){", 
				"		coord.y = (texCoord.y - 1.0/3) * 3.0 * 2.0 - 1.0;", 
				"		coord.x = (texCoord.x - 2.0/4) * 4.0 * 2.0 - 1.0;",
				"	 	if (level >= 0)",
				" 			fragColor = textureLod(drawTexture, vec3(+1.0, -coord.y, -coord.x), level);",
				"		else",
				"			fragColor = texture(drawTexture, vec3( +1.0, -coord.y, -coord.x));",
				"	} else", 
				"		discard;",
				"}"
			};
		public Viewer() {
			super(ShaderUtils.loadProgram(SHADER_VERT_SRC, SHADER_FRAG_SRC, null, null, null, null ));
		}

		@Override
		public void view(int textureID, Vec2D xy, Vec2D scale, int level){
			if (glIsProgram(shaderProgram)) {
				glPushAttrib(GL_DEPTH_BUFFER_BIT|GL_ENABLE_BIT);
				int[] sp = {'0'};
				glGetIntegerv(GL_CURRENT_PROGRAM, sp);
				glUseProgram(shaderProgram);
				glActiveTexture(GL_TEXTURE0);
				glEnable(GL_TEXTURE_CUBE_MAP);
				glUniformMatrix4fv(locMat, false, ToFloatArray
						.convert(new Mat4Scale(scale.getX(), scale.getY(), 1).mul(new Mat4Transl(xy.getX(), xy.getY(), 0))));
				glUniform1i(locLevel, level);
				glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
				glUniform1i(glGetUniformLocation(shaderProgram, "drawTexture"), 0);
				buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
				glDisable(GL_TEXTURE_CUBE_MAP);
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
	
	private OGLTextureCube() {
		targetSize = new TargetSize[6];
		textureID = glGenTextures();
		bind();
	}
		
	private void read(String fileName, int target) throws IOException {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        
        System.out.print("Reading texture file " + fileName);
		
        ByteBuffer imageBuffer  = OGLTexture2D.ioResourceToByteBuffer(fileName, 1024);
        
        if (!stbi_info_from_memory(imageBuffer, width, height, components))
            throw new IOException("Failed to read image information: " + stbi_failure_reason());

        ByteBuffer data = stbi_load_from_memory(imageBuffer, width, height, components, 4);
        
        if (data == null)
            throw new IOException("Failed to load image: " + stbi_failure_reason());
        
        System.out.println(" ... OK [" + width.get(0) + "x" + height.get(0) + "]");
        
        data.rewind();

        targetSize[target] = new TargetSize(width.get(0), height.get(0));
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
        glTexImage2D(TARGETS[target], 0, GL_RGBA, 
				width.get(0), height.get(0), 0, 
				GL_RGBA, GL_UNSIGNED_BYTE, data);
		stbi_image_free(data);
	}

	private void readTextures(String[] fileNames) throws IOException{
		for (int i = 0; i < fileNames.length; i++) {
			read(fileNames[i],i);
		}
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		//glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

	}

	public OGLTextureCube(String[] fileNames) throws IOException {
		this();
		readTextures(fileNames);
	}

	public OGLTextureCube(String fileName, String[] suffixes) throws IOException {
		this();
		String baseName=fileName.substring(0,fileName.lastIndexOf('.'));
    	String suffix=fileName.substring(fileName.lastIndexOf('.')+1);
		String[] fullNames = new String[suffixes.length];
		for (int i = 0; i < suffixes.length; i++) {
    		fullNames[i] = baseName + suffixes[i] + "." + suffix;
    	}
		readTextures(fullNames);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
	}

	@Override
	public void bind(int shaderProgram, String name, int slot) {
		bind();
		glActiveTexture(GL_TEXTURE0 + slot);
		glUniform1i(glGetUniformLocation(shaderProgram, name), slot);
	}

	@Override
	public void bind(int shaderProgram, String name) {
		bind(shaderProgram, name, 0);
	}

	@Override
	public int getTextureId(){
		return textureID; 
	}
	
	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format, Buffer buffer, int cubeFaceIndex) {
		bind();
		if (format instanceof OGLTexImageFloat.Format)
			glTexSubImage2D(TARGETS[cubeFaceIndex], 0, 0, 0, 
				targetSize[cubeFaceIndex].getWidth(),targetSize[cubeFaceIndex].getHeight(), 
				format.getPixelFormat(), format.getPixelType(), (ByteBuffer) buffer);

		if (format instanceof OGLTexImageByte.Format)
			glTexSubImage2D(TARGETS[cubeFaceIndex], 0, 0, 0, 
				targetSize[cubeFaceIndex].getWidth(),targetSize[cubeFaceIndex].getHeight(), 
				format.getPixelFormat(), format.getPixelType(), (FloatBuffer) buffer);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> Buffer getTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format, int cubeFaceIndex) {
		bind();
		if (format instanceof OGLTexImageFloat.Format) {
			FloatBuffer buffer = format.newBuffer(targetSize[cubeFaceIndex].getWidth(),
					targetSize[cubeFaceIndex].getHeight());
			glGetTexImage(TARGETS[cubeFaceIndex], 0, format.getPixelFormat(), format.getPixelType(), buffer);
			return buffer;
		}
		if (format instanceof OGLTexImageByte.Format) {
			ByteBuffer buffer = format.newBuffer(targetSize[cubeFaceIndex].getWidth(),
					targetSize[cubeFaceIndex].getHeight());
			glGetTexImage(TARGETS[cubeFaceIndex], 0, format.getPixelFormat(), format.getPixelType(), buffer);
			return buffer;
		}
		return null;
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTexImage(OGLTexImageType image, int cubeFaceIndex) {
		setTextureBuffer(image.getFormat(), image.getDataBuffer(), cubeFaceIndex);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLTexImageType getTexImage(
			OGLTexImage.Format<OGLTexImageType> format, int cubeFaceIndex) {
		OGLTexImageType image = format.newTexImage(
				 targetSize[cubeFaceIndex].getWidth(),  targetSize[cubeFaceIndex].getHeight());
		image.setDataBuffer(getTextureBuffer(format, cubeFaceIndex));
		return image;
	}
	
	@Override
	public String toString() {
		String text = String.format(Locale.US,
				 "OGLTextureCube: " + "ID: " + getTextureId() + ", [" + targetSize[0].getWidth() + "x" +
						 targetSize[0].getHeight() +  "x" +  targetSize[1].getHeight() + "] \n");
		return  text;
	}	
	
	@Override
    protected void finalize() throws Throwable {
		super.finalize();
		//if (glIsTexture(textureID))
		//	glDeleteTextures(textureID);
	}
}
