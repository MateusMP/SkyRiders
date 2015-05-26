/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders.core;

import static SkyRiders.SkyRiders.gl;
import com.jogamp.common.nio.Buffers;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

public class ModelBuilder {
    
    /**
     * Creates and bind a new VAO
     * @return 
     */
    public static int CreateVAO(){
        int vaos[] = new int[1];
        gl.glGenVertexArrays(1, vaos, 0);
        gl.glBindVertexArray(vaos[0]);
        
        return vaos[0];
    }
    
    /**
     * Stores a float data vector with componets of X.
     * [f1,f2,f3][f4,f5,f6]
     * 
     * @param attrid
     * @param data
     * @return 
     */
    public static int StoreDataInAttributeListfv(int attrid, int X, float[] data)
    {
        int[] vboID = new int[1];
        int vbo;
        gl.glGenBuffers(1, vboID, 0);
        vbo = vboID[0];
        
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, data.length * Buffers.SIZEOF_FLOAT, Buffers.newDirectFloatBuffer(data), GL3.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(attrid);
        gl.glVertexAttribPointer(attrid, X, GL3.GL_FLOAT, false, 0, 0 );
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);    // unbind
        
        return vbo;
    }
    
    /**
     * Store an int element data vector with components of 2.
     * @param attrid
     * @param data
     * @return 
     */
    public static int StoreElementInAttributeList2iv(int attrid, int[] data)
    {
        int[] vboID = new int[1];
        int vbo;
        gl.glGenBuffers(1, vboID, 0);
        vbo = vboID[0];
        
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vbo); // Bind normals buffer
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, data.length * Buffers.SIZEOF_INT, Buffers.newDirectIntBuffer(data), GL3.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(attrid);
        gl.glVertexAttribPointer(attrid, 2, GL3.GL_INT, false, 0, 0);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        
        return vbo;
    }
}