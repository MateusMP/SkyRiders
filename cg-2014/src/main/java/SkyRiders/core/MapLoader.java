package SkyRiders.core;

import Handlers.LODHandler;
import Renderers.TexturedMesh;
import Handlers.MeshHandler;
import Handlers.ShaderHandler;
import MathClasses.Transform;
import MathClasses.Vector3;
import SkyRiders.Circuits.Island;
import SkyRiders.core.GameRenderer.RENDER_TYPE;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;


public class MapLoader
{
    static TexturedMesh waterPlane;
    static TexturedMesh lavaPlane;
    
    protected static class VirtualData
    {
        public static enum DATA_TYPE{ 
            TRANSFORM,  // Transform CLASS
            MESHFILE    // String[2] -> Filepath, GroupName
        };
        
        public DATA_TYPE type;
        public Object data;
    }
    
    protected static class VirtualObject
    {        
        public String name;
        public ArrayList<VirtualData> vdata;
        
        public VirtualObject()
        {
            vdata = new ArrayList<VirtualData>();
        }
        
        public int HasType(VirtualData.DATA_TYPE t)
        {
            for (int i = 0; i < vdata.size(); ++i)
            {
                if (vdata.get(i).type == t)
                    return i;
            }
            
            return -1;
        }
        
        public VirtualData GetFirstOf(VirtualData.DATA_TYPE t)
        {
            for (int i = 0; i < vdata.size(); ++i)
            {
                if (vdata.get(i).type == t)
                    return vdata.get(i);
            }
            
            return null;
        }
    }

    public static Map LoadMap(String name) throws FileNotFoundException, Exception
    {
        Scanner s = new Scanner(new File(name));
        s.useLocale(Locale.US);
        
        ArrayList<VirtualObject> objects = new ArrayList<VirtualObject>();
        
        // LOAD
        while ( s.hasNext() ){
                        
            String objTag = s.next();
            ProcessTag(s, objTag, objects);
            
        }
        
        Map map = ProcessObjects(objects);
        
        return map;
    }
    
    private static Map ProcessObjects(ArrayList<VirtualObject> objects)
    {
        // Map m = new Map();
        Map m = new Island(SkyRiders.SkyRiders.gl);
        
        for (VirtualObject obj : objects)
        {
            if ( !ProcessSpecific(m, obj) )
            {
                ProcessGeneric(m, obj);
            }
        }
        
        return m;
    }
    
    private static boolean ProcessSpecific(Map m, VirtualObject obj)
    {
        System.out.print("PROCESS SPECIFIC "+obj.name);
        
        switch (obj.name)
        {
            case "RaceStart":
                m.startpoint = (Transform) obj.GetFirstOf(VirtualData.DATA_TYPE.TRANSFORM).data;
                return true;
        }
        
        if (obj.name.toLowerCase().contains("leaf")){
            LoadFoliage(m, obj);
            return true;
        }
        else if (obj.name.startsWith("waterPlane"))
        {
            if (waterPlane == null)
                waterPlane = new TexturedMesh(MeshHandler.LoadMesh("./Assets/graphics/water_plane.obj", null , ShaderHandler.waterShader), ShaderHandler.waterShader);
            
            Transform trans = (Transform) obj.GetFirstOf(VirtualData.DATA_TYPE.TRANSFORM).data;
            WaterObject waterObj = new WaterObject(trans, waterPlane);
            waterObj.setRenderType(RENDER_TYPE.RENDER_WATER);
            m.addObject(waterObj);
            return true;
        }
        else if (obj.name.startsWith("lavaPlane"))
        {
            if (lavaPlane == null)
                lavaPlane = new TexturedMesh(MeshHandler.LoadMesh("./Assets/graphics/lava_plane.obj", null , ShaderHandler.waterShader), ShaderHandler.waterShader);
            
            Transform trans = (Transform) obj.GetFirstOf(VirtualData.DATA_TYPE.TRANSFORM).data;
            WaterObject waterObj = new WaterObject(trans, lavaPlane);
            waterObj.setRenderType(RENDER_TYPE.RENDER_WATER);
            m.addObject(waterObj);
            return true;
        }
        
        
        System.out.println(" TRYING GENERIC... ");
        return false;
    }
    
