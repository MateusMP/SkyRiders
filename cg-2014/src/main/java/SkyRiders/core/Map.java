package SkyRiders.core;

import MathClasses.Transform;
import java.util.ArrayList;

public class Map {
    
    protected ArrayList<GameObject> objects;
    protected ArrayList<GameObject> checkpoints;
    
    public Transform startpoint = new Transform();
    
    public Map()
    {
        this.objects = new ArrayList<>();
        this.checkpoints = new ArrayList<>();
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

    public void dispose()
    {
        objects.clear();
        checkpoints.clear();
    }

}
