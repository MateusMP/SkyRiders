/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SkyRacers.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import javax.media.opengl.GL;

public class Line extends SimpleModel {

    public Line(Vector3 begin, Vector3 end) {
        // Triangle
        vertex_buffer = new float[]{
            begin.x, begin.y, begin.z,
            end.x, end.y, end.z};
        
    }
    
    @Override
    public void draw() {
        draw(GL.GL_LINES);
    }
}
