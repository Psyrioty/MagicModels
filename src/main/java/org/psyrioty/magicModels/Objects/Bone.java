package org.psyrioty.magicModels.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.ResourcePack.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Bone {

    float originX, originY, originZ; //начальная точка привязки
    //float globalX, globalY, globalZ; //глобальная точка смещения
    float rotationX, rotationY, rotationZ; //поворот кости
    List<Bone> childBones = new ArrayList<>(); //дочерние кости
    String name; //имя кости
    UUID uuid; //идентификатор кости
    ItemDisplay boneEntity;
    Bone headBone;

    //----------------АНИМАЦИЯ ЕБ***Я----------------------------------------
    float animRotationX, animRotationY, animRotationZ;
    float animPositionX, animPositionY, animPositionZ;
    float animScaleX, animScaleY, animScaleZ;

    Vector3f newOrigin;
    //----------------для перерасчёта дочерних костей анимаций---------------
    float addTranslateX, addTranslateY, addTranslateZ;
    float addRotateX, addRotateY, addRotateZ;
    float addScaleX, addScaleY, addScaleZ;
    //=======================================================================

    //------------------для настроек модели----------------------------------
    float offsetX = 0;
    float offsetY = 0;
    float offsetZ = 0;

    float scale = 1;

    int brightness = 0;
    //=======================================================================


    Group group; //для формирования ресурспаков

    String modelName;

    public Bone(
            float originX,
            float originY,
            float originZ,

            float rotationX,
            float rotationY,
            float rotationZ,

            String name,

            UUID uuid,

            Group group,

            String modelName
    ){

        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;

        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;

        this.animPositionX = 0;
        this.animPositionY = 0;
        this.animPositionZ = 0;

        this.animRotationX = 0;
        this.animRotationY = 0;
        this.animRotationZ = 0;

        this.animScaleX = 1;
        this.animScaleY = 1;
        this.animScaleZ = 1;

        this.name = name;

        this.uuid = uuid;

        this.group = group;

        this.modelName = modelName;
    }

    public void remove(){
        boneEntity.leaveVehicle();
        for(Entity entity: boneEntity.getPassengers()){
            entity.leaveVehicle();
        }
        boneEntity.remove();
    }

    public void setAnimScale(float x, float y, float z){
        this.animScaleX = x;
        this.animScaleY = y;
        this.animScaleZ = z;
    }

    public void setNewOrigin(Vector3f newOrigin) {
        this.newOrigin = newOrigin;
    }

    public Vector3f getNewOrigin() {
        return newOrigin;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getAnimPositionX() {
        return animPositionX;
    }

    public float getAnimPositionY() {
        return animPositionY;
    }

    public float getAnimPositionZ() {
        return animPositionZ;
    }

    public float getAnimRotationX() {
        return animRotationX;
    }

    public float getAnimRotationY() {
        return animRotationY;
    }

    public float getAnimRotationZ() {
        return animRotationZ;
    }

    public float getAnimScaleX() {
        return animScaleX;
    }

    public float getAnimScaleY() {
        return animScaleY;
    }

    public float getAnimScaleZ() {
        return animScaleZ;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public float getScale() {
        return scale;
    }

    public void setAnimPosition(float x, float y, float z){
        animPositionX = x;
        animPositionY = y;
        animPositionZ = z;
    }

    //type = rotation, position, scale
    public void mathAddAnimation(float x, float y, float z, String type){
        switch (type){
            case "rotation":
                addRotateX += x;
                addRotateY += y;
                addRotateZ += z;
                break;
            case "position":
                addTranslateX += x;
                addTranslateY += y;
                addTranslateZ += z;
                break;
            case "scale":
                addScaleX += x;
                addScaleY += y;
                addScaleZ += z;
                break;
        }
    }

    public void setAnimRotation(float x, float y, float z){
        animRotationX = x;
        animRotationY = y;
        animRotationZ = z;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }

    public void setOriginZ(float originZ) {
        this.originZ = originZ;
    }

    public float getAddRotateX() {
        return addRotateX;
    }

    public float getAddRotateY() {
        return addRotateY;
    }

    public float getAddRotateZ() {
        return addRotateZ;
    }

    public float getAddScaleX() {
        return addScaleX;
    }

    public float getAddScaleY() {
        return addScaleY;
    }

    public float getAddScaleZ() {
        return addScaleZ;
    }

    public float getAddTranslateX() {
        return addTranslateX;
    }

    public float getAddTranslateY() {
        return addTranslateY;
    }

    public float getAddTranslateZ() {
        return addTranslateZ;
    }

    public void clearAddTranslate(){
        addTranslateX = 0;
        addTranslateY = 0;
        addTranslateZ = 0;
    }

    public void clearAddRotate(){
        addRotateX = 0;
        addRotateY = 0;
        addRotateZ = 0;
    }

    public void clearAddScale(){
        addScaleX = 0;
        addScaleY = 0;
        addScaleZ = 0;
    }

    public void setHeadBone(Bone headBone) {
        this.headBone = headBone;
    }

    public Bone getHeadBone() {
        return headBone;
    }

    public void addChildBone(Bone bone){
        childBones.add(bone);

        //bone.setOriginX(bone.getGlobalX() - globalX);
        //bone.setOriginY(bone.getGlobalY() - globalY);
        //bone.setOriginZ(bone.getGlobalZ() - globalZ);


        Bukkit.getLogger().info("");
        Bukkit.getLogger().info(bone.name + " X: " + bone.getOriginX() + " Y: " + bone.getOriginY() + " Z: " + bone.getOriginZ());
        Bukkit.getLogger().info("");
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getOriginZ() {
        return originZ;
    }

    public List<Bone> getChildBones() {
        return childBones;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Bone clone() {
        Bone copy = new Bone(
                originX,
                originY,
                originZ,
                rotationX,
                rotationY,
                rotationZ,
                name,
                uuid,
                null,
                modelName
        );

        return copy;
    }

    //для формирования ресурспаков
    public Group getGroup() {
        return group;
    }

    public ItemDisplay getBoneEntity() {
        return boneEntity;
    }

    public void createBoneEntity(
            Entity target,
            int brightness,
            float scale,
            float offsetX,
            float offsetY,
            float offsetZ
    ){
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.brightness = brightness;


        World world = target.getWorld();
        Location location = target.getLocation();
        location.setPitch(0);
        location.setYaw(0);

        ItemStack itemStack = new ItemStack(Material.WHITE_WOOL);
        ItemMeta meta = itemStack.getItemMeta();

        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        List<String> strings = new ArrayList<>();
        strings.add(modelName + "_" + name);
        Bukkit.getLogger().info(modelName + "_" + name);
        component.setStrings(strings);


        meta.setCustomModelDataComponent(component);

        itemStack.setItemMeta(meta);

        ItemDisplay display = world.spawn(location, ItemDisplay.class, entity -> {
            entity.setItemStack(itemStack);
        });

        display.setInterpolationDuration(1);
        display.setPersistent(false);

        if(brightness > -1) {
            display.setBrightness(new Display.Brightness(brightness, brightness));
        }

        Quaternionf rotation = new Quaternionf()
                .rotateXYZ(
                        (float) Math.toRadians(rotationX),
                        (float) Math.toRadians(rotationY),
                        (float) Math.toRadians(rotationZ)
                );

        display.setTransformation(new Transformation(
                new Vector3f(
                        originX + this.offsetX,
                        originY + this.offsetY,
                        originZ + this.offsetZ
                ),        // смещение
                new Quaternionf(),                              // левый поворот
                new Vector3f(
                        1 * 2 * this.scale,
                        1 * 2 * this.scale,
                        1 * 2 * this.scale
                ),                                              // масштаб
                rotation                                        // правый поворот
        ));

        target.addPassenger(display);

        boneEntity = display;
    }
}