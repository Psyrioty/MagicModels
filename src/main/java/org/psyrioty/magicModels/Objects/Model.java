package org.psyrioty.magicModels.Objects;

import org.psyrioty.magicModels.Objects.Animations.AnimationController;

import java.util.List;

public class Model {
    float scale;
    List<Bone> headBones;
    String name;
    AnimationController animationController;

    public Model(
            List<Bone> headBones,
            String name
    ){
        this.headBones = headBones;
        this.name = name;
    }

    public List<Bone> getHeadBones() {
        return headBones;
    }

    public String getName() {
        return name;
    }

    public void setAnimationController(AnimationController animationController) {
        this.animationController = animationController;
    }

    public void setHeadBones(List<Bone> headBones) {
        this.headBones = headBones;
    }

    public AnimationController getAnimationController() {
        return animationController;
    }
}
