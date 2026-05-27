package org.psyrioty.magicModels;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.psyrioty.magicModels.Commands.MainPluginCommands;
import org.psyrioty.magicModels.Listeners.TargetEvents;
import org.psyrioty.magicModels.Objects.ActiveModel;
import org.psyrioty.magicModels.Objects.Animations.AnimationState;
import org.psyrioty.magicModels.Objects.Model;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;
import org.psyrioty.magicModels.utils.Converter;
import org.psyrioty.magicModels.utils.Tasker;

import java.util.*;

public final class MagicModels extends JavaPlugin {

    static MagicModels plugin;
    Set<Model> models = new HashSet<>();
    PluginManager pm;
    List<AnimationState> animationStateList = new ArrayList<>();
    Set<ActiveModel> activeModels = new HashSet<>();
    Set<ActiveEntity> activeEntities = new HashSet<>();

    static List<JsonObject> caseList = new ArrayList<>(); //для ресурспака

    Tasker tasker; //выполнитель задач, например функция update

    @Override
    public void onEnable() {
        plugin = this;
        pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new TargetEvents(), this);

        this.getCommand("magicmodels").setExecutor(new MainPluginCommands());

        //createDefaultAnimationStates();
        Converter.ConvertBBModelsToResourcePackAndModels();

        tasker = new Tasker();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static List<JsonObject> getCaseList() {
        return caseList;
    }

    private void createDefaultAnimationStates(){
        AnimationState idle = new AnimationState(
                "idle"
        );
        animationStateList.add(idle);

        AnimationState walk = new AnimationState(
                "walk"
        );
        animationStateList.add(walk);

        AnimationState jump = new AnimationState(
                "jump"
        );
        animationStateList.add(jump);

        AnimationState fly = new AnimationState(
                "fly"
        );
        animationStateList.add(fly);

        AnimationState endJump = new AnimationState(
                "end_jump"
        );
        animationStateList.add(endJump);

        AnimationState death = new AnimationState(
                "death"
        );
        animationStateList.add(death);
    }

    public Set<ActiveEntity> getActiveEntities() {
        return activeEntities;
    }


    public ActiveEntity findActiveEntity(Entity entity){
        for (ActiveEntity activeEntity: activeEntities){
            if(entity == activeEntity.getTarget()){
                return activeEntity;
            }
        }

        return null;
    }

    public ActiveEntity findActiveEntity(UUID uuid){
        for (ActiveEntity activeEntity: activeEntities){
            if(uuid.equals(activeEntity.getTarget().getUniqueId())){
                return activeEntity;
            }
        }

        return null;
    }

    public static MagicModels getPlugin() {
        return plugin;
    }

    public Set<Model> getModels() {
        return models;
    }

    public Set<ActiveModel> getActiveModels() {
        return activeModels;
    }

    public ActiveModel spawnModel(Entity entity, String id, HashMap<UUID, Integer> boneBrightness){
        for(Model model: models){
            if(model.getName().equals(id)){

                ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

                if(activeEntity == null){
                    activeEntity = new ActiveEntity(
                            entity
                    );
                }

                ActiveModel activeModel = new ActiveModel(
                        entity,
                        model,
                        activeEntity,
                        boneBrightness
                );

                activeEntity.addActiveModel(activeModel);

                MagicModels.getPlugin().getActiveModels().add(activeModel);
                MagicModels.getPlugin().getActiveEntities().add(activeEntity);

                return activeModel;
            }
        }

        return null;
    }

    public ActiveModel spawnModel(
            Entity entity,
            Model model,
            HashMap<UUID, Integer> boneBrightness
    ){
        try {
            ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

            if(activeEntity == null){
                activeEntity = new ActiveEntity(
                        entity
                );
            }

            ActiveModel activeModel = new ActiveModel(
                    entity,
                    model,
                    activeEntity,
                    boneBrightness
            );

            activeEntity.addActiveModel(activeModel);

            MagicModels.getPlugin().getActiveModels().add(activeModel);
            MagicModels.getPlugin().getActiveEntities().add(activeEntity);

            return activeModel;

        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels MagicModels.java spawnModel() error " + exception.getMessage());
        }
        return null;
    }

    public ActiveModel spawnModel(
            Entity entity,
            Model model,
            HashMap<UUID, Integer> boneBrightness,
            float scale,
            float offsetX,
            float offsetY,
            float offsetZ
    ){
        try {
            ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(entity);

            if(activeEntity == null){
                activeEntity = new ActiveEntity(
                        entity
                );
            }

            ActiveModel activeModel = new ActiveModel(
                    entity,
                    model,
                    activeEntity,
                    boneBrightness,
                    scale,
                    offsetX,
                    offsetY,
                    offsetZ
            );

            activeEntity.addActiveModel(activeModel);

            MagicModels.getPlugin().getActiveModels().add(activeModel);
            MagicModels.getPlugin().getActiveEntities().add(activeEntity);

            return activeModel;

        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels MagicModels.java spawnModel() error " + exception.getMessage());
        }
        return null;
    }
}
