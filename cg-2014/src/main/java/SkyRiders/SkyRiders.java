package SkyRiders;

import SkyRiders.core.Camera;
import SkyRiders.core.GameRenderer;
import Handlers.InputHandler;
import SkyRiders.core.Map;
import SkyRiders.core.MapLoader;
import Handlers.MeshHandler;
import SkyRiders.core.SkydomeMesh;
import Handlers.ShaderHandler;
import MathClasses.Vector3;
import SkyRiders.core.GameObject;
import SkyRiders.core.GameRenderer.RENDER_TYPE;
import Renderers.TexturedMesh;
import SkyRiders.core.WaterObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Frame;
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
    
    public static SkyRiders skyriders;
    
    public static GL3 gl;
    
    private Map gameMap;
    private Camera currentCamera;
    
    // public static Matrix4 modelMatrix = new Matrix4();
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private final float angle = 60;
    private float aspect;
    private static final float nearDistance = 0.1f;
    private static final float farDistance = 10000;

    private static AnimatorBase animator;
    private static Frame frame;
    private static GLCanvas glCanvas;
    public static InputHandler inputHandler;
    
    TexturedMesh waterPlane; 
    GameObject waterObj;
    
    public SkyRiders() {
        this.projectionMatrix = new Matrix4();
        this.viewMatrix = new Matrix4();
    }
    
    @Override
    public void init(GLAutoDrawable drawable)
    {
        // Get pipeline
        gl = drawable.getGL().getGL3();

        // Print OpenGL version
        System.out.println("OpenGL Version: " + this.gl.glGetString(GL.GL_VERSION) + "\n");

        //inicializa os shaders
        ShaderHandler.Init();

        //ativa os shaders
        ShaderHandler.generalShader.bind();
        
        try{
            MeshHandler mh = new MeshHandler(this.gl);
        } catch (Exception ex) {
            Logger.getLogger(SkyRiders.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            gameMap = MapLoader.LoadMap("island.txt");
            
            // Create player airplane and define a controller for it
            TexturedMesh om = new TexturedMesh(MeshHandler.LoadMesh("./Assets/graphics/cartoonAriplaneNoPropeller.obj", "Airplane", ShaderHandler.generalShader), ShaderHandler.generalShader);
            Airplane plane = new Airplane(gameMap.startpoint, om);
            gameMap.addObject(plane);
            AirplaneController controller = new AirplaneController(plane);
            inputHandler.AddHandler(controller);

            // Set camera fo the player airplane
            Camera cam = new AirplaneCamera(plane);
            setCurrentCamera(cam);
        
            // SkyRacers.inputHandler.RemoveHandler(controller);
            
            // STANDARD CAMERA            
            StandardCamera stdcam = new StandardCamera(gameMap.startpoint.position);
            inputHandler.AddHandler(stdcam);
            //setCurrentCamera(stdcam);
            
            // SKY DOME
            SkydomeMesh objMesh = new SkydomeMesh(MeshHandler.LoadMesh("./Assets/graphics/skydome.obj", null, ShaderHandler.skyDomeShader));
            SkyDome skyDome = new SkyDome(cam, objMesh);
            GameRenderer.setSkyDome(skyDome);
            
            //Water Plane
//            waterPlane = new TexturedMesh(MeshHandler.LoadMesh("./Assets/graphics/water_plane.obj", null , ShaderHandler.waterShader), ShaderHandler.waterShader);
//            for (int i = 0; i < 15; i++){
//                for (int j = 0; j < 15; j++){
//                    waterObj = new WaterObject(new Vector3(-5000+i*400f, -30f, -1000+j*400f), waterPlane);
//
//                    waterObj.setRenderType(RENDER_TYPE.RENDER_WATER);
//                    waterObj.getTransform().scale = waterObj.getTransform().scale.mul(200);
//                    gameMap.addObject(waterObj);
//                }
//            }
            
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
        
        String str = String.format("Sky Riders %06.2f - OBJs: %03d - Vertices %,07d",
                animator.getLastFPS(), Profiler.getRenderingObjects(), Profiler.getRenderingVertex());
        SkyRiders.frame.setTitle(str);
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
        gl.glClearColor(0.49f,0.78f, 0.96f, 1);
        
        // Projection Matrix
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(angle, aspect, nearDistance, farDistance);

        // View Matrix
        currentCamera.DefineViewMatrix(viewMatrix);

        ShaderHandler.generalShader.LoadProjView(projectionMatrix, viewMatrix);
        ShaderHandler.foliageShader.LoadProjView(projectionMatrix, viewMatrix);
        ShaderHandler.waterShader.LoadProjView(projectionMatrix, viewMatrix);
        
        GameRenderer.SetFrustum(getCurrentCamera(), projectionMatrix, viewMatrix);
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
        inputHandler = new InputHandler();
        
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
        skyriders = listener;
        glCanvas.addGLEventListener(listener);
        glCanvas.addKeyListener(inputHandler);

        frame = new Frame("Sky Racers");
        frame.setSize(1024, 576);
        frame.add(glCanvas);
        frame.addKeyListener(inputHandler);
        frame.setFocusable(true);
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
