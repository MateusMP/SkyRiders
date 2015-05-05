package SkyRacers;

import SkyRacers.Circuits.Island;
import SkyRacers.core.Camera;
import SkyRacers.core.InputHandler;
import SkyRacers.core.Map;
import SkyRacers.core.MapLoader;
import SkyRacers.core.MeshHandler;
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
import java.awt.KeyboardFocusManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkyRacers implements GLEventListener {
    
    private static SkyRacers skyracers;

    public GL3 gl;
    public final Shader shader; // Gerenciador dos shaders
    
    private Map gameMap;
    private Camera currentCamera;
    
    public static Matrix4 modelMatrix = new Matrix4();
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private float aspect;

    private static AnimatorBase animator;
    private static Frame frame;
    private static GLCanvas glCanvas;
    public static InputHandler inputHandler;
    
    // ---- PERFORMANCE STATISTICS ----
    private static int renderingObjects;
    private static int renderingVertex;
    public static void AddRenderingObject(JWavefrontObject mesh)
    {
        renderingObjects++;
        renderingVertex += mesh.getVertices().size();
    }
    private static void ResetVertexCount()
    {
        renderingObjects = 0;
        renderingVertex = 0;
    }
    // ---------------------------------
    

    public SkyRacers() {
        
        skyracers = this;
        
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
        // modelMatrix = new Matrix4();
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();

    }
    
    public static SkyRacers hdl()
    {
        return skyracers;
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        // Get pipeline
        gl = drawable.getGL().getGL3();

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
        
        try {
            // gameMap = new Island(gl, shader);
            gameMap = MapLoader.LoadMap("island.txt");
            
            // Create player airplane and define a controller for it
            Airplane plane = new Airplane(gameMap.startpoint, MeshHandler.hdl().LoadMesh("./Assets/graphics/cartoonAriplane.obj"));
            gameMap.addObject(plane);
            AirplaneController controller = new AirplaneController(plane);
            inputHandler.AddHandler(controller);

            // Set camera fo the player airplane
            Camera cam = new AirplaneCamera(plane);
            setCurrentCamera(cam);   
        
            // SkyRacers.inputHandler.RemoveHandler(controller);
            
            // STANDARD CAMERA
            
            /*StandardCamera stdcam = new StandardCamera(gameMap.startpoint.position);
            inputHandler.AddHandler(stdcam);
            setCurrentCamera(stdcam);*/
            
            
        } catch (Exception ex) {
            Logger.getLogger(SkyRacers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCurrentCamera(Camera cam)
    {
        currentCamera = cam;
    }
    
    public Camera getCurrentCamera()
    {
        return currentCamera;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        
        ResetVertexCount();
        
        update();
        render(drawable);
        
        frame.setTitle("Sky Racers "+animator.getLastFPS()+" - OBJS: "+renderingObjects+" - Vertices: "+renderingVertex);
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
        gl.glClearColor(0.9f, 0.9f, 1, 1);

        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(60, aspect, 0.1f, 1000);
        projectionMatrix.bind();
        
        currentCamera.DefineViewMatrix(viewMatrix);

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

        frame = new Frame("Sky Racers");
        frame.setSize(800, 600);
        frame.add(glCanvas);
        frame.addKeyListener(inputHandler);
        frame.setFocusable(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(inputHandler);
        animator = new FPSAnimator(glCanvas, 60);
        

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
