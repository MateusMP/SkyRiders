/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handlers;

/**
 *
 * @author Mateus
 */
public class LODHandler {
    
    public static String[] GetLODFiles(String file){

        
        String path = file.substring(0, file.lastIndexOf("/")+1);
        String name = file.substring(file.lastIndexOf("/")+1);
        name = name.substring(0, name.lastIndexOf("."));
        
        System.out.println(file+" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> NAME: "+name);
        
        
        String[] out = new String[]{""};
        switch (name){
            
            case "Rocks_03":
                out = new String[]{"", "_L1", "_L2", "_L3", "_L4"};
            break;
                
            case "Rocks_02":
                out = new String[]{"", "_L1", "_L2", "_L3", "_L4"};
            break;
            
        }
        
        for ( int i = 0; i < out.length; ++i ){
            out[i] = path+name+out[i]+".obj";
            System.out.println(out[i]);
        }
        
        return out;
    }
    
}
