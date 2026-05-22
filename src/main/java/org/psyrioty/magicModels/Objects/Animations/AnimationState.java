package org.psyrioty.magicModels.Objects.Animations;

import org.bukkit.event.EventHandler;

import java.util.List;

//хуйнюшка для переходов анимаций, нужно для масштабируемости
public class AnimationState {
    //Animation animation; //анимация стэйта
    String name; //должно быть равным названию анимации
    //List<EventHandler> eventHandlers; //события, которые вызовут анимацию
    //List<Condition> conditions; //условия, которые вызовут анимацию
    boolean enable = false; //включена ли анимация

    public AnimationState(
            String name
    ){
        this.name = name;
        //this.eventHandlers = eventHandlers;
        //this.conditions = conditions;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }
}
