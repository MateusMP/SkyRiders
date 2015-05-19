package SkyRiders;

import SkyRiders.core.Camera;
import SkyRiders.core.GameRenderer;
import SkyRiders.core.InputHandler;
import SkyRiders.core.Map;
import SkyRiders.core.MapLoader;
import SkyRiders.core.MeshHandler;
import SkyRiders.core.ObjMesh;
import SkyRiders.core.ShaderHandler;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

public class SkyRiders implements GLEventListener {
    
    private static SkyRiders skyracers;

    public GL3 gl;
    
    private Map gameMap;
    private Camera currentCamera;
    
    // public static Matrix4 modelMatrix = new Matrix4();
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
    

    public SkyRiders() {
        
        skyracers = this;
        
        // modelMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.viewMatrix = new Matrix4();

    }
    
    public static SkyRiders hdl()
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

        this.gl.glEnable(GL.GL_DEPTH_TEST);
        // this.gl.glEnable(GL.GL_CULL_FACE);
        // this.gl.glCullFace(GL.GL_BACK);
        
        //inicializa os shaders
        ShaderHandler.Init(this.gl);

        //ativa os shaders
        ShaderHandler.generalShader.bind();
        
        //inicializa a matrix Model and Projection
        //SkyRacers.modelMatrix.init(this.gl, ShaderHandler.generalShader.getUniformLocation("u_modelMatrix"));
        //this.projectionMatrix.init(this.gl, ShaderHandler.generalShader.getUniformLocation("u_projectionMatrix"));
        //this.viewMatrix.init(this.gl, ShaderHandler.generalShader.getUniformLocation("u_viewMatrix"));
        
        System.out.println("SHADER view handle: " + ShaderHandler.generalShader.viewMatrix_hdl);
        System.out.println("MATRIX view handle: " + viewMatrix.handle);
        
        try{
            MeshHandler mh = new MeshHandler(this.gl, ShaderHandler.generalShader);
        } catch (Exception ex) {
            Logger.getLogger(SkyRiders.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            gameMap = MapLoader.LoadMap("island.txt");
            
            // Create player airplane and define a controller for it
            ObjMesh om = new ObjMesh(MeshHandler.hdl().LoadMesh("./Assets/graphics/cartoonAriplaneNoPropeller.obj"), null);
            om.setShader(ShaderHandler.generalShader);
            Airplane plane = new Airplane(gameMap.startpoint, om);
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
            Logger.getLogger(SkyRiders.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public void display(GLAutoDrawable drawable) {
        
        Profiler.ResetVertexCount();
        
        update();
        render(drawable);
        
        this.frame.setTitle("Sky Racers "+animator.getLastFPS()+" - OBJS: "+Profiler.getRenderingObjects()+" - Vertices: "+Profiler.getRenderingVertex());
    }
    
    private void update()
    {
        this.gameMap.update();
    }
    
    private void render(GLAutoDrawable drawable)
    {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();
        
        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.9f, 0.9f, 1, 1);
        
        // Projection Matrix
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(angle, aspect, nearDistance, farDistance);
        //projectionMatrix.bind();

        // View Matrix
        currentCamera.DefineViewMatrix(viewMatrix);

        ShaderHandler.generalShader.LoadProjView(projectionMatrix, viewMatrix);
        GameRenderer.SetFrustum(projectionMatrix, viewMatrix);
        GameRenderer.Render(gameMap);
        
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
        glcaps.setDepthBits(20);

        // Create canvas
        glCanvas = new GLCanvas(glcaps);

        // Add listener to panel
        SkyRiders listener = new SkyRiders();
        glCanvas.addGLEventListener(listener);
        
        inputHandler = new InputHandler();

        frame = new Frame("Sky Racers");
        frame.setSize(800, 600);
        frame.add(glCanvas);
        frame.addKeyListener(inputHandler);
        frame.setFocusable(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(inputHandler);
        animator = new FPSAnimator(glCanvas, 60);
        animator.setUpdateFPSFrames(3, null);
        
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
