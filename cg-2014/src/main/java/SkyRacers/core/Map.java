package SkyRacers.core;

import SkyRacers.Airplane;
import java.util.ArrayList;

public class Map {
    
    protected ArrayList<GameObject> objects;
    protected ArrayList<GameObject> checkpoints;
    
    public Map()
    {
        objects = new ArrayList<>();
        checkpoints = new ArrayList<>();
    }
    
    public void addObject(GameObject obj)
    {
        objects.add(obj);
    }
    
    public void update()
    {
        for (GameObject go : objects){
            go.update();
        }
    }
    
    public void draw()
    {
        for (GameObject go : objects){
            go.draw();
        }
    }
    
    public void dispose()
    {
        objects.clear();
        checkpoints.clear();
    }
    
}
