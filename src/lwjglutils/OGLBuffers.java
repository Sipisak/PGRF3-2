package lwjglutils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.lwjgl.BufferUtils;

//import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL33.*;
public class OGLBuffers {
	static public class Attrib {
		String name;
		int dimension;
		boolean normalize = false;
		int offset = -1;

		public Attrib(String name, int dimension) {
			this.name = name;
			this.dimension = dimension;
		}

		public Attrib(String name, int dimension, int offsetInFloats) {
			this.name = name;
			this.dimension = dimension;
			this.offset = 4 * offsetInFloats;
		}

		public Attrib(String name, int dimension, boolean normalize) {
			this.name = name;
			this.dimension = dimension;
			this.normalize = normalize;
		}

		public Attrib(String name, int dimension, boolean normalize, int offsetInFloats) {
			this.name = name;
			this.dimension = dimension;
			this.normalize = normalize;
			this.offset = 4 * offsetInFloats;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, 
					 "new Attrib( /*name:*/ " + name +
					 ", /*dimension:*/ "+ dimension + 
					 ", /*normalize:*/ " + normalize +
					 ", /*offset:*/ " + offset + ")");
		}
	}

	protected class VertexBuffer {
		int id, stride;
		Attrib[] attributes;

		public VertexBuffer(int id, int stride, Attrib[] attributes) {
			this.id = id;
			this.stride = stride;
			this.attributes = attributes;
		}
		
		@Override
		public String toString() {
			String text = "";
			text += String.format(Locale.US, 
					 "VertexBuffer ID: " + id +
					 ", stride: " + stride +
					 ", length: " + attributes.length );
			for (int i = 0; i< attributes.length; i++ ){
				text += "\n\t\t" + i + ": " + attributes[i].toString() ;
			}
			return  text;
		}
	}

	protected List<VertexBuffer> vertexBuffers = new ArrayList<>();
	protected List<Integer> attribArrays = null;
	protected int indexBuffer;
	protected int indexCount = -1;
	protected int vertexCount = -1;

	public OGLBuffers(float[] vertexData, Attrib[] attributes, int[] indexData) {
		addVertexBuffer(vertexData, attributes);
		if (indexData != null)
			setIndexBuffer(indexData);
	}

	public OGLBuffers(float[] vertexData, int floatsPerVertex, Attrib[] attributes, int[] indexData) {
		addVertexBuffer(vertexData, floatsPerVertex, attributes);
		if (indexData != null)
			setIndexBuffer(indexData);
	}

	public void addVertexBuffer(float[] data, Attrib[] attributes) {
		if (attributes == null || attributes.length == 0)
			return;

		int floatsPerVertex = 0;
		for (int i = 0; i < attributes.length; i++)
			floatsPerVertex += attributes[i].dimension;

		addVertexBuffer(data, floatsPerVertex, attributes);
	}

	public void addVertexBuffer(float[] data, int floatsPerVertex, Attrib[] attributes) {
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length)
				.put(data).rewind();
		int bufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, bufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

		if (data.length % floatsPerVertex != 0)
			throw new RuntimeException(
					"The total number of floats is incongruent with the number of floats per vertex.");
		if (vertexCount < 0)
			vertexCount = data.length / floatsPerVertex;
		else if (vertexCount != data.length / floatsPerVertex)
			System.out.println("Warning: GLBuffers.addVertexBuffer: vertex count differs from the first one.");

		vertexBuffers.add(new VertexBuffer(bufferID, floatsPerVertex * 4, attributes));
	}

	public void setIndexBuffer(int[] data) {
		indexCount = data.length;
		IntBuffer indexBufferBuffer = BufferUtils.createIntBuffer(indexCount)
				.put(data).rewind();
		indexBuffer = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferBuffer,
				GL_STATIC_DRAW);
	}

	public void bind(int shaderProgram) {
		if (attribArrays != null)
			for (Integer attrib : attribArrays)
				glDisableVertexAttribArray(attrib);
		attribArrays = new ArrayList<>();
		for (VertexBuffer vb : vertexBuffers) {
			glBindBuffer(GL_ARRAY_BUFFER, vb.id);
			int offset = 0;
			for (int j = 0; j < vb.attributes.length; j++) {
				int location = glGetAttribLocation(shaderProgram, vb.attributes[j].name);
				if (location >= 0) {// due to optimization GLSL on a graphic card
					attribArrays.add(location);
					glEnableVertexAttribArray(location);
					glVertexAttribPointer(location, vb.attributes[j].dimension, GL_FLOAT,
							vb.attributes[j].normalize, vb.stride,
							vb.attributes[j].offset < 0 ? offset : vb.attributes[j].offset);
				}
				offset += 4 * vb.attributes[j].dimension;
			}
		}

		if (indexBuffer !=0)
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

	}

	public void unbind() {
		if (attribArrays != null) {
			for (Integer attrib : attribArrays)
				glDisableVertexAttribArray(attrib);
			attribArrays = null;
		}
	}

	public void draw(int topology, int shaderProgram) {
		// gl.glUseProgram(shaderProgram);
		bind(shaderProgram);
		if (indexBuffer == 0) {
			glDrawArrays(topology, 0, vertexCount);
		} else {
			glDrawElements(topology, indexCount, GL_UNSIGNED_INT, 0);
		}
		unbind();
	}

	public void draw(int topology, int shaderProgram, int count) {
		draw(topology, shaderProgram, count, 0);
	}

	public void draw(int topology, int shaderProgram, int count, int start) {
		// gl.glUseProgram(shaderProgram);
		bind(shaderProgram);
		if (indexBuffer == 0) {
			glDrawArrays(topology, start, count);
		} else {
			glDrawElements(topology, count, GL_UNSIGNED_INT, start * 4L);
		}
		unbind();
	}

	@Override
	public String toString() {
		String text = "";
		text += String.format(Locale.US, 
				 "OGLBuffers" +
				 ", indexCount: " + indexCount +
				 ", vertexCount: " + vertexCount );
		for (VertexBuffer vb: vertexBuffers){
			text += "\n\t" + vb.toString() ;
		}
		return  text;
	}
	
	/*@Override
	public void finalize() throws Throwable {
		super.finalize();
		if (indexBuffer != 0)
			glDeleteBuffers(indexBuffer);
		for (int i = 0; i < vertexBuffers.size(); i++)
			glDeleteBuffers(vertexBuffers.get(i).id);
		
	}*/
}
