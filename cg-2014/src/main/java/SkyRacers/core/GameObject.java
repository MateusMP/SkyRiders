package SkyRacers.core;

import MathClasses.Transform;
import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import java.util.ArrayList;

public class GameObject {
    
    protected Transform transform;
    protected LODMesh mesh;
    protected float objectRadius;

    
    public GameObject(Transform t, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = t;
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale.set(1,1,1);
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, Vector3 scale, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale = scale;
        
        calculateObjectRadius();
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        modelMatrix.loadIdentity();
        modelMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        modelMatrix.rotate(transform.rotation.x, 1.0f, 0, 0);
        modelMatrix.rotate(transform.rotation.y, 0, 1.0f, 0);
        modelMatrix.rotate(transform.rotation.z, 0, 0, 1.0f);
        modelMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        modelMatrix.bind();
        
        mesh.ActiveMeshDraw();
    }
    
    private void calculateObjectRadius() 
    {
        ArrayList<Vertex> vertices = mesh.getActiveMesh().getVertices();
        
        float maxx, minx, maxy, miny, maxz, minz;

        /*
         * get the max/mins
         */
        maxx = minx = vertices.get(1).x;
        maxy = miny = vertices.get(1).y;
        maxz = minz = vertices.get(1).z;

        for (int i = 1; i <= vertices.size(); i++) {
            if (maxx < vertices.get(i).x) {
              maxx = vertices.get(i).x;
            }
            if (minx > vertices.get(i).x) {
              minx = vertices.get(i).x;
            }

            if (maxy < vertices.get(i).y) {
              maxy = vertices.get(i).y;
            }
            if (miny > vertices.get(i).y) {
              miny = vertices.get(i).y;
            }

            if (maxz < vertices.get(i).z) {
              maxz = vertices.get(i).z;
            }
            if (minz > vertices.get(i).z) {
              minz = vertices.get(i).z;
            }
        }

        /*
         * calculate model width, height, and depth
         */
        float w = (maxx - minx)/2;
        float h = (maxy - miny)/2;
        float d = (maxz - minz)/2;
        
        Vector3 v = new Vector3(w*transform.scale.x, h*transform.scale.y, d*transform.scale.z);
        objectRadius = v.norm();
        System.out.println("RADIUS: "+objectRadius);
    }

    
    public Transform getTransform() {
        return transform;
    }
    
    public float getObjectRadius() {
        return objectRadius;
    }
    
}
