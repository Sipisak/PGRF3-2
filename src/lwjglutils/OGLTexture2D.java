package lwjglutils;


import transforms.Mat4Scale;
import transforms.Mat4Transl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

import org.lwjgl.BufferUtils;
import transforms.Vec2D;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.*;

public class OGLTexture2D implements OGLTexture {
	private final int textureID;
	private final int width, height;
	
	public static class Viewer implements OGLTexture.Viewer {
		protected final int shaderProgram;
		protected final OGLBuffers buffers;
		protected final int locMat;
		protected final int locLevel;
		
		private static final String[] SHADER_VERT_SRC = {
				"#version 330\n",
				"in vec2 inPosition;", 
				"in vec2 inTexCoord;", 
				"uniform mat4 matTrans;",
				"out vec2 texCoords;", 
				"void main() {",
				"	gl_Position = matTrans * vec4(inPosition , 0.0f, 1.0f);",
				"   texCoords = inTexCoord;",
				"}"
			};
		
		private static final String[] SHADER_FRAG_SRC = { 
				"#version 330\n",
				"in vec2 texCoords;", 
				"out vec4 fragColor;", 
				"uniform sampler2D drawTexture;",
				"uniform int level;",
				"void main() {",
				" 	if (level >= 0)",
				" 		fragColor = textureLod(drawTexture, texCoords, level);",
				" 	else",
				" 		fragColor = texture(drawTexture, texCoords);",
				"}"
			};

		private OGLBuffers createBuffers() {
			float[] vertexBufferData = { 
					0, 0, 0, 0,
					1, 0, 1, 0,
					0, 1, 0, 1,
					1, 1, 1, 1 };
			int[] indexBufferData = { 0, 1, 2, 3 };

			OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 2),
					new OGLBuffers.Attrib("inTexCoord", 2) };

