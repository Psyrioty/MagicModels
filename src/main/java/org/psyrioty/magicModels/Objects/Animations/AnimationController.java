package org.psyrioty.magicModels.Objects.Animations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.psyrioty.magicModels.Objects.ActiveModel;
import org.psyrioty.magicModels.Objects.Bone;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class AnimationController {
    //List<AnimationState> animationStates;

    List<Animation> animations;

    public AnimationController(List<Animation> animations){
        //this.animations = animations;


        this.animations = animations.stream()
                .sorted(Comparator.comparingInt(Animation::getWeight))
                .toList();
    }

    public void animationTick(List<Bone> bones, Entity target, ActiveEntity activeEntity){
        for(Animation animation: animations){
            if(animation.isEnable()){
                animation.animationTick(bones, target, activeEntity);
                break;
            }
        }
    }

    private void walk(ActiveEntity activeEntity, Entity entity){

        Vector velocity = entity.getVelocity();
        Bukkit.getLogger().info(velocity.toString());

        Location location = entity.getLocation();
        boolean moving = activeEntity.getX() != location.getX() ||
                activeEntity.getZ() != location.getZ();

        activeEntity.setLocation(
                location.getWorld(),
                location.getX(),
                location.getY(),
                location.getZ()
        );

        for (ActiveModel activeModel : activeEntity.getActiveModels()) {
            for (Animation animation : activeModel.getAnimationController().getAnimations()) {
                if(animation.getName().equals("walk")){
                    animation.setEnable(moving);
                }
            }
        }
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public AnimationController clone(){
        List<Animation> animationList = new ArrayList<>();
        for(Animation animation: animations){
            animationList.add(animation.clone());
        }

        AnimationController animationController = new AnimationController(animationList);
        return animationController;
    }
}
