package org.psyrioty.magicModels.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.Animations.AnimationController;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;
import org.psyrioty.magicModels.Objects.Animations.Animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ActiveModel {
    Entity target;
    List<Bone> headBones = new ArrayList<>();
    AnimationController animationController;
    ActiveEntity activeEntity;

    public ActiveModel(
            Entity target,
            Model model,
            ActiveEntity activeEntity,
            HashMap<UUID, Integer> boneBrightness
    ){
        this.target = target;
        getHeadBones(model.getHeadBones(), null);
        this.animationController = model.getAnimationController().clone();


        this.activeEntity = activeEntity;

        Spawn(boneBrightness);

        MagicModels.getPlugin().getActiveModels().add(this);
    }

    public ActiveModel(
            Entity target,
            Model model,
            ActiveEntity activeEntity,

            HashMap<UUID, Integer> boneBrightness,
            float scale,
            float offsetX,
            float offsetY,
            float offsetZ
    ){
        this.target = target;
        getHeadBones(model.getHeadBones(), null);
        this.animationController = model.getAnimationController().clone();


        this.activeEntity = activeEntity;

        Spawn(
                boneBrightness,
                scale,
                offsetX,
                offsetY,
                offsetZ
        );

        MagicModels.getPlugin().getActiveModels().add(this);
    }

    public void remove(){
        MagicModels.getPlugin().getActiveModels().remove(this);
        for(Bone bone: headBones){
            bone.remove();
            removeChildBones(bone);
        }
    }

    private void removeChildBones(Bone bone){
        List<Bone> bones = bone.getChildBones();

        if(bones == null){
            return;
        }

        for(Bone boneChild: bones){
            boneChild.remove();
            removeChildBones(boneChild);
        }
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

    private void Spawn(HashMap<UUID, Integer> boneBrightness){
        spawnBone(
                null,
                headBones,
                boneBrightness,
                1,
                0,
                0,
                0
        );
    }

    private void Spawn(
            HashMap<UUID, Integer> boneBrightness,
            float scale,
            float offsetX,
            float offsetY,
            float offsetZ
    ){
        spawnBone(
                null,
                headBones,
                boneBrightness,
                scale,
                offsetX,
                offsetY,
                offsetZ
        );
    }

    private void spawnBone(
            Bone headBone,
            List<Bone> bones,
            HashMap<UUID, Integer> boneBrightness,
            float scale,
            float offsetX,
            float offsetY,
            float offsetZ
    ){
        try {
            for(Bone bone: bones){
                int brightness = -1;

                if(boneBrightness != null) {
                    for (UUID uuid : boneBrightness.keySet()) {
                        if (bone.getUuid().equals(uuid)) {
                            brightness = boneBrightness.get(uuid);
                        }
                    }
                }

                if(headBone == null){
                    bone.createBoneEntity(
                            target,
                            brightness,
                            scale,
                            offsetX,
                            offsetY,
                            offsetZ
                    );
                }else{
                    bone.createBoneEntity(
                            headBone.getBoneEntity(),
                            brightness,
                            scale,
                            offsetX,
                            offsetY,
                            offsetZ
                    );
                }
                List<Bone> boneList = bone.getChildBones();
                if(boneList.isEmpty()){
                    continue;
                }
                spawnBone(
                        bone,
                        boneList,
                        boneBrightness,
                        scale,
                        offsetX,
                        offsetY,
                        offsetZ
                );
            }
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error ActiveModel.java spawnBone() " + exception.getMessage());
        }
    }

    public void animationTick(){
        checkTarget();
        checkHide();

        animationController.animationTick(headBones, target, activeEntity);

        clearNewOriginBones(headBones);
    }

    private void checkHide(){
        LivingEntity livingEntity = (LivingEntity) target;

        boolean isSpectator = false;
        if(target instanceof Player player){
            if(player.getGameMode() == GameMode.SPECTATOR){
                isSpectator = true;
            }
        }

        boolean isHided = false;
        if(
                livingEntity.isGliding() ||
                livingEntity.isSwimming()
        ) {
            hideAllBones(true);
            isHided = true;
        }

        if(
                livingEntity.hasPotionEffect(PotionEffectType.INVISIBILITY) ||
                livingEntity.isSneaking() ||
                isSpectator
        ){
            if(!isHided) {
                if (
                        livingEntity.isGliding() ||
                                livingEntity.isSwimming()
                ) {
                    hideAllBones(true);
                } else {
                    hideAllBones(false);
                }
            }
        }else{
            if(
                    !livingEntity.isGliding() &&
                    !livingEntity.isSwimming()
            ) {
                showAllBones();
            }
        }
    }

    private void showAllBones(){
        for(Player player: Bukkit.getOnlinePlayers()){
            for(Bone bone: headBones) {
                if(!player.canSee(bone.getBoneEntity())){
                    Bukkit.getScheduler().runTask(MagicModels.getPlugin(), () -> {
                        player.showEntity(MagicModels.getPlugin(), bone.getBoneEntity());
                    });
                    showChildBones(bone.getChildBones(), player);
                }
            }
        }
    }

    private void showChildBones(List<Bone> bones, Player player){
        if(bones == null){
            return;
        }
        if(bones.isEmpty()){
            return;
        }

        for(Bone bone: bones) {
            Bukkit.getScheduler().runTask(MagicModels.getPlugin(), () -> {
                        player.showEntity(MagicModels.getPlugin(), bone.getBoneEntity());
                    });
            showChildBones(bone.getChildBones(), player);
        }
    }

    private void hideAllBones(boolean isOtherPose){
        for(Player player: Bukkit.getOnlinePlayers()){
            if(player == target && !isOtherPose){
                continue;
            }

            for(Bone bone: headBones) {
                if(
                        player.canSee(bone.getBoneEntity())
                ){
                    Bukkit.getScheduler().runTask(MagicModels.getPlugin(), () -> {
                                player.hideEntity(MagicModels.getPlugin(), bone.getBoneEntity());
                            });
                    hideChildBones(bone.getChildBones(), player);
                }
            }
        }
    }

    private void hideChildBones(List<Bone> bones, Player player){
        if(bones == null){
            return;
        }
        if(bones.isEmpty()){
            return;
        }

        for(Bone bone: bones) {
            Bukkit.getScheduler().runTask(MagicModels.getPlugin(), () -> {
                        player.hideEntity(MagicModels.getPlugin(), bone.getBoneEntity());
                    });
            hideChildBones(bone.getChildBones(), player);
        }
    }

    private void checkTarget(){
        for(Bone bone: headBones){
            ItemDisplay boneEntity = bone.getBoneEntity();
            if(boneEntity.getVehicle() != null){
                continue;
            }

            if(target instanceof Player player){
                if(player.isOnline()){
                    syncAddPassenger(target, boneEntity);
                }
            }else{
                if(!target.isDead()){
                    syncAddPassenger(target, boneEntity);
                }
            }
        }
    }

    private void syncAddPassenger(Entity target, Entity passenger){
        Bukkit.getScheduler().runTask(MagicModels.getPlugin(), () -> {
            Location location = target.getLocation();
            location.setPitch(0);
            location.setYaw(0);
            passenger.teleport(location);
            target.addPassenger(passenger);
        });
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