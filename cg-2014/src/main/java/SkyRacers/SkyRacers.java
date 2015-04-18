package SkyRacers;

import SkyRacers.Circuits.Island;
import SkyRacers.core.GameObject;
import SkyRacers.core.InputHandler;
import SkyRacers.core.Map;
import SkyRacers.core.MeshHandler;
import SkyRacers.core.Vector3;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import br.usp.icmc.vicg.gl.util.ShaderFactory.ShaderType;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkyRacers implements GLEventListener {

    private final Shader shader; // Gerenciador dos shaders

    private Map gameMap;

    public static Matrix4 modelMatrix = new Matrix4();
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private float aspect;

    private static GLCanvas glCanvas;
    
    public static InputHandler inputHandler;

    public SkyRacers() {
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
        // modelMatrix = new Matrix4();
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();

    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        // Get pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Print OpenGL version
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        //gl.glEnable(GL.GL_BLEND);
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        //inicializa os shaders
        shader.init(gl);

        //ativa os shaders
        shader.bind();
        
        //inicializa a matrix Model and Projection
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
        viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));
        
        try{
            MeshHandler mh = new MeshHandler(gl, shader);
        } catch (Exception ex) {
            Logger.getLogger(SkyRacers.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        gameMap = new Island(gl, shader);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        update();
        render(drawable);
    }
    
    private void update()
    {
        gameMap.update();
    }
    
    private void render(GLAutoDrawable drawable)
    {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(60, aspect, 0.1f, 100);
        projectionMatrix.bind();

        viewMatrix.loadIdentity();
        viewMatrix.lookAt(
                0, 4, 10,
                -5, 0, -5,
                0, 1, 0);
        viewMatrix.bind();

        gameMap.draw();

        // Execute
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        aspect = width / height;
        drawable.getGL().getGL3().glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        gameMap.dispose();
    }

    public static void main(String[] args) {
        // Get GL3 profile (to work with OpenGL 4.0)
        GLProfile profile = GLProfile.get(GLProfile.GL3);

        // Configurations
        GLCapabilities glcaps = new GLCapabilities(profile);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        // Create canvas
        glCanvas = new GLCanvas(glcaps);

        // Add listener to panel
        SkyRacers listener = new SkyRacers();
        glCanvas.addGLEventListener(listener);
        
        inputHandler = new InputHandler();

        Frame frame = new Frame("Sky Racers");
        frame.setSize(800, 600);
        frame.add(glCanvas);
        frame.addKeyListener(inputHandler);
        frame.setFocusable(true);
        //KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(inputHandler);
        final AnimatorBase animator = new FPSAnimator(glCanvas, 60);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        frame.setVisible(true);
        animator.start();
    }
}
