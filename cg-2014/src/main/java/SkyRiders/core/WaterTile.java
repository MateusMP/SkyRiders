/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders.core;

import Handlers.ShaderHandler;
import MathClasses.Vector3;
import static SkyRiders.SkyRiders.gl;
import br.usp.icmc.vicg.gl.model.*;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

/**
 *
 * @author PC
 */
public class WaterTile extends GameObject{
    private int VAO;
    private int[] VBO;
    
    public WaterTile() {
        super(new Vector3(0, 0, 0), null);
        VAO = ShaderHandler.waterShader.CreateTile();
        setRenderType(GameRenderer.RENDER_TYPE.RENDER_WATER);
    }
    
    @Override
    public void draw() {
        gl.glBindVertexArray(VAO);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 3*2);
    }
}