    private static void LoadFoliage(Map m, VirtualObject obj){
        int mesh_id = obj.HasType(VirtualData.DATA_TYPE.MESHFILE);
        int transform_id = obj.HasType(VirtualData.DATA_TYPE.TRANSFORM);
        
        Transform transform = (Transform) obj.vdata.get(transform_id).data;
        
        String[] meshinfo = (String[]) obj.vdata.get(mesh_id).data;
        String files[] = LODHandler.GetLODFiles(meshinfo[0]);
        
        MeshRenderer[] renderers = new MeshRenderer[files.length];
        for (int i = 0; i < files.length; ++i){
        
            Group mesh = MeshHandler.LoadMesh(files[i], meshinfo[1], ShaderHandler.foliageShader);
            TexturedMesh om = new TexturedMesh(mesh, ShaderHandler.foliageShader);
            renderers[i] = om;
        }

        FoliageObject gameobj = new FoliageObject(transform, renderers);
        gameobj.name = "Foliage_object"+m.objects.size();
        gameobj.setRenderType(GameRenderer.RENDER_TYPE.RENDER_SOLID2FACES);

        m.addObject(gameobj);
        
        System.out.println("New foliage loaded!");
    }
    
    private static void ProcessGeneric(Map m, VirtualObject obj)
    {
//        System.out.println("DOING GENERIC PROCESS.");
        int mesh_id = obj.HasType(VirtualData.DATA_TYPE.MESHFILE);
        int transform_id = obj.HasType(VirtualData.DATA_TYPE.TRANSFORM);
        
        if (mesh_id != -1 && transform_id != -1)
        {
            Transform transform = (Transform) obj.vdata.get(transform_id).data;
            
            String[] meshinfo = (String[]) obj.vdata.get(mesh_id).data;
            String files[] = LODHandler.GetLODFiles(meshinfo[0]);
        
            MeshRenderer[] renderers = new MeshRenderer[files.length];
            for (int i = 0; i < files.length; ++i){

                Group mesh = MeshHandler.LoadMesh(files[i], meshinfo[1], ShaderHandler.generalShader);
                TexturedMesh om = new TexturedMesh(mesh, ShaderHandler.generalShader);
                renderers[i] = om;
            }
            GameObject gameobj = new GameObject(transform, renderers);
            
            gameobj.name = "Generic_solid"+m.objects.size();
            gameobj.setRenderType(GameRenderer.RENDER_TYPE.RENDER_SOLID);
            
            m.addObject(gameobj);
        }
    }
    
    private static ArrayList<String> ReadTypes(Scanner s)
    {
        ArrayList<String> t = new ArrayList<String>();
        
        String str;
        while ( (str = s.next()).compareTo("!") != 0)
        {
            t.add(str);
        }
        
        return t;
    }
    
    private static void ProcessTag(Scanner s, String objTag, ArrayList<VirtualObject> objects) throws Exception
    {
        if (objTag.compareTo("+") == 0)
        {
            VirtualObject obj = new VirtualObject();
            
            String objName = s.next();
            obj.name = objName;
//            System.out.println("NAME: "+objName);
                        
            ArrayList<String> types = ReadTypes(s);
            for ( String t : types )
            {
                switch (t){
                    case "m":
                        obj.vdata.add(LoadMesh(s));
                    break;
                    case "t":
                        obj.vdata.add(LoadTransform(s));
                    break;
                }
            }
            
            objects.add(obj);
           
        } else {
            throw new Exception("OPS! Unexpected tag?");
        }      
        
    }
    
    private static VirtualData LoadMesh(Scanner s)
    {
        VirtualData vd = new VirtualData();
        
        String [] array = new String[2];
        array[0] = s.next();    // .obj Filepath
        array[1] = s.next();    // group name
        
        vd.type = VirtualData.DATA_TYPE.MESHFILE;
        vd.data = array;
        
//        System.out.println("MESH: "+array[0]+" GROUP: '"+array[1]+"'");
        
        return vd;
    }
    
    private static VirtualData LoadTransform(Scanner s){
        VirtualData vd = new VirtualData();
               
        Transform t = new Transform();
        
        t.position.x = s.nextFloat();
        t.position.y = s.nextFloat();
        t.position.z = s.nextFloat();
        
        t.rotation.x = s.nextFloat();
        t.rotation.y = s.nextFloat();
        t.rotation.z = s.nextFloat();
        
        t.scale.x = s.nextFloat();
        t.scale.y = s.nextFloat();
        t.scale.z = s.nextFloat();
        
//        System.out.println("TRANSFORM: "+t.position + " "+t.rotation+" "+t.scale);
        
        vd.type = VirtualData.DATA_TYPE.TRANSFORM;
        vd.data = t;
        
        return vd;
    }
    
    
    public static void main(String args[]){
        
        Scanner s = new Scanner("1.0050 1");
        s.useLocale(Locale.US);
        
        float t = s.nextFloat();
        System.out.println(t);
        t = s.nextFloat();
        System.out.println(t);
        
    }
    
}
