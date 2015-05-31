package br.usp.icmc.vicg.gl.util;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;

import com.jogamp.common.nio.Buffers;

public abstract class Shader {
    
    private String[] shaders_filenames = new String[5];
    protected GL3 gl = null;
    private int programHandle = -1;
    
    public static final int[] TYPES = new int[]{
        GL3.GL_VERTEX_SHADER,
        GL3.GL_FRAGMENT_SHADER,
        GL3.GL_GEOMETRY_SHADER,
        GL3.GL_TESS_CONTROL_SHADER,
        GL3.GL_TESS_EVALUATION_SHADER
    };
    
    public Shader(String vertex, String fragment) {
        this(vertex, fragment, null, null, null);
    }
    
    public Shader(String vertex, String fragment, String geometry,
            String tessControl, String tessEval) {
        this.shaders_filenames[0] = vertex;
        this.shaders_filenames[1] = fragment;
        this.shaders_filenames[2] = geometry;
        this.shaders_filenames[3] = tessControl;
        this.shaders_filenames[4] = tessEval;
    }
    
    /**
     * Should be used to catch all uniforms/attributes needed
     */
    protected abstract void RegisterAllUniformLocations();
    
    /**
     * Load all global shader information
     * This will be called once PER layer, before processing the pack of game objects that use this shader.
     * Should load data in common used by all objects like Projection/View Matrices, prepare texture handles
     */
    public abstract void fullBind();
    
    public void init(final GL3 gl) {
        this.gl = gl;
        
        String[] sources = readSources();
        
        int[] shadersHandles = new int[sources.length];
        
        for (int i = 0; i < sources.length; i++) {
            shadersHandles[i] = (sources[i] != null) ? compileSource(sources[i], Shader.TYPES[i]) : -1;
        }
        
        programHandle = linkShaders(shadersHandles);
        RegisterAllUniformLocations();
        
        for (int i = 0; i < shadersHandles.length; i++) {
            if (shadersHandles[i] >= 0) {
                gl.glDeleteShader(shadersHandles[i]);
            }
        }
    }
    
    /**
     * Activates the shader
     */
    public void bind() {
        gl.glUseProgram(programHandle);
    }
    
    /**
     * Deactivate the shader
     */
    public void unbind() {
        gl.glUseProgram(0);
    }
    
    public void dispose() {
        gl.glDeleteProgram(programHandle);
    }
    
    public int getUniformLocation(String varName) {
        int location = gl.glGetUniformLocation(programHandle, varName);
        if (location < 0) {
            System.err.println(varName + " uniform not found.");
            return -1;
        } else {
//      System.out.println(varName + " uniform found.");
            return location;
        }
    }
    
    public int getAttribLocation(String varName) {
        int location = gl.glGetAttribLocation(programHandle, varName);
        if (location < 0) {
            System.err.println(varName + " attribute not found");
            return -1;
        } else {
//      System.out.println(varName + " attribute found");
            return location;
        }
    }
    
    protected void loadInt(int location, int value){
        gl.glUniform1i(location, value);
    }
    
    protected void loadFloat(int location, float value){
        gl.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3 vector){
        gl.glUniform3f(location, vector.x, vector.y, vector.z);
    }
    
    protected void loadVector4f(int location, float[] vector){
        gl.glUniform4fv(location, 1, Buffers.newDirectFloatBuffer(vector));
    }

    protected void loadBoolean(int location, boolean b){
        float toload = 0.0f;
        if (b)
                toload = 1.0f;
        gl.glUniform1f(location, toload);
    }
    
    protected void loadMatrix(int location, Matrix4 matrix){
        gl.glUniformMatrix4fv(location, 1, false, matrix.getMatrix(), 0);
    }
    
    /**
     * @param textureID Should be one of GL_TEXTURE0 ... GL_TEXTURE31
     * @param tex Texture object to be bound on the specified unit
     */
    protected void loadTexture(int textureID, Texture tex){
        gl.glActiveTexture(textureID);
        tex.texturedata.bind(gl);
    }
    
    private String[] readSources() {
        String[] sources = new String[Shader.TYPES.length];
        
        for (int i = 0; i < Shader.TYPES.length; i++) {
            try {
                if (shaders_filenames[i] != null) {
                    InputStream source = getClass().getClassLoader().getResourceAsStream(shaders_filenames[i]);
                    sources[i] = readSource(source);
                }
            } catch (Exception e) {
                System.err.println("\t" + shaders_filenames[i] + " shader not found.");
            }
            
            System.out.println("\t" + shaders_filenames[i] + " shader found.");
        }
        return sources;
    }
    
    private String readSource(final InputStream inputStream) {
        final StringBuilder source = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                source.append(line).append("\n");
            }
            
            bufferedReader.close();
        } catch (final IOException e) {
            System.err.println("Invalid shader file.");
        }
        return source.toString();
    }
    
    private int compileSource(final String source, final int type) {
        final IntBuffer buffer = Buffers.newDirectIntBuffer(1);
        final int handle_aux = gl.glCreateShader(type);
        
        gl.glShaderSource(handle_aux, 1, new String[]{source}, null, 0);
        gl.glCompileShader(handle_aux);
        
        gl.glGetShaderiv(handle_aux, GL3.GL_COMPILE_STATUS, buffer);
        
        if (buffer.get(0) == 1) {
            return handle_aux;
        } else {
            gl.glGetShaderiv(handle_aux, GL3.GL_INFO_LOG_LENGTH, buffer);
            
            final ByteBuffer byteBuffer = Buffers.newDirectByteBuffer(buffer
                    .get(0));
            gl.glGetShaderInfoLog(handle_aux, byteBuffer.capacity(), buffer,
                    byteBuffer);
            
            System.err.println("\nshader compile error: ");
            for (int i = 0; i < buffer.get(0); i++) {
                System.err.print((char) byteBuffer.get(i));
            }
            
            return -1;
        }
    }
    
    private int linkShaders(int[] shadersHandles) {
        final int progHandle = gl.glCreateProgram();
        
        for (int i = 0; i < shadersHandles.length; i++) {
            if (shadersHandles[i] >= 0) {
                gl.glAttachShader(progHandle, shadersHandles[i]);
            }
        }
        
        gl.glLinkProgram(progHandle);
        gl.glValidateProgram(progHandle);
        
        final IntBuffer buffer = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(progHandle, GL3.GL_VALIDATE_STATUS, buffer);
        
        if (buffer.get(0) == 1) {
            return progHandle;
        } else {
            
            gl.glGetProgramiv(progHandle, GL3.GL_INFO_LOG_LENGTH, buffer);
            
            final ByteBuffer byteBuffer = Buffers.newDirectByteBuffer(buffer
                    .get(0));
            gl.glGetProgramInfoLog(progHandle, byteBuffer.capacity(), buffer,
                    byteBuffer);
            
            System.err.println("\nshader link error: ");
            for (int i = 0; i < buffer.get(0); i++) {
                System.err.print((char) byteBuffer.get(i));
            }
            
            return -1;
        }
    }
}
