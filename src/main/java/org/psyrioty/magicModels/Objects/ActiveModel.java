package org.psyrioty.magicModels.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.Animations.AnimationController;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;
import org.psyrioty.magicModels.Objects.Animations.Animation;

import java.util.ArrayList;
import java.util.List;

public class ActiveModel {
    Entity target;
    List<Bone> headBones = new ArrayList<>();
    AnimationController animationController;
    ActiveEntity activeEntity;

    public ActiveModel(
            Entity target,
            Model model,
            ActiveEntity activeEntity
    ){
        this.target = target;
        getHeadBones(model.getHeadBones(), null);
        this.animationController = model.getAnimationController().clone();


        this.activeEntity = activeEntity;

        Spawn();

        MagicModels.getPlugin().getActiveModels().add(this);
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    private void getHeadBones(List<Bone> bones, Bone headBone){
        try {
            for(Bone bone: bones){
                Bone newHeadBone = bone.clone();
                if(headBone == null){
                    headBones.add(newHeadBone);
                }else{
                    headBone.addChildBone(newHeadBone);
                    newHeadBone.setHeadBone(headBone);
                }
                List<Bone> boneList = bone.getChildBones();
                if(boneList.isEmpty()){
                    continue;
                }
                Bukkit.getLogger().info(newHeadBone.getName() + " " + boneList);
                getHeadBones(boneList, newHeadBone);
            }
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error ActiveModel.java getHeadBones() " + exception.getMessage());
        }
    }

    private void Spawn(){
        spawnBone(null, headBones);
    }

    private void spawnBone(Bone headBone, List<Bone> bones){
        try {
            for(Bone bone: bones){
                if(headBone == null){
                    bone.createBoneEntity(target);
                }else{
                    bone.createBoneEntity(headBone.getBoneEntity());
                }
                List<Bone> boneList = bone.getChildBones();
                if(boneList.isEmpty()){
                    continue;
                }
                spawnBone(bone, boneList);
            }
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error ActiveModel.java spawnBone() " + exception.getMessage());
        }
    }

    public void animationTick(){
        animationController.animationTick(headBones, target);

        clearNewOriginBones(headBones);
    }

    private void clearNewOriginBones(List<Bone> bones){
        if(bones == null){
            return;
        }

        for(Bone bone: bones){
            bone.setNewOrigin(null);
            clearNewOriginBones(bone.getChildBones());
        }

    }
}