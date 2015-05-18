package SkyRacers.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;

public class ObjMesh implements MeshRenderer {
    
    private Shader shader;
    private JWavefrontObject fullmesh;
    private Group g;
    private ArrayList<Vertex> vertices;
    
    public ObjMesh(JWavefrontObject obj, String group)
    {
        fullmesh = obj;
        g = obj.findGroup(group);
        
        copyVertices();
    }
    
    private void copyVertices()
    {
        if (g == null){
            vertices = fullmesh.getVertices();
            return;
        }
        
        vertices = new ArrayList<Vertex>() {
      @Override
      public Vertex get(int i) {
        return super.get(i - 1);
      }
    };
        for (Triangle t : g.triangles){
            vertices.add(t.vertices[0]);
            vertices.add(t.vertices[1]);
            vertices.add(t.vertices[2]);
        }
    }
    
    @Override
    public void draw()
    {
        if (g != null){
            fullmesh.drawGroup(g);
        } else {
            fullmesh.draw();
        }
    }

    @Override
    public ArrayList<Vertex> getVertices() {
        
        return vertices;
    }

    @Override
    public Vector3 getSizes() {
        float maxx, minx, maxy, miny, maxz, minz;
        
        ArrayList<Vertex> verts = vertices;

        System.out.println( "TAMANHO DO VETOR: "+verts.size() );
        
        /*
         * get the max/mins
         */
        maxx = minx = verts.get(1).x;
        maxy = miny = verts.get(1).y;
        maxz = minz = verts.get(1).z;

        for (int i = 1; i <= verts.size(); i++) {
            if (maxx < verts.get(i).x) {
              maxx = verts.get(i).x;
            }
            if (minx > verts.get(i).x) {
              minx = verts.get(i).x;
            }

            if (maxy < verts.get(i).y) {
              maxy = verts.get(i).y;
            }
            if (miny > verts.get(i).y) {
              miny = verts.get(i).y;
            }

            if (maxz < verts.get(i).z) {
              maxz = verts.get(i).z;
            }
            if (minz > verts.get(i).z) {
              minz = verts.get(i).z;
            }
        }

        /*
         * calculate model width, height, and depth
         */
        float w = (maxx - minx);
        float h = (maxy - miny);
        float d = (maxz - minz);
        
        return new Vector3(w, h, d);
    }

    @Override
    public int getVAO() {
        return g.vao;
    }

    @Override
    public Shader getShader() {
        return shader;
    }
    
    @Override
    public void setShader(Shader s){
        shader = s;
    }
    
    
}
