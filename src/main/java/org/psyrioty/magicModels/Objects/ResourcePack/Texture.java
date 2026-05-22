package org.psyrioty.magicModels.Objects.ResourcePack;

public class Texture {
    int width;
    int height;
    int uv_width;
    int uv_height;
    String name;

    public Texture(
            String name,

            int width, int height,

            int uv_width, int uv_height
    ){
        this.name = name;

        this.width = width;
        this.height = height;

        this.uv_width = uv_width;
        this.uv_height = uv_height;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getUv_width() {
        return uv_width;
    }

    public int getUv_height() {
        return uv_height;
    }

    public int getWidth() {
        return width;
    }
}
