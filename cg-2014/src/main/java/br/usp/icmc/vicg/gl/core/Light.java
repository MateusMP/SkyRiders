package br.usp.icmc.vicg.gl.core;

import java.util.Arrays;

public class Light 
{
    private float[] ambientColor;
    private float[] diffuseColor;
    private float[] specularColor;
    private float[] position;

    public Light() {
        setPosition(new float[]{0.0f, 0.0f, 1.0f, 0.0f});
        setAmbientColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    }

    public float[] getAmbientColor() {
        return ambientColor;
    }

    public float[] getDiffuseColor() {
        return diffuseColor;
    }

    public float[] getSpecularColor() {
        return specularColor;
    }

    public float[] getPosition() {
        return position;
    }
  
    public final void setPosition(float[] position) {
      this.position = Arrays.copyOf(position, position.length);
    }

    public final void setAmbientColor(float[] ambientColor) {
      this.ambientColor = Arrays.copyOf(ambientColor, ambientColor.length);
    }

    public final void setDiffuseColor(float[] diffuseColor) {
      this.diffuseColor = Arrays.copyOf(diffuseColor, diffuseColor.length);
    }

    public final void setSpecularColor(float[] specularColor) {
      this.specularColor = Arrays.copyOf(specularColor, specularColor.length);
    }

}