			return new OGLBuffers(vertexBufferData, attributes, indexBufferData);
		}

		public Viewer() {
			this(ShaderUtils.loadProgram(SHADER_VERT_SRC, SHADER_FRAG_SRC, null, null, null, null));
		}
		
		protected Viewer(int shaderProgram) {
			buffers = createBuffers();
			this.shaderProgram = shaderProgram; 
			locMat = glGetUniformLocation(shaderProgram, "matTrans");
			locLevel = glGetUniformLocation(shaderProgram, "level");
		}

		@Override
		public void view(int textureID) {
			view(textureID, -1, -1);
		}

		@Override
		public void view(int textureID, double x, double y) {
			view(textureID, x, y, 1.0, 1.0);
		}

		@Override
		public void view(int textureID, double x, double y, double scale) {
			view(textureID, x, y, scale, 1.0);
		}
		@Override
		public void view(int textureID, double x, double y, double scale, double aspectXY) {
			view(textureID, x, y, scale, aspectXY, -1);
		}

		@Override
		public void view(int textureID, double x, double y, double scale, double aspectXY, int level) {
			view(textureID, new Vec2D(x,y), new Vec2D(scale*aspectXY,scale),level);
		}

        @Override
        public void view(int textureID, Vec2D xy, Vec2D scale, int level) {
            if (glIsProgram(shaderProgram)) {
                glPushAttrib(GL_DEPTH_BUFFER_BIT|GL_ENABLE_BIT);
                int[] sp = {'0'};
                glGetIntegerv(GL_CURRENT_PROGRAM, sp);
                glUseProgram(shaderProgram);
                glActiveTexture(GL_TEXTURE0);
                glEnable(GL_TEXTURE_2D);
                glUniformMatrix4fv(locMat, false, ToFloatArray
                        .convert(new Mat4Scale(scale.getX(), scale.getY(), 1).mul(new Mat4Transl(xy.getX(), xy.getY(), 0))));
                glUniform1i(locLevel, level);
                glBindTexture(GL_TEXTURE_2D, textureID);
                glUniform1i(glGetUniformLocation(shaderProgram, "drawTexture"), 0);
                buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
                glDisable(GL_TEXTURE_2D);
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
		
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null)
            throw new IOException("Classpath resource not found: " + resource);
        File file = new File(url.getFile());
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
        } else
		{
			buffer = BufferUtils.createByteBuffer(bufferSize);
			InputStream source = url.openStream();
			if (source == null)
				throw new FileNotFoundException(resource);
			try {
				byte[] buf = new byte[bufferSize];
				while (true) {
					int bytes = source.read(buf, 0, buf.length);
					if (bytes == -1)
						break;
					if (buffer.remaining() < bytes) {
						int newCapacity = buffer.capacity();
						while ((buffer.position() + bytes) > newCapacity)
							newCapacity *= 2;
						buffer = resizeBuffer(buffer, newCapacity);
					}
					buffer.put(buf, 0, bytes);
				}
				buffer.flip();
			} finally {
				source.close();
			}
        }
        return buffer;
    }
    
	public OGLTexture2D(int width, int height, int internalFormat, int pixelFormat, int pixelType, Buffer buffer) {
		this.width = width;
		this.height = height;
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		if (pixelType == GL_FLOAT) {
			glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, 
					width, height, 0, 
					pixelFormat, pixelType, (FloatBuffer) buffer);}
    	if (pixelType == GL_UNSIGNED_BYTE) {
			glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, 
				width, height, 0, 
				pixelFormat, pixelType, (ByteBuffer) buffer);}	
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>>
	OGLTexture2D(int width, int height, OGLTexImage.Format<OGLTexImageType> format, Buffer buffer) {
		this(width,height,format.getInternalFormat(), format.getPixelFormat(), format.getPixelType(), buffer);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLTexture2D(OGLTexImageType image) {
		this(image.getWidth(),	image.getHeight(), 
				image.getFormat().getInternalFormat(), image.getFormat().getPixelFormat(), 
				image.getFormat().getPixelType(),  image.getDataBuffer());
	}
	
	public OGLTexture2D(String fileName) throws IOException {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        
        System.out.print("Reading texture file " + fileName);
		
        ByteBuffer imageBuffer  = ioResourceToByteBuffer(fileName, 1024);
        
        if (!stbi_info_from_memory(imageBuffer, width, height, components))
            throw new IOException("Failed to read image information: " + stbi_failure_reason());

        ByteBuffer data = stbi_load_from_memory(imageBuffer, width, height, components, 4);
        
        if (data == null)
            throw new IOException("Failed to load image: " + stbi_failure_reason());
        
        System.out.println(" ... OK [" + width.get(0) + "x" + height.get(0) + "]");
        
        data.rewind();

        this.width =  width.get(0);
		this.height = height.get(0);
        textureID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 
				this.width, this.height, 0, 
				GL_RGBA, GL_UNSIGNED_BYTE, data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        stbi_image_free(data);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>>
	void flipY(OGLTexImage.Format<OGLTexImageType> format) {
		OGLTexImage image = this.getTexImage(format);
		image.flipY();
		this.setTexImage(image);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format, Buffer buffer) {
		/*bind();
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, getWidth(), getHeight(), 
				format.getPixelFormat(), format.getPixelType(), buffer);
		*/
		setTextureBuffer(format, buffer, 0);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> Buffer getTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format) {
		/*bind();
		Buffer buffer = format.newBuffer(getWidth(), getHeight());
		glGetTexImage(GL_TEXTURE_2D, 0, format.getPixelFormat(), format.getPixelType(), buffer);
		return buffer;*/
		return getTextureBuffer(format, 0);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format, Buffer buffer, int level) {
		bind();
		buffer.rewind();
		if (format instanceof OGLTexImageFloat.Format) {
			glTexSubImage2D(GL_TEXTURE_2D, level, 0, 0, 
				getWidth() >> level, getHeight() >> level, 
				format.getPixelFormat(), format.getPixelType(), (FloatBuffer) buffer);}
		if (format instanceof OGLTexImageByte.Format) {
			glTexSubImage2D(GL_TEXTURE_2D, level, 0, 0, 
				getWidth() >> level, getHeight() >> level, 
				format.getPixelFormat(), format.getPixelType(), (ByteBuffer) buffer);}
		
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> Buffer getTextureBuffer(
			OGLTexImage.Format<OGLTexImageType> format, int level) {
		bind();
		if (format instanceof OGLTexImageFloat.Format) {
	    	FloatBuffer buffer = format.newBuffer(getWidth() >> level, getHeight() >> level);
			glGetTexImage(GL_TEXTURE_2D, level, format.getPixelFormat(), format.getPixelType(), buffer);
			buffer.rewind();
			
			return buffer;
		}
	    if (format instanceof OGLTexImageByte.Format) {
			ByteBuffer buffer = format.newBuffer(getWidth() >> level, getHeight() >> level);
			glGetTexImage(GL_TEXTURE_2D, level,  format.getPixelFormat(), format.getPixelType(), buffer);
			buffer.rewind();
			return buffer;
		}
		return null;
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTexImage(OGLTexImageType image) {
		setTextureBuffer(image.getFormat(), image.getDataBuffer());
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLTexImageType getTexImage(
			OGLTexImage.Format<OGLTexImageType> format) {
		OGLTexImageType image = format.newTexImage(getWidth(), getHeight());
		image.setDataBuffer(getTextureBuffer(format));
		return image;
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> void setTexImage(OGLTexImageType image, int level) {
		setTextureBuffer(image.getFormat(), image.getDataBuffer(), level);
	}

	public <OGLTexImageType extends OGLTexImage<OGLTexImageType>> OGLTexImageType getTexImage(
			OGLTexImage.Format<OGLTexImageType> format, int level) {
		OGLTexImageType image = format.newTexImage(getWidth() >> level, getHeight() >> level);
		image.setDataBuffer(getTextureBuffer(format, level));
		return image;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	@Override
	public void bind(int shaderProgram, String name, int slot) {
		glActiveTexture(GL_TEXTURE0 + slot);
		bind();
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BufferedImage toBufferedImage() {
		bind();
		int[] arr = new int[width*height];
		glGetTexImage (GL_TEXTURE_2D, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, arr);
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		image.setRGB (0, 0, getWidth(), getHeight(), arr, 0, getWidth());
		return image;
	}

	public void fromBufferedImage(BufferedImage img) {
		
		bind();
		int[] array = new int[getWidth() * getHeight()];
		img.getRGB(0, 0, getWidth(), getHeight(), array, 0, getWidth());
		glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, getWidth(), getHeight(), GL_BGRA,
				GL_UNSIGNED_INT_8_8_8_8_REV, array);
	}

	@Override
	public String toString() {
		String text = String.format(Locale.US,
				 "OGLTexture2D: " + "ID: " + getTextureId() + ", [" + getWidth() + "x" +
						 getHeight() + "] ");
		return  text;
	}
	
	@Override
    protected void finalize() throws Throwable{
		super.finalize();
		//if (glIsTexture(textureID))
		//	glDeleteTextures(textureID);
	}
	

}
