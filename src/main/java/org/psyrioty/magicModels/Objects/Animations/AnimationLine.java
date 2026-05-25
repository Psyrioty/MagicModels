package org.psyrioty.magicModels.Objects.Animations;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.psyrioty.magicModels.Objects.Bone;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AnimationLine {
    private List<AnimationKey> rotationKeys = new ArrayList<>();
    private List<AnimationKey> scaleKeys = new ArrayList<>();
    private List<AnimationKey> translateKeys = new ArrayList<>();
    private final UUID uuidBone;

    public AnimationLine(UUID uuidBone, List<AnimationKey> animationKeys) {
        this.uuidBone = uuidBone;

        for (AnimationKey animationKey : animationKeys) {
            switch (animationKey.getTypeKey()) {
                case "rotation" -> rotationKeys.add(animationKey);
                case "position" -> translateKeys.add(animationKey);
                case "scale" -> scaleKeys.add(animationKey);
            }
        }
    }

    public AnimationLine(
            UUID uuidBone,
            List<AnimationKey> rotationKeys,
            List<AnimationKey> scaleKeys,
            List<AnimationKey> translateKeys
    ){
        this.uuidBone = uuidBone;

        this.rotationKeys = rotationKeys;
        this.scaleKeys = scaleKeys;
        this.translateKeys = translateKeys;
    }

    public void animationTick(int tick, List<Bone> bones, Entity target, ActiveEntity activeEntity) {
        Bone animatedBone = getNeedBone(bones);
        if (animatedBone == null) {
            return;
        }

        scaleTick(animatedBone, tick);
        rotationTick(animatedBone, tick);
        translateTick(animatedBone, tick);

        Bone root = animatedBone;
        while (root.getHeadBone() != null) {
            root = root.getHeadBone();
        }

        float targetYawRadians = (float) Math.toRadians(-target.getLocation().getYaw());
        Quaternionf targetRotation = new Quaternionf().rotateY(targetYawRadians);

        if(target instanceof Player player){
            if(player.isGliding() || player.isSwimming()){
                float targetPitchRadians = (float) Math.toRadians(player.getLocation().getPitch() + 90);
                targetRotation.rotateX(targetPitchRadians);
            }
        }

        applyBoneRecursive(
                root,
                new Vector3f(0, 0, 0),
                targetRotation,
                new Vector3f(2f, 2f, 2f),
                new Vector3f(0, 0, 0),
                activeEntity
        );
    }

    private void applyBoneRecursive(
            Bone bone,
            Vector3f parentWorldPos,
            Quaternionf parentWorldRot,
            Vector3f parentWorldScale,
            Vector3f parentBindOrigin,
            ActiveEntity activeEntity
    ) {
        if (bone == null || bone.getBoneEntity() == null) {
            return;
        }

        Vector3f bindOrigin;

        if(bone.getHeadBone() == null) {
            bindOrigin = new Vector3f(
                    bone.getOriginX(),
                    (float) (bone.getOriginY() + activeEntity.getAddOffsetY()),
                    bone.getOriginZ()
            );
        }else{
            bindOrigin = new Vector3f(
                    bone.getOriginX(),
                    bone.getOriginY(),
                    bone.getOriginZ()
            );
        }

        Quaternionf bindRotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(bone.getRotationZ()))
                .rotateY((float) Math.toRadians(bone.getRotationY()))
                .rotateX((float) Math.toRadians(bone.getRotationX()));

        Quaternionf animRotation = new Quaternionf()
                .rotateZ((float) Math.toRadians(bone.getAnimRotationZ()))
                .rotateY((float) Math.toRadians(bone.getAnimRotationY()))
                .rotateX((float) Math.toRadians(bone.getAnimRotationX()));

        Quaternionf localRotation = new Quaternionf(animRotation).mul(bindRotation);
        Quaternionf worldRotation = new Quaternionf(parentWorldRot).mul(localRotation);

        float animScaleX = bone.getAnimScaleX() == 0 ? 1 : bone.getAnimScaleX();
        float animScaleY = bone.getAnimScaleY() == 0 ? 1 : bone.getAnimScaleY();
        float animScaleZ = bone.getAnimScaleZ() == 0 ? 1 : bone.getAnimScaleZ();

        Vector3f localScale = new Vector3f(animScaleX, animScaleY, animScaleZ);

        Vector3f worldScale = new Vector3f(parentWorldScale).mul(localScale);

        Vector3f localOffset = new Vector3f(bindOrigin)
                .sub(parentBindOrigin)
                .add(
                        bone.getAnimPositionX(),
                        bone.getAnimPositionY(),
                        bone.getAnimPositionZ()
                )
                .mul(new Vector3f(parentWorldScale).div(2.0f));

        Vector3f worldPos = new Vector3f(localOffset)
                .rotate(parentWorldRot)
                .add(parentWorldPos);

        ItemDisplay display = bone.getBoneEntity();
        Transformation old = display.getTransformation();

        display.setTransformation(new Transformation(
                worldPos,
                worldRotation,
                worldScale,
                old.getRightRotation()
        ));

        for (Bone child : bone.getChildBones()) {
            applyBoneRecursive(child, worldPos, worldRotation, worldScale, bindOrigin, activeEntity);
        }
    }

    private Bone getNeedBone(List<Bone> bones) {
        try {
            for (Bone bone : bones) {
                if (bone.getUuid().equals(uuidBone)) {
                    return bone;
                }

                List<Bone> childBones = bone.getChildBones();
                if (childBones == null || childBones.isEmpty()) {
                    continue;
                }

                Bone boneNeed = getNeedBone(childBones);
                if (boneNeed != null) {
                    return boneNeed;
                }
            }
        } catch (Exception exception) {
            Bukkit.getLogger().severe("MagicModel error AnimationLine.java getNeedBone() " + exception.getMessage());
        }

        return null;
    }

    private void rotationTick(Bone bone, int tick) {
        List<AnimationKey> animationKeys = getCurrentGapBetweenKeys(rotationKeys, tick);

        float x = 0;
        float y = 0;
        float z = 0;

        if (animationKeys != null && !animationKeys.isEmpty()) {
            x = mathValue(
                    animationKeys.getFirst().getX(),
                    animationKeys.getLast().getX(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );

            y = mathValue(
                    animationKeys.getFirst().getY(),
                    animationKeys.getLast().getY(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );

            z = mathValue(
                    animationKeys.getFirst().getZ(),
                    animationKeys.getLast().getZ(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );
        }

        //if(bone.getHeadBone() == null){
        //    y -= target.getYaw();
        //}
        bone.setAnimRotation(x, y, z);
    }


    private void scaleTick(Bone bone, int tick) {
        List<AnimationKey> animationKeys = getCurrentGapBetweenKeys(scaleKeys, tick);

        float x = 1;
        float y = 1;
        float z = 1;

        if (animationKeys != null && !animationKeys.isEmpty()) {
            x = mathValue(
                    animationKeys.getFirst().getX(),
                    animationKeys.getLast().getX(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );

            y = mathValue(
                    animationKeys.getFirst().getY(),
                    animationKeys.getLast().getY(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );

            z = mathValue(
                    animationKeys.getFirst().getZ(),
                    animationKeys.getLast().getZ(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );
        }

        bone.setAnimScale(x, y, z);
    }

    private void translateTick(Bone bone, int tick) {
        List<AnimationKey> animationKeys = getCurrentGapBetweenKeys(translateKeys, tick);

        float x = 0;
        float y = 0;
        float z = 0;

        if (animationKeys != null && !animationKeys.isEmpty()) {
            x = mathValue(
                    animationKeys.getFirst().getX(),
                    animationKeys.getLast().getX(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );

            y = mathValue(
                    animationKeys.getFirst().getY(),
                    animationKeys.getLast().getY(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );

            z = mathValue(
                    animationKeys.getFirst().getZ(),
                    animationKeys.getLast().getZ(),
                    tick,
                    animationKeys.getFirst().getTick(),
                    animationKeys.getLast().getTick()
            );
        }

        bone.setAnimPosition(x, y, z);
    }

    private List<AnimationKey> getCurrentGapBetweenKeys(List<AnimationKey> animationKeys, int tick) {
        AnimationKey oldKey = null;
        AnimationKey nextKey = null;

        for (AnimationKey animationKey : animationKeys) {
            if (animationKey.getTick() <= tick) {
                if (oldKey == null || oldKey.getTick() < animationKey.getTick()) {
                    oldKey = animationKey;
                }
            }

            if (animationKey.getTick() >= tick) {
                if (nextKey == null || nextKey.getTick() > animationKey.getTick()) {
                    nextKey = animationKey;
                }
            }
        }

        if (oldKey == null || nextKey == null) {
            return null;
        }

        List<AnimationKey> animationKeyList = new ArrayList<>(2);
        animationKeyList.add(oldKey);
        animationKeyList.add(nextKey);
        return animationKeyList;
    }

    private float mathValue(float oldKeyValue, float nextKeyValue, int tick, int oldKeyTick, int nextKeyTick) {
        int differenceTick = nextKeyTick - oldKeyTick;
        float differenceValue = nextKeyValue - oldKeyValue;

        float stepValueInTick = 0;
        if (differenceTick != 0) {
            stepValueInTick = differenceValue / differenceTick;
        }

        int actualityTick = tick - oldKeyTick;
        return oldKeyValue + (actualityTick * stepValueInTick);
    }

    public AnimationLine clone(){
        List<AnimationKey> rotationKeysNew = new ArrayList<>();
        List<AnimationKey> translateKeysNew = new ArrayList<>();
        List<AnimationKey> scaleKeysNew = new ArrayList<>();

        for(AnimationKey animationKey: rotationKeys){
            rotationKeysNew.add(animationKey.clone());
        }

        for(AnimationKey animationKey: translateKeys){
            translateKeysNew.add(animationKey.clone());
        }

        for(AnimationKey animationKey: scaleKeys){
            scaleKeysNew.add(animationKey.clone());
        }

        AnimationLine animationLine = new AnimationLine(
                uuidBone,
                rotationKeysNew,
                scaleKeysNew,
                translateKeysNew
        );

        return animationLine;
    }
}