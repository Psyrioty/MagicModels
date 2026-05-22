package org.psyrioty.magicModels.Listeners;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.ActiveModel;
import org.psyrioty.magicModels.Objects.Animations.Animation;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;

public class TargetEvents implements Listener {
    @EventHandler
    private void targetTeleport(EntityTeleportEvent event){

    }

    @EventHandler
    private void targetMove(EntityMoveEvent event){
        if(event.isCancelled()){
            return;
        }

        Entity entity = event.getEntity();
        ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

        if(activeEntity == null){
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(MagicModels.getPlugin(), () -> {
            gliding(activeEntity, entity);
            swim(activeEntity, entity);
            //walk(activeEntity, entity);
        });
    }

    @EventHandler
    private void targetMove(PlayerMoveEvent event){
        if(event.isCancelled()){
            return;
        }

        Entity entity = event.getPlayer();
        ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

        if(activeEntity == null){
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(MagicModels.getPlugin(), () -> {
            gliding(activeEntity, entity);
            swim(activeEntity, entity);
            //walk(activeEntity, entity);
        });
    }

    private void gliding(ActiveEntity activeEntity, Entity entity){
        if(entity instanceof Player player){
            for (ActiveModel activeModel : activeEntity.getActiveModels()) {
                for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                    if (animation.getName().equals("gliding")) {
                        animation.setEnable(player.isGliding());
                    }
                }
            }
        }
    }

    private void swim(ActiveEntity activeEntity, Entity entity){
        if(entity instanceof Player player){
            for (ActiveModel activeModel : activeEntity.getActiveModels()) {
                for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                    if(animation.getName().equals("swim")){
                        animation.setEnable(player.isSwimming());
                    }
                }
            }
        }else{
            for (ActiveModel activeModel : activeEntity.getActiveModels()) {
                for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                    if(animation.getName().equals("swim")){
                        animation.setEnable(entity.isInWater());
                    }
                }
            }
        }
    }


    @EventHandler
    private void entityJumpEvent(EntityJumpEvent event){
        if(event.isCancelled()){
            return;
        }

        Entity entity = event.getEntity();
        ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

        if(activeEntity == null){
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(MagicModels.getPlugin(), () -> {
            jump(activeEntity, entity);
        });
    }

    @EventHandler
    private void entityJumpEvent(PlayerJumpEvent event){
        if(event.isCancelled()){
            return;
        }

        Player entity = event.getPlayer();
        ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

        if(activeEntity == null){
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(MagicModels.getPlugin(), () -> {
            jump(activeEntity, entity);
        });
    }

    private void jump(ActiveEntity activeEntity, Entity entity){
        for (ActiveModel activeModel : activeEntity.getActiveModels()) {
            for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                if(animation.getName().equals("jump")){
                    animation.setEnable(true);
                }
            }
        }
    }



    @EventHandler
    private void playerExit(PlayerQuitEvent event){

    }
}
