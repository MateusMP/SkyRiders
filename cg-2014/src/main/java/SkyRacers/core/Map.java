package SkyRacers.core;

import MathClasses.Transform;
import SkyRacers.SkyRacers;
import br.usp.icmc.vicg.gl.model.Sphere;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import static javax.media.opengl.GLProfile.GL3;

public class Map {
    
    protected ArrayList<GameObject> objects;
    protected ArrayList<GameObject> checkpoints;
    protected Frustum frusCull;
    
    public Transform startpoint = new Transform();
    
    public Map()
    {
        this.objects = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.frusCull = null;
    }
    
    public Map(Frustum fc)
    {
        this.objects = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.frusCull = fc;
    }
    
    public void addObject(GameObject obj)
    {
        this.objects.add(obj);
    }
    
    public void update()
    {
        for (GameObject go : this.objects){
            go.update();
        }
    }

    public void draw()
    {
        ArrayList<GameObject> postponed = new ArrayList<>();
        
        SkyRacers.hdl().gl.glEnable(GL2.GL_CULL_FACE);
        
        // SOLIDS
        for (GameObject go : this.objects)
        {
            if ( frusCull.sphereIntersects(go.getTransform().position.x, go.getTransform().position.y, go.getTransform().position.z, go.getObjectRadius()) != Frustum.Result.Miss )
            {
                if ( go.name.contains("transparent") )
                {
                    postponed.add(go);
                }
                else
                {
                    go.draw();
                }
            }
        }

        // TRANSPARENTS
        SkyRacers.hdl().gl.glDisable(GL2.GL_CULL_FACE);
        for (GameObject go : postponed)
        {
            // DEBUG
//            float radius = go.getObjectRadius();
//            Transform t = go.getTransform();
//            Sphere sp = new Sphere(radius);
//            sp.init(SkyRacers.hdl().gl, SkyRacers.hdl().shader);
//            sp.bind();
//            SkyRacers.modelMatrix.loadIdentity();
//            SkyRacers.modelMatrix.translate(t.position.x, t.position.y, t.position.z);
//            sp.draw();
            //
            
            go.draw();
        }
    }
    
    public void dispose()
    {
        objects.clear();
        checkpoints.clear();
    }

    public void setFrusCull(Frustum frusCull) {
        this.frusCull = frusCull;
    }
    
}
