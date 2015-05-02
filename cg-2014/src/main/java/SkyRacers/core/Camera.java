/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

/**
 *
 * @author Mateus
 */
public interface Camera {
    
    public void DefineViewMatrix(Matrix4 viewMatrix);
    
    public Vector3 GetPosition();
    
}
