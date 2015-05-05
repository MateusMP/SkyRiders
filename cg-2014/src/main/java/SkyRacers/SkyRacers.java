package SkyRacers;

import MathClasses.Plane;
import MathClasses.Vector3;
import SkyRacers.Circuits.Island;
import SkyRacers.core.Camera;
import SkyRacers.core.FrustumCulling;
import SkyRacers.core.InputHandler;
import SkyRacers.core.Map;
import SkyRacers.core.MapLoader;
import SkyRacers.core.MeshHandler;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import br.usp.icmc.vicg.gl.util.ShaderFactory.ShaderType;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

public class SkyRacers implements GLEventListener {
    
    private static SkyRacers skyracers;

    public GL3 gl;
    public final Shader shader; // Gerenciador dos shaders
    
    private Map gameMap;
    private Camera currentCamera;
    private FrustumCulling frusCull;
    
    public static Matrix4 modelMatrix = new Matrix4();
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private final float angle = 60;
    private float aspect;
    private final float nearDistance = 0.1f;
    private final float farDistance = 1000;

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
        this.shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
        // modelMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.viewMatrix = new Matrix4();
        this.frusCull = new FrustumCulling();

    }
    
    public static SkyRacers hdl()
    {
        return skyracers;
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        // Get pipeline
        this.gl = drawable.getGL().getGL3();

        // Print OpenGL version
        System.out.println("OpenGL Version: " + this.gl.glGetString(GL.GL_VERSION) + "\n");

        this.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.gl.glClearDepth(1.0f);

        this.gl.glEnable(GL.GL_DEPTH_TEST);
        this.gl.glEnable(GL.GL_CULL_FACE);
        //gl.glEnable(GL.GL_BLEND);
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        //inicializa os shaders
        this.shader.init(this.gl);

        //ativa os shaders
        this.shader.bind();
        
        //inicializa a matrix Model and Projection
        modelMatrix.init(this.gl, this.shader.getUniformLocation("u_modelMatrix"));
        this.projectionMatrix.init(this.gl, this.shader.getUniformLocation("u_projectionMatrix"));
        this.viewMatrix.init(this.gl, this.shader.getUniformLocation("u_viewMatrix"));
        
        try{
            MeshHandler mh = new MeshHandler(this.gl, this.shader);
        } catch (Exception ex) {
            Logger.getLogger(SkyRacers.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            // gameMap = new Island(gl, shader);
            this.gameMap = MapLoader.LoadMap("island.txt");
            this.gameMap.setFrusCull(this.frusCull);
        } catch (Exception ex) {
            Logger.getLogger(SkyRacers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCurrentCamera(Camera cam)
    {
        this.currentCamera = cam;
    }
    
    public Camera getCurrentCamera()
    {
        return this.currentCamera;
    }

    public FrustumCulling getFrusCull() {
        return this.frusCull;
    }

    public void setFrusCull(FrustumCulling frusCull) {
        this.frusCull = frusCull;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        
        ResetVertexCount();
        
        update();
        render(drawable);
        
        this.frame.setTitle("Sky Racers "+animator.getLastFPS()+" - OBJS: "+renderingObjects+" - Vertices: "+renderingVertex);
    }
    
    private void update()
    {
        this.gameMap.update();
    }
    
    private void render(GLAutoDrawable drawable)
    {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();
        ArrayList<Plane> planes = new ArrayList<>();
        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.9f, 0.9f, 1, 1);
        
        this.currentCamera.DefineProjectionMatrix(this.projectionMatrix, this.angle, this.aspect, this.nearDistance, this.farDistance);
        
        this.currentCamera.DefineViewMatrix(viewMatrix);
        
        this.frusCull.setCamInternals(this.currentCamera.getAngle(), this.currentCamera.getAspect(), this.currentCamera.getNearDistance(), this.currentCamera.getFarDistance());
        this.frusCull.setCamDef(this.currentCamera.GetPosition(), this.currentCamera.getLookat(), this.currentCamera.getUp());

        this.gameMap.draw();

        // Execute
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.aspect = width / height;
        drawable.getGL().getGL3().glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        this.gameMap.dispose();
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
