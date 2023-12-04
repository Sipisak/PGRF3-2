package lwjglutils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Locale;

public class OGLTexImageFloat implements OGLTexImage<OGLTexImageFloat> {
	private float[] data;
	private final int width, height, depth;
	private final OGLTexImage.Format<OGLTexImageFloat> format;

	public static class Format implements OGLTexImage.Format<OGLTexImageFloat> {
		private final int componentCount;

		public Format(int componentCount) {
			this.componentCount = componentCount;
		}

		@Override
		public int getInternalFormat() {
			switch (componentCount) {
			case 1:
				return GL_R32F;
			case 2:
				return GL_RG32F;
			case 3:
				return GL_RGB32F;
			case 4:
				return GL_RGBA32F;
			default:
				return -1;
			}
		}

		@Override
		public int getPixelFormat() {
			switch (componentCount) {
			case 1:
				return GL_RED;
			case 2:
				return GL_RG;
			case 3:
				return GL_RGB;
			case 4:
				return GL_RGBA;
			default:
				return -1;
			}
		}

		@Override
		public int getPixelType() {
			return GL_FLOAT;
		}

		@Override
		public int getComponentCount() {
			return componentCount;
		}

		@Override
		public FloatBuffer buffer(Buffer buf) {
			return (FloatBuffer) buf;
		}

		@Override
		public FloatBuffer newBuffer(int width, int height) {
			return newBuffer(width, height, 1);
		}

		@Override
		public FloatBuffer newBuffer(int width, int height, int depth) {
			return ByteBuffer.allocateDirect(width * height * depth * componentCount * 4)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
		}

		@Override
		public OGLTexImageFloat newTexImage(int width, int height) {
			return newTexImage(width, height, 1);
		}

		@Override
		public OGLTexImageFloat newTexImage(int width, int height, int depth) {
			return new OGLTexImageFloat(width, height, depth, this);
		}
		@Override
		public String toString() {
			return String.format(Locale.US, 
					 "FloatFormat[" + "components:" + getComponentCount() + 
					 " internal format:" + getInternalFormat() +
					 " pixel format:" + getPixelFormat() + 
					 " pixel type:" + getPixelType() + "]");
		}
		
	}

	public static class FormatDepth extends Format {
		public FormatDepth() {
			super(1);
		}

		@Override
		public int getInternalFormat() {
			return GL_DEPTH_COMPONENT;
		}

		@Override
		public int getPixelFormat() {
			return GL_DEPTH_COMPONENT;
		}

		@Override
		public int getPixelType() {
			return GL_FLOAT;
		}
	}

	public static class FormatIntensity extends Format {
		public FormatIntensity() {
			super(1);
		}

		@Override
		public int getInternalFormat() {
			return 1;
		}

		@Override
		public int getPixelFormat() {
			return GL_LUMINANCE;
		}

		@Override
		public int getPixelType() {
			return GL_FLOAT;
		}
	}

	public OGLTexImageFloat(int width, int height, int componentCount) {
		this(width, height, 1, new OGLTexImageFloat.Format(componentCount));
	}

	public OGLTexImageFloat(int width, int height, int componentCount, float[] data) {
		this(width, height, 1, new OGLTexImageFloat.Format(componentCount), data);
	}

	public OGLTexImageFloat(int width, int height, int depth, int componentCount) {
		this(width, height, depth, new OGLTexImageFloat.Format(componentCount));
	}

	public OGLTexImageFloat(int width, int height, int depth, int componentCount, float[] data) {
		this(width, height, depth, new OGLTexImageFloat.Format(componentCount), data);
	}

	public OGLTexImageFloat(int width, int height, OGLTexImage.Format<OGLTexImageFloat> format) {
		this(width, height, 1, format, new float[width * height * format.getComponentCount()]);
	}

	public OGLTexImageFloat(int width, int height, OGLTexImage.Format<OGLTexImageFloat> format, float[] data) {
		this(width, height, 1, format, data);
	}

	public OGLTexImageFloat(int width, int height, int depth, OGLTexImage.Format<OGLTexImageFloat> format) {
		this(width, height, depth, format, new float[width * height * depth * format.getComponentCount()]);
	}

	public OGLTexImageFloat(int width, int height, int depth, OGLTexImage.Format<OGLTexImageFloat> format,
			float[] data) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.format = format;
		this.data = data;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void setDataBuffer(Buffer buffer) {
		if (buffer instanceof FloatBuffer && buffer.capacity() == width * height * depth * format.getComponentCount()) {
			buffer.rewind();
			data = new float[buffer.capacity()];
			((FloatBuffer) buffer).get(data);
		}
	}

	@Override
	public FloatBuffer getDataBuffer() {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(data.length * Float.BYTES); 
		// 4  bytes per float
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(data);
		buffer.position(0);

		return buffer;
	}

	@Override
	public OGLTexImage.Format<OGLTexImageFloat> getFormat() {
		return format;
	}

	public float[] getData() {
		return data;
	}

	public OGLTexImageByte toOGLTexImageByte() {
		return toOGLTexImageByte(format.getComponentCount());

	}

	public OGLTexImageByte toOGLTexImageByte(int componentCount) {
		byte[] array = new byte[width * height * componentCount];
		for (int z = 0; z < depth; z++)
			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					for (int i = 0; i < componentCount; i++)
						array[z * width * height * componentCount + y * width * componentCount + x * componentCount
								+ i] = (byte) (Math
										.min(data[z * width * height * format.getComponentCount()
												+ y * width * format.getComponentCount()
												+ x * format.getComponentCount() + i % format.getComponentCount()], 1.0)
										* 255.0);

		return new OGLTexImageByte(width, height, depth, new OGLTexImageByte.Format(componentCount), array);
	}

	public void setPixel(int x, int y, float value) {
		setVoxel(x, y, 0, 0, value);
	}

	public void setPixel(int x, int y, int component, float value) {
		setVoxel(x, y, 0, component, value);
	}

	public void setVoxel(int x, int y, int z, float value) {
		setVoxel(x, y, z, 0, value);
	}

	public void setVoxel(int x, int y, int z, int component, float value) {
		if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth && component >= 0
				&& component < format.getComponentCount()) {
			data[(z * width * height + y * width + x) * format.getComponentCount() + component] = value;
		}
	}

	public float getPixel(int x, int y) {
		return getVoxel(x, y, 0, 0);
	}

	public float getPixel(int x, int y, int component) {
		return getVoxel(x, y, 0, component);
	}

	public float getVoxel(int x, int y, int z) {
		return getVoxel(x, y, z, 0);
	}

	public float getVoxel(int x, int y, int z, int component) {
		float value = 0;
		if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth && component >= 0
				&& component < format.getComponentCount())
			value = data[(z * width * height + y * width + x) * format.getComponentCount() + component];
		return value;
	}

	public void flipY() {
		for (int z = 0; z < depth; z++)
			for (int y = 0; y < height/2; y++)
				for (int x = 0; x < width; x++)
					for (int i = 0; i < format.getComponentCount(); i++) {
						float value = getVoxel(x, y, z, i);
						setVoxel(x, y, z, i, getVoxel(x, height - y, z, i));
						setVoxel(x, height - y, z, i, value);
					}
	}

	@Override
	public String toString() {
		return String.format(Locale.US, 
				 "OGLTexImageFloat: " + "[" + getWidth() + "x" +
						 getHeight() + "x" + getDepth() + 
						 "] length:" + data.length + "x4 B, " +
						 "format: " + getFormat().toString());
	}
}
