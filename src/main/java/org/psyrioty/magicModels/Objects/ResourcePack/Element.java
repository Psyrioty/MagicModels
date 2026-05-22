package org.psyrioty.magicModels.Objects.ResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Element {

    List<Float> from,
            to,
            rotationOrigin, // нормальная origin записывается после нахождения группы
            northFaces, eastFaces, southFaces, westFaces, upFaces, downFaces;
    String northTextureName, eastTextureName, southTextureName, westTextureName, upTextureName, downTextureName;
    float rotationX, rotationY, rotationZ;
    String name;
    UUID uuid;
    int resolutionWidth, resolutionHeight;

    private int northRotation;
    private int eastRotation;
    private int southRotation;
    private int westRotation;
    private int upRotation;
    private int downRotation;

    Texture northTexture;
    Texture eastTexture;
    Texture southTexture;
    Texture westTexture;
    Texture upTexture;
    Texture downTexture;

    List<Float> pivotPoint;

    public Element(
            List<Float> from,
            List<Float> to,

            float rotationX, float rotationY, float rotationZ,
            List<Float> rotationOrigin,

            List<Float> northFaces,
            List<Float> eastFaces,
            List<Float> southFaces,
            List<Float> westFaces,
            List<Float> upFaces,
            List<Float> downFaces,

            String northTextureName,
            String eastTextureName,
            String southTextureName,
            String westTextureName,
            String upTextureName,
            String downTextureName,

            String name,

            UUID uuid,

            int resolutionWidth,
            int resolutionHeight,

            int downRotation,
            int eastRotation,
            int northRotation,
            int southRotation,
            int upRotation,
            int westRotation,

            Texture northTexture,
            Texture downTexture,
            Texture eastTexture,
            Texture southTexture,
            Texture upTexture,
            Texture westTexture
    ){
        //this.from = repairModel(from);
        //this.to = repairModel(to);

        this.from = from;
        this.to = to;

        this.rotationOrigin = rotationOrigin;

        List<Float> pivot = new ArrayList<>();
        pivot.add((float) 8);
        pivot.add((float) 8);
        pivot.add((float) 8);

        this.pivotPoint = pivot;

        this.northFaces = northFaces;
        this.eastFaces = eastFaces;
        this.southFaces = southFaces;
        this.westFaces = westFaces;
        this.upFaces = upFaces;
        this.downFaces = downFaces;

        this.northTextureName = northTextureName;
        this.eastTextureName = eastTextureName;
        this.southTextureName = southTextureName;
        this.westTextureName = westTextureName;
        this.upTextureName = upTextureName;
        this.downTextureName = downTextureName;

        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;

        this.name = name;

        this.uuid = uuid;

        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;

        this.downRotation = downRotation;
        this.eastRotation = eastRotation;
        this.northRotation = northRotation;
        this.southRotation = southRotation;
        this.upRotation = upRotation;
        this.westRotation = westRotation;


        this.northTexture = northTexture;
        this.downTexture = downTexture;
        this.eastTexture = eastTexture;
        this.southTexture = southTexture;
        this.upTexture = upTexture;
        this.westTexture = westTexture;
    }

    private List<Float> repairModel(List<Float> values){
        List<Float> newValues = new ArrayList<>();

        float x = (values.getFirst() + 8) / 2;
        float y = values.get(1) / 2;
        float z = (values.get(2) + 8) / 2;

        newValues.add(x);
        newValues.add(y);
        newValues.add(z);

        return newValues;
    }

    public List<Float> getPivotPoint() {
        return pivotPoint;
    }

    public Texture getDownTexture() {
        return downTexture;
    }

    public Texture getEastTexture() {
        return eastTexture;
    }

    public Texture getNorthTexture() {
        return northTexture;
    }

    public Texture getSouthTexture() {
        return southTexture;
    }

    public Texture getUpTexture() {
        return upTexture;
    }

    public Texture getWestTexture() {
        return westTexture;
    }

    public int getDownRotation() {
        return downRotation;
    }

    public int getEastRotation() {
        return eastRotation;
    }

    public int getNorthRotation() {
        return northRotation;
    }

    public int getSouthRotation() {
        return southRotation;
    }

    public int getUpRotation() {
        return upRotation;
    }

    public int getWestRotation() {
        return westRotation;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public List<Float> getFrom() {
        return from;
    }

    public List<Float> getTo() {
        return to;
    }

    public List<Float> getRotationOrigin() {
        return rotationOrigin;
    }

    public List<Float> getNorthFaces() {
        return northFaces;
    }

    public List<Float> getEastFaces() {
        return eastFaces;
    }

    public List<Float> getSouthFaces() {
        return southFaces;
    }

    public List<Float> getWestFaces() {
        return westFaces;
    }

    public List<Float> getDownFaces() {
        return downFaces;
    }

    public List<Float> getUpFaces() {
        return upFaces;
    }

    public String getNorthTextureName() {
        return northTextureName;
    }

    public String getEastTextureName() {
        return eastTextureName;
    }

    public String getSouthTextureName() {
        return southTextureName;
    }

    public String getDownTextureName() {
        return downTextureName;
    }

    public String getUpTextureName() {
        return upTextureName;
    }

    public String getWestTextureName() {
        return westTextureName;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
