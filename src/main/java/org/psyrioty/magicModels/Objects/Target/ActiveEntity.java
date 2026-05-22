package org.psyrioty.magicModels.Objects.Target;


import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.psyrioty.magicModels.Objects.ActiveModel;

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
