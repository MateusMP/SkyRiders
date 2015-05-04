package SkyRacers.core;

import MathClasses.Transform;
import SkyRacers.Circuits.Island;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;


public class MapLoader
{
    protected static class VirtualData
    {
        enum DATA_TYPE{TRANSFORM, MESHFILE};
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
        Map m = new Island(SkyRacers.SkyRacers.hdl().gl, SkyRacers.SkyRacers.hdl().shader);
        
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
            case "StartRace":
                // TODO
                return true;
        }
        
        System.out.print (" NOT FOUND -> ");
        return false;
    }
    
    private static void ProcessGeneric(Map m, VirtualObject obj)
    {
        System.out.println("DOING GENERIC PROCESS.");
        int mesh_id = obj.HasType(VirtualData.DATA_TYPE.MESHFILE);
        int transform_id = obj.HasType(VirtualData.DATA_TYPE.TRANSFORM);
        
        if (mesh_id != -1 && transform_id != -1)
        {
            Transform transform = (Transform) obj.vdata.get(transform_id).data;
            JWavefrontObject mesh = MeshHandler.hdl().LoadMesh((String) obj.vdata.get(mesh_id).data);
            
            m.addObject( new GameObject(transform, mesh));
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
            System.out.println("NAME: "+objName);
                        
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
        String name = s.next();
        
        vd.type = VirtualData.DATA_TYPE.MESHFILE;
        vd.data = name;
        
        // MeshHandler.hdl().LoadMesh("data/graphics/"+name+".obj")
        
        return vd;
    }
    
    private static VirtualData LoadTransform(Scanner s){
        VirtualData vd = new VirtualData();
               
        Transform t = new Transform();
        
        System.out.println("READING TRANSFORM");
        
        t.position.x = s.nextFloat();
        t.position.y = s.nextFloat();
        t.position.z = s.nextFloat();
        
        
        t.rotation.x = s.nextFloat();
        t.rotation.y = s.nextFloat();
        t.rotation.z = s.nextFloat();
        
        // Escalar
        t.scale.x = s.nextFloat();
        t.scale.y = s.nextFloat();
        t.scale.z = s.nextFloat();
        
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
