package lwjglutils;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OGLModelOBJ {
private int topology;
	
	private OGLBuffers buffer;
	private FloatBuffer verticesBuffer, normalsBuffer, texCoordsBuffer;


	public FloatBuffer getVerticesBuffer() {
		return verticesBuffer;
	}

	public FloatBuffer getNormalsBuffer() {
		return normalsBuffer;
	}

	public FloatBuffer getTexCoordsBuffer() {
		return texCoordsBuffer;
	}

	public OGLBuffers getBuffers() {
		return buffer;
	}

	public int getTopology() {
		return topology;
	}

/*
	private List<Integer> geometryList;
	
	private List<OGLBuffers> bufferList;
	
	public List<OGLBuffers> getBufferList() {
		return bufferList;
	}

	public List<Integer> getGeometryList() {
		return geometryList;
	}
*/
	public OGLModelOBJ(String modelPath) {

		class OBJLoader{
			final List<float[]> vData = new ArrayList<float[]>(); // List of Vertex Coordinates
			final List<float[]> vtData = new ArrayList<float[]>(); // List of Texture Coordinates
			final List<float[]> vnData = new ArrayList<float[]>(); // List of Normal Coordinates
			final List<int[]> fv = new ArrayList<int[]>(); // Face Vertex Indices;
			final List<int[]> ft = new ArrayList<int[]>(); // Face Texture Indices
			final List<int[]> fn = new ArrayList<int[]>(); // Face Normal Indices
			
			OBJLoader(String modelPath) {
				loadOBJModel(modelPath);
				setFaceRenderType();
			}

			private void loadOBJModel(String modelPath) {
				try {
                    System.out.print("Reading model file " + modelPath);
                    // Open a file handle and read the models data
					InputStream is = OBJLoader.class.getResourceAsStream(modelPath);

					if (is == null) {
						System.err.println("File not found ");
						return;
					}

					BufferedReader br = null;
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String line = null;
					while ((line = br.readLine()) != null) {
						if (line.startsWith("#")) {
						} else if (line.equals("")) {
							// Ignore whitespace data
						} else if (line.startsWith("v ")) { 
							// Read in Vertex Data
							vData.add(processData(line));
						} else if (line.startsWith("vt ")) { 
							// Read Texture Coordinates
							vtData.add(processData(line));
						} else if (line.startsWith("vn ")) { 
							// Read Normal Coordinates
							vnData.add(processData(line));
						} else if (line.startsWith("f ")) { 
							// Read Face (index) Data
							processFaceData(line);
						}
					}
					is.close();
					br.close();
					System.out.println("OBJ model: " + modelPath + "... read");
				} catch (IOException e) {
					System.out.println("Failed to find or read OBJ: " + modelPath);
					System.err.println(e);
				}
			}

			private float[] processData(String read) {
				String[] s = read.split("\\s+");
				return (processFloatData(s)); 
			}

			private float[] processFloatData(String[] sdata) {
				float[] data = new float[sdata.length - 1];
				for (int loop = 0; loop < data.length; loop++) {
					data[loop] = Float.parseFloat(sdata[loop + 1]);
				}
				return data; 
			}

			private void processFaceData(String fread) {
				String[] s = fread.split("\\s+");
				if (fread.contains("//")) { 
					// Pattern is present if obj has only v and vn in face data
					for (int loop = 1; loop < s.length; loop++) {
						s[loop] = s[loop].replaceAll("//", "/1/"); 
						// insert a zero for missing vt data
					}
				}
				processfIntData(s); // Pass in face data
			}

			private void processfIntData(String[] sdata) {
									
				int[] vdata = new int[3];
				int[] vtdata = new int[3];
				int[] vndata = new int[3];

				for (int loop = 1; loop < sdata.length; loop++) {
					String s = sdata[loop];
					String[] temp = s.split("/");
					int index = loop - 1;
					if (loop>3) {	//make a new triangle as a triangle fan
						fv.add(vdata);		//save previous triangle
						ft.add(vtdata);
						fn.add(vndata);
						
						int[] vdataN = new int[3];
						int[] vtdataN = new int[3];
						int[] vndataN = new int[3];

						vdataN[0] = vdata[0]; //first vertex always at index 0
						vtdataN[0] = vtdata[0];
						vndataN[0] = vndata[0];
						
						vdataN[1] = vdata[2]; //second vertex is the third one of previous triangle 
						vtdataN[1] = vtdata[2];
						vndataN[1] = vndata[2];
						index = 2;
						
						vdata = vdataN; 
						vtdata = vtdataN;
						vndata = vndataN;
					}
					
					vdata[index] = Integer.valueOf(temp[0]); 
					// always add vertex indices

					if (temp.length > 1) {// if true, we have v and vt data
						vtdata[index] = Integer.valueOf(temp[1]); 
						// add in vt indices
					} else {
						vtdata[index] = 0; // if no vt data is present fill in zeros
					}
					if (temp.length > 2) {// if true, we have v, vt, and vn data
						vndata[index] = Integer.valueOf(temp[2]); 
						// add in vn indices
					} else {
						vndata[index] = 0;// if no vn data is present fill in zeros
					}
				}
				fv.add(vdata);
				ft.add(vtdata);
				fn.add(vndata);
			}

			private void setFaceRenderType() {
				topology = GL_TRIANGLES; 
				/*final int temp[] = (int[]) fv.get(0);

				if (temp.length == 3) {
					topology = GL_TRIANGLES; 
					// The faces come in sets of 3 so we have triangular faces
				} else if (temp.length == 4) {
					topology = GL_QUADS; 
					// The faces come in sets of 4 so we have quadrilateral faces
				} else {
					topology = GL_POLYGON; 
					// Fall back to render as free form polygons
				}*/
			}

		}
		
		verticesBuffer = null;
		normalsBuffer =null;
		texCoordsBuffer = null;
		
		OBJLoader loader = new OBJLoader(modelPath); 
		
		float[] coords4 = new float[4];
	System.out.println(loader.fv.size() + " " + (loader.fv.get(0)).length);
		if (loader.fv.get(0)[0] > 0) {
			verticesBuffer = ByteBuffer.allocateDirect(loader.fv.size() * 4
					* (loader.fv.get(0)).length * 4)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
					
			verticesBuffer.position(0);

			coords4[3] = 1;
			for (int i = 0; i < loader.fv.size(); i++) {
				for (int j = 0; j < loader.fv.get(i).length; j++) {
					coords4[0] = loader.vData.get(loader.fv.get(i)[j] - 1)[0]; // x
					coords4[1] = loader.vData.get(loader.fv.get(i)[j] - 1)[1]; // y
					coords4[2] = loader.vData.get(loader.fv.get(i)[j] - 1)[2]; // z
					verticesBuffer.put(coords4);
				}

			}
			verticesBuffer.position(0);
		}

		if (loader.ft.get(0)[0] > 0) {
			texCoordsBuffer = ByteBuffer.allocateDirect(loader.ft.size() * 2
					* (loader.ft.get(0)).length * 4)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			texCoordsBuffer.position(0);

			for (int i = 0; i < loader.ft.size(); i++) {
				try {
					
		//			for (int j = 0; j < 3; j++) {
					for (int j = 0; j < loader.ft.get(i).length; j++) {
								texCoordsBuffer
							.put(loader.vtData.get(loader.ft.get(i)[j] - 1)[0]);
					texCoordsBuffer
							.put(loader.vtData.get(loader.ft.get(i)[j] - 1)[1]);
				}
				} catch (ArrayIndexOutOfBoundsException exception) {
					System.out.println(i + " " + loader.ft.get(i).length);
					System.out.println(loader.ft.get(i));
					System.out.println(loader.ft.get(i)[0] + " " + loader.ft.get(i)[1] + " "+loader.ft.get(i)[2]);
					System.out.println(loader.vtData.get(loader.ft.get(i)[0] - 1));
					System.out.println(loader.vtData.get(loader.ft.get(i)[1] - 1));
					System.out.println(loader.vtData.get(loader.ft.get(i)[2] - 1));
					exception.printStackTrace();
					return;
				}
				
			}
			texCoordsBuffer.position(0);
		}

		float[] coords3 = new float[3];
		if (loader.fn.get(0)[0] > 0) {
			normalsBuffer = ByteBuffer.allocateDirect(loader.fn.size() * 3
					* (loader.fn.get(0)).length * 4)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			
			normalsBuffer.position(0);

			for (int i = 0; i < loader.fn.size(); i++) {
				for (int j = 0; j < loader.fn.get(i).length; j++) {
					coords3[0] = loader.vnData.get(loader.fn.get(i)[j] - 1)[0]; // x
					coords3[1] = loader.vnData.get(loader.fn.get(i)[j] - 1)[1]; // y
					coords3[2] = loader.vnData.get(loader.fn.get(i)[j] - 1)[2]; // z
					normalsBuffer.put(coords3);
				}
			}
			normalsBuffer.position(0);
		}
		
		buffer = toOGLBuffers(verticesBuffer, normalsBuffer, texCoordsBuffer);
	}

	
	private OGLBuffers toOGLBuffers(FloatBuffer verticesBuf, FloatBuffer normalsBuf, FloatBuffer texCoordsBuf){
		OGLBuffers buffers;
		
		if (verticesBuf != null) {
			OGLBuffers.Attrib[] attributesPos = {
					new OGLBuffers.Attrib("inPosition", 4),
			};
			float[] floatArray = new float[verticesBuf.limit()];
			verticesBuf.get(floatArray);
	        buffers = new OGLBuffers(floatArray, attributesPos, null);
		}
		else
			return null;

		if (texCoordsBuf != null) {
			OGLBuffers.Attrib[] attributesTexCoord = {
					new OGLBuffers.Attrib("inTexCoord", 2)
			};
			float[] floatArray = new float[texCoordsBuf.limit()];
			texCoordsBuf.get(floatArray);
			buffers.addVertexBuffer(floatArray, attributesTexCoord);
		}
			
		if (normalsBuf != null) {
			OGLBuffers.Attrib[] attributesNormal = {
					new OGLBuffers.Attrib("inNormal", 3)
			};
			float[] floatArray = new float[normalsBuf.limit()];
			normalsBuf.get(floatArray);
			buffers.addVertexBuffer(floatArray, attributesNormal);
		}
			
		return buffers;
	}

}