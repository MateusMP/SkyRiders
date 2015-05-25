/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.jwavefront;

import MathClasses.BoundingBox;
import MathClasses.Vector3;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class Group {

    public static Group default_group = new Group("_default_");

    public Group(String name) {
        this.name = name;
        this.material = Material.default_material;
        this.triangles = new ArrayList<Triangle>();
        this.centerGrav = new Vector3();
    }
    
    /**
     * Generates facet normals for a model (by taking the cross product of the
     * two vectors derived from the sides of each triangle). Assumes a
     * counter-clockwise winding.
     */
    public void calculate_face_normals() {
        float u[] = new float[3];
        float v[] = new float[3];
        
        centerGrav.x = 0;
        centerGrav.y = 0;
        centerGrav.z = 0;
        
        float max[] = {0,0,0};
        float min[] = {0,0,0};
        
        min[0] = max[0] = triangles.get(0).vertices[0].x;
        min[0] = max[0] = triangles.get(0).vertices[0].y;
        min[0] = max[0] = triangles.get(0).vertices[0].z;

        for (int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);
            Vertex v1 = triangle.vertices[0];
            Vertex v2 = triangle.vertices[1];
            Vertex v3 = triangle.vertices[2];
            
            if (v1.x < min[0]) min[0] = v1.x;
            if (v1.y < min[1]) min[1] = v1.y;
            if (v1.z < min[2]) min[2] = v1.z;
            if (v2.x < min[0]) min[0] = v2.x;
            if (v2.y < min[1]) min[1] = v2.y;
            if (v2.z < min[2]) min[2] = v2.z;
            if (v3.x < min[0]) min[0] = v3.x;
            if (v3.y < min[1]) min[1] = v3.y;
            if (v3.z < min[2]) min[2] = v3.z;
            
            if (v1.x > max[0]) max[0] = v1.x;
            if (v1.y > max[1]) max[1] = v1.y;
            if (v1.z > max[2]) max[2] = v1.z;
            if (v2.x > max[0]) max[0] = v2.x;
            if (v2.y > max[1]) max[1] = v2.y;
            if (v2.z > max[2]) max[2] = v2.z;
            if (v3.x > max[0]) max[0] = v3.x;
            if (v3.y > max[1]) max[1] = v3.y;
            if (v3.z > max[2]) max[2] = v3.z;
            
            centerGrav.x += v1.x + v2.x + v3.x;
            centerGrav.y += v1.y + v2.y + v3.y;
            centerGrav.z += v1.z + v2.z + v3.z;

            u[0] = v2.x - v1.x;
            u[1] = v2.y - v1.y;
            u[2] = v2.z - v1.z;

            v[0] = v3.x - v1.x;
            v[1] = v3.y - v1.y;
            v[2] = v3.z - v1.z;

            float[] n = VectorMath.cross(u, v);
            VectorMath.normalize(n);

            triangle.face_normal = new Normal(n[0], n[1], n[2]);
        }
        
        Vector3 maxV = new Vector3(max[0], max[1], max[2]);
        Vector3 minV = new Vector3(min[0], min[1], min[2]);
        bbox = new BoundingBox(minV, maxV);
        
        centerGrav = centerGrav.div(triangles.size()*3.0f);
    }
    
    public BoundingBox getBoundingBox(){
        return bbox;
    }

    public void dump() {
        System.out.println("Group name: " + name);
        System.out.println("Number triangles: " + triangles.size());
        material.dump();
   
    }
    
    public String name; // name of this group
    public ArrayList<Triangle> triangles; // array of triangle indices
    public Material material; // index to material for group
    public int vao;
    public int[] vbo;
    private Vector3 centerGrav;
    private BoundingBox bbox;
}
