package SkyRacers.core;

import java.util.ArrayList;

public class Map {
    
    protected ArrayList<GameObject> objects;
    protected ArrayList<GameObject> checkpoints;
    protected FrustumCulling frusCull;
    
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
            if(frusCull.pointInFrustum(go.getTransform().position) != frusCull.OUTSIDE)
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
