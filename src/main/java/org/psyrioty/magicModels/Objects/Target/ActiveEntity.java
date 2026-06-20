package org.psyrioty.magicModels.Objects.Target;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.ActiveModel;
import org.psyrioty.magicModels.Objects.Animations.Animation;

import java.util.ArrayList;
import java.util.List;

public class ActiveEntity {
    Entity target;
    List<ActiveModel> activeModels = new ArrayList<>();

    //=================для проверки движется ли игрок===================
    World world;
    double x;
    double y;
    double z;

    byte tick = 0;

    boolean walking = false;
    boolean flying = false;
    //-------------------------------------------------------------------

    double scale;

    //double addOffsetY = 0; //для полета и плавания
    //double addOffsetZ = 0; //для полета и плавания

    public ActiveEntity(
            Entity target
    ){
        this.target = target;

        if(target instanceof Player player){
            this.scale = player.getAttribute(Attribute.SCALE).getValue();
        }
    }

    public double getScale() {
        return scale;
    }

    public void animationTick(){
        /*if(target instanceof Player player) {
            checkOtherPose(player);
        }*/

        if(tick == 1) {
            walk();
            fly();
            tick = 0;
        }else{
            tick++;
        }

        if(activeModels != null) {
            for (ActiveModel activeModel : new ArrayList<>(activeModels)) {
                activeModel.animationTick();
            }
        }
    }

    /*private void checkOtherPose(Player player){
        if(
                player.isGliding() ||
                        player.isSwimming()
        ){
            double scalePlayer = scale * 2;
            double pitchPlayer = player.getPitch();

            double pitchExchangeRate = 90 - pitchPlayer;

            double offset = (((double) 1 /180) * pitchExchangeRate) - 1;

            this.addOffsetZ = offset * scalePlayer;
            this.addOffsetY = offset * scalePlayer;
        }else{
            if(this.addOffsetY != 0){
                this.addOffsetY = 0;
                this.addOffsetZ = 0;
            }
        }
    }

    public double getAddOffsetY() {
        return addOffsetY;
    }

    public double getAddOffsetZ() {
        return addOffsetZ;
    }*/

    private void fly(){
        if(flying == !target.isOnGround()){
            return;
        }

        for (ActiveModel activeModel : activeModels) {
            for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                if(animation.getName().equals("fly")){
                    animation.setEnable(!target.isOnGround());
                }
            }
        }

        flying = !target.isOnGround();
    }

    private void walk(){
        Location location = target.getLocation();
        boolean moving = x != location.getX() ||
                z != location.getZ();

        setLocation(
                location.getWorld(),
                location.getX(),
                location.getY(),
                location.getZ()
        );

        if(walking == moving){
            return;
        }

        for (ActiveModel activeModel : activeModels) {
            for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                if(animation.getName().equals("walk")){
                    animation.setEnable(moving);
                }
            }
        }

        walking = moving;
    }

    public Entity getTarget() {
        return target;
    }

    public List<ActiveModel> getActiveModels() {
        return activeModels;
    }

    public void addActiveModel(ActiveModel activeModel){
        activeModels.add(activeModel);
    }

    public void setLocation(World world, double x, double y, double z){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public void removeAll(){
        for(ActiveModel activeModel: activeModels){
            activeModel.remove();
        }

        activeModels = new ArrayList<>();
    }
}
