package SkyRacers.core;

import MathClasses.Transform;
import java.util.ArrayList;

public class Map {
    
    protected ArrayList<GameObject> objects;
    protected ArrayList<GameObject> checkpoints;
    protected FrustumCulling frusCull;
    
    public Transform startpoint = new Transform();
    
    public Map()
    {
        this.objects = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
        this.frusCull = null;
    }
    
    public Map(FrustumCulling fc)
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
        for (GameObject go : this.objects){
        //for(int i = 0; i < 1; i++){
            //GameObject go = this.objects.get(i);
            System.out.println("object x y z: "+ go.getTransform().position.x +" "+ go.getTransform().position.y +" "+go.getTransform().position.z);
            if(frusCull.pointInFrustum(go.getTransform().position) != FrustumCulling.OUTSIDE)
                go.draw();
        }
    }
    
    public void dispose()
    {
        objects.clear();
        checkpoints.clear();
    }

    public void setFrusCull(FrustumCulling frusCull) {
        this.frusCull = frusCull;
    }
    
}
