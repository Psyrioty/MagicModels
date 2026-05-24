package org.psyrioty.magicModels.Objects.Target;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
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
    //-------------------------------------------------------------------

    public ActiveEntity(
            Entity target
    ){
        this.target = target;
    }

    public void animationTick(){
        walk();

        for(ActiveModel activeModel: activeModels){
            activeModel.animationTick();
        }
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

        for (ActiveModel activeModel : activeModels) {
            for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                if(animation.getName().equals("walk")){
                    animation.setEnable(moving);
                }
            }
        }
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
}
