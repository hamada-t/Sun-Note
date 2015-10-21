package com.example.hiranakalab02.sunnote.gles;

import com.example.hiranakalab02.sunnote.mqo.MQOMaterial;

import java.io.IOException;

/**
 * Created by HiranakaLab02 on 2015/08/28.
 */
public class Material extends GLObject {
    private Texture texture = null;

    public Material(GLManager gl, MQOMaterial material) {
        setGLManager(gl);
        try {
            texture = gl.createTextureFromAsset(material.getTextureName());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void bind() {
        if (texture != null) {
            texture.bind();
        }
    }

    @Override
    public void unbind() {
        if (texture != null) {
            texture.unbind();
        }
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
