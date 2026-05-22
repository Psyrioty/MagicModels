package org.psyrioty.magicModels.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.ActiveModel;

import java.util.List;
import java.util.Set;

public class Tasker {
    BukkitTask updateTask;
    Set<ActiveModel> models;

    public Tasker(){
        update();
    }

    private void update(){
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MagicModels.getPlugin(), () -> {
            for(ActiveModel model: MagicModels.getPlugin().getActiveModels()){
                model.animationTick();
            }
        },1L,1L);
    }

    public void reload(){
        updateTask.cancel();

        update();
    }
}
