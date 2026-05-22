package org.psyrioty.magicModels.Objects.BBModel;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Outliner {

    UUID uuid; //uuid
    List<Outliner> childOutliners = new ArrayList<>();

    public Outliner(UUID uuid){
        this.uuid = uuid;
    }

    public void addChildOutliner(Outliner outliner){
        this.childOutliners.add(outliner);
    }

    public List<Outliner> getChildOutliners() {
        return childOutliners;
    }

    public UUID getUuid() {
        return uuid;
    }
}
