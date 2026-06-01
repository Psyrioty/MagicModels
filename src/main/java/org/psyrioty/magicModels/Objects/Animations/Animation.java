package org.psyrioty.magicModels.Objects.Animations;

import org.bukkit.entity.Entity;
import org.psyrioty.magicModels.Objects.Bone;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Animation {
    String name; //название анимации
    UUID uuid;
    List<AnimationLine> animationLines;
    boolean loop; //true - зациклен, false - запуск один раз
    int tick = 0; //тик хранит в себе данный тик анимации
    int length; //длина анимации
    boolean enable; //включена ли анимация

    int weight; //вес анимации, чем ниже, тем больше приоритет

    public Animation(
            String name,
            UUID uuid,
            boolean loop,
            List<AnimationLine> animationLines,
            int length,
            int weight
    ){
        this.name = name;
        this.uuid = uuid;
        this.loop = loop;
        this.animationLines = animationLines;
        this.length = length;

        this.weight = weight;

        enable = name.equals("idle");
    }

    public void setEnable(boolean enable) {
        if(this.enable != enable) {
            this.enable = enable;
        }
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void animationTick(List<Bone> bones, Entity target, ActiveEntity activeEntity, boolean headModel){
        for(AnimationLine animationLine: animationLines){
            animationLine.animationTick(tick, bones, target, activeEntity, headModel);
        }
        tick++;
        if(tick > length){
            tick = 0;
            if(!loop && enable){
                enable = false;
            }
        }
    }

    public int getWeight() {
        return weight;
    }

    public boolean isEnable() {
        return enable;
    }

    public Animation clone(){
        List<AnimationLine> animationLineList = new ArrayList<>();

        for(AnimationLine animationLine: animationLines){
            animationLineList.add(animationLine.clone());
        }

        Animation animation = new Animation(
                name,
                uuid,
                loop,
                animationLineList,
                length,
                weight
        );

        return animation;
    }
}