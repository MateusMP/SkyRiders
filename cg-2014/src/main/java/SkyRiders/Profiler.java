/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders;

import SkyRiders.core.MeshRenderer;

public class Profiler {
    private static int renderingObjects;
    private static int renderingVertex;

    public static int getRenderingObjects() {
        return renderingObjects;
    }

    public static int getRenderingVertex() {
        return renderingVertex;
    }
    
    public static void AddRenderingObject(MeshRenderer mesh)
    {
        renderingObjects++;
        renderingVertex += mesh.getVertices().size();
    }
    
    public static void ResetVertexCount()
    {
        renderingObjects = 0;
        renderingVertex = 0;
    }
}
