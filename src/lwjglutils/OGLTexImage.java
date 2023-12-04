package lwjglutils;

import java.nio.Buffer;

public interface OGLTexImage<OGLTexImageType> {
	interface Format<OGLTexImageType> {
		int getInternalFormat();
		int getPixelFormat();
		int getPixelType();
		int getComponentCount();
		<B extends Buffer> B newBuffer(int width, int height);
		<B extends Buffer> B newBuffer(int width, int height, int depth);
		<B extends Buffer> B buffer(Buffer buffer);
		OGLTexImageType newTexImage(int width, int height);
		OGLTexImageType newTexImage(int width, int height, int depth);
	}
	int getWidth();
	int getHeight();
	int getDepth();
	<B extends Buffer> void setDataBuffer(B buffer);
	<B extends Buffer> B getDataBuffer();
	Format<OGLTexImageType> getFormat();
	void flipY();
}
