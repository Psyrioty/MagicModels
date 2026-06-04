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
import org.psyrioty.magicModels.utils.Converter;

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

        if(args.length == 1) {
            if (args[0].equals("reload")) {
                Converter.ConvertBBModelsToResourcePackAndModels();
                return true;
            }
        }

        MagicModels.getPlugin().spawnModel(player, args[0], null);


        return true;
    }
}
