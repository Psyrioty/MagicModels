package org.psyrioty.magicModels.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.ActiveModel;
import org.psyrioty.magicModels.Objects.Model;
import org.psyrioty.magicModels.Objects.Target.ActiveEntity;

import java.util.ArrayList;
import java.util.Set;

public class MainPluginCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Entity player)){
            return true;
        }
        if(args.length == 0){
            return true;
        }
        Set<Model> modelSet = MagicModels.getPlugin().getModels();
        for(Model model: modelSet){
            if(model.getName().equals(args[0])){

                ActiveEntity activeEntity = MagicModels.getPlugin().findActiveEntity(player);

                if(activeEntity == null){
                    activeEntity = new ActiveEntity(
                            player
                    );
                }

                ActiveModel activeModel = new ActiveModel(
                        player,
                        model,
                        activeEntity
                );

                activeEntity.addActiveModel(activeModel);

                MagicModels.getPlugin().getActiveModels().add(activeModel);
                MagicModels.getPlugin().getActiveEntities().add(activeEntity);
                return true;
            }
        }

        return true;
    }
}
