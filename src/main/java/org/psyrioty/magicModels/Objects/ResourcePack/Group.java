package org.psyrioty.magicModels.Objects.ResourcePack;

import java.util.List;

public class Group {
    String name;
    List<Float> origin;
    float scope;
    float color;

    public Group(
            String name,
            List<Float> origin,
            float scope,
            float color
    ){
        this.name = name;
        this.origin = origin;
        this.scope = scope;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public float getColor() {
        return color;
    }

    public float getScope() {
        return scope;
    }

    public List<Float> getOrigin() {
        return origin;
    }
}
