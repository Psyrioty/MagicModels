package org.psyrioty.magicModels.Objects.Animations;

public class AnimationKey {
    int tick; //это типо время будет, на котором метка
    //Bone bone; //кость, для которой сделана метка
    float x, y, z; //координаты ключа для scale/rotation/postion
    String typeKey; //тип ключа, "position", "rotation", "scale"

    public AnimationKey(
            int tick,
            float x, float y, float z,
            String typeKey
    ){
        this.tick = tick;

        if(typeKey.equals("position")){
            x /= 16;
            y /= 16;
            z /= 16;
        }

        this.x = x;
        this.y = y;
        this.z = z;

        this.typeKey = typeKey;
    }

    public int getTick() {
        return tick;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public AnimationKey clone(){
        if(typeKey.equals("position")){
            return new AnimationKey(
                    tick,
                    x * 16, y * 16, z * 16,
                    typeKey
            );
        }

        return new AnimationKey(
                tick,
                x, y, z,
                typeKey
        );
    }
}
