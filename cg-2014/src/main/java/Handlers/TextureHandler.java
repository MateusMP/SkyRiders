/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handlers;

import static SkyRiders.SkyRiders.gl;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLProfile;

public class TextureHandler {
    private static final HashMap<String, Texture> textures = new HashMap<String, Texture>();
    
    public static Texture LoadTexture(String path)
    {
        Texture tex = textures.get(path);
        if (tex != null)
            return tex;
        
        File file = new File(path);
        if (!file.exists()){
            System.out.println("ERROR: TextureHandler: File not found: "+path);
            return null;
        }
        
        if (tex == null)
        {
            BufferedImage image;
            try {
                System.out.println("Loading Texture: "+file);
                image = ImageIO.read(file);
                ImageUtil.flipImageVertically(image); //vertically flip the image

                tex = new Texture(path);
                tex.texturedata = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
                tex.texturedata.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                tex.texturedata.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                tex.texturedata.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                tex.texturedata.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                textures.put(path, tex);
            } catch (IOException ex) {
                Logger.getLogger(TextureHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return tex;
    }
    
}
