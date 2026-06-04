package org.psyrioty.magicModels.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.Animations.Animation;
import org.psyrioty.magicModels.Objects.Animations.AnimationController;
import org.psyrioty.magicModels.Objects.Animations.AnimationKey;
import org.psyrioty.magicModels.Objects.Animations.AnimationLine;
import org.psyrioty.magicModels.Objects.BBModel.Outliner;
import org.psyrioty.magicModels.Objects.Bone;
import org.psyrioty.magicModels.Objects.Model;
import org.psyrioty.magicModels.Objects.ResourcePack.Element;
import org.psyrioty.magicModels.Objects.ResourcePack.Group;
import org.psyrioty.magicModels.Objects.ResourcePack.Texture;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;
import static org.psyrioty.magicModels.utils.ResourcePackBuilder.*;

public class Converter {
    public static void ConvertBBModelsToResourcePackAndModels(){
        clearResourcePackFolder();

        createPackMcmeta();
        createAtlasFile();

        List<File> modelFiles = getAllBbModels();
        for(File modelFile: modelFiles){
            try {
                //############## создание модели #####################
                List<Bone> bones = getBones(modelFile); // создание костей

                List<Texture> textures = getTextureNames(modelFile);
                List<Element> elementList = getAllBBModelElements(modelFile, textures); //

                decodeTextures(modelFile);

                Model model = getModel(modelFile, bones, elementList, textures); //создание модели
                if(model == null){
                    continue;
                }



                AnimationController animationController = createAnimationController(modelFile, bones); //создание анимаций
                if(animationController != null) {
                    model.setAnimationController(animationController);
                }

                //####################################################
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicModels error Converter.java ConvertBBModelsToResourcePack() " + exception.getMessage());
            }
        }

        createItemFile();
        zipResourcePack();
    }

    private static List<Element> getAllBBModelElements(File modelFile, List<Texture> textures){
        try {
            List<Element> elements = new ArrayList<>();

            String jsonElements = getKeyValue(modelFile, "elements");
            List<JsonObject> jsonElementList = getObjects(jsonElements);

            String resolution = getKeyValue(modelFile, "resolution");
            int resolutionWidth = Integer.parseInt(getKeyValue(resolution, "width"));
            int resolutionHeight = Integer.parseInt(getKeyValue(resolution, "height"));

            for(JsonObject jsonElement: jsonElementList){
                if(!jsonElement.get("type").getAsString().equals("cube")){
                    continue;
                }

                String name = jsonElement.get("name").getAsString();
                name = name.replaceAll("\"", "");

                List<Float> from = new ArrayList<>();
                JsonArray jsonArrayFrom = jsonElement.get("from").getAsJsonArray();
                from.add(jsonArrayFrom.get(0).getAsFloat());
                from.add(jsonArrayFrom.get(1).getAsFloat());
                from.add(jsonArrayFrom.get(2).getAsFloat());

                List<Float> to = new ArrayList<>();
                JsonArray jsonArrayTo = jsonElement.get("to").getAsJsonArray();
                to.add(jsonArrayTo.get(0).getAsFloat());
                to.add(jsonArrayTo.get(1).getAsFloat());
                to.add(jsonArrayTo.get(2).getAsFloat());

                JsonArray jsonArrayOrigin = jsonElement.get("origin").getAsJsonArray();
                List<Float> origin = new ArrayList<>();
                origin.add(jsonArrayOrigin.get(0).getAsFloat());
                origin.add(jsonArrayOrigin.get(1).getAsFloat());
                origin.add(jsonArrayOrigin.get(2).getAsFloat());

                float rotationX = 0;
                float rotationY = 0;
                float rotationZ = 0;

                JsonElement rotationElement = jsonElement.get("rotation");

                if(rotationElement != null && rotationElement.isJsonArray()) {

                    JsonArray rotationArray = rotationElement.getAsJsonArray();

                    rotationX = rotationArray.get(0).getAsFloat();
                    rotationY = rotationArray.get(1).getAsFloat();
                    rotationZ = rotationArray.get(2).getAsFloat();
                }

                int northRotation = 0;
                int eastRotation = 0;
                int southRotation = 0;
                int westRotation = 0;
                int upRotation = 0;
                int downRotation = 0;

                JsonObject faces = jsonElement.get("faces").getAsJsonObject();
                List<Float> northFaces = new ArrayList<>();
                JsonObject northFaceJson = faces.get("north").getAsJsonObject();
                JsonArray jsonArrayNorth = northFaceJson.get("uv").getAsJsonArray();
                northFaces.add(jsonArrayNorth.get(0).getAsFloat());
                northFaces.add(jsonArrayNorth.get(1).getAsFloat());
                northFaces.add(jsonArrayNorth.get(2).getAsFloat());
                northFaces.add(jsonArrayNorth.get(3).getAsFloat());
                int textureIdNorth = northFaceJson.get("texture").getAsInt();
                String textureNameNorth = "#" + textureIdNorth;
                Texture textureNorth = textures.get(textureIdNorth);
                if(northFaceJson.has("rotation")){
                    northRotation = northFaceJson.get("rotation").getAsInt();
                }

                List<Float> eastFaces = new ArrayList<>();
                JsonObject eastFaceJson = faces.get("east").getAsJsonObject();
                JsonArray jsonArrayEast = eastFaceJson.get("uv").getAsJsonArray();
                eastFaces.add(jsonArrayEast.get(0).getAsFloat());
                eastFaces.add(jsonArrayEast.get(1).getAsFloat());
                eastFaces.add(jsonArrayEast.get(2).getAsFloat());
                eastFaces.add(jsonArrayEast.get(3).getAsFloat());
                int textureIdEast = eastFaceJson.get("texture").getAsInt();
                String textureNameEast = "#" + textureIdEast;
                Texture textureEast = textures.get(textureIdEast);
                if(eastFaceJson.has("rotation")){
                    eastRotation = eastFaceJson.get("rotation").getAsInt();
                }

                List<Float> southFaces = new ArrayList<>();
                JsonObject southFaceJson = faces.get("south").getAsJsonObject();
                JsonArray jsonArraySouth = southFaceJson.get("uv").getAsJsonArray();
                southFaces.add(jsonArraySouth.get(0).getAsFloat());
                southFaces.add(jsonArraySouth.get(1).getAsFloat());
                southFaces.add(jsonArraySouth.get(2).getAsFloat());
                southFaces.add(jsonArraySouth.get(3).getAsFloat());
                int textureIdSouth = southFaceJson.get("texture").getAsInt();
                String textureNameSouth = "#" + textureIdSouth;
                Texture textureSouth = textures.get(textureIdSouth);
                if(southFaceJson.has("rotation")){
                    southRotation = southFaceJson.get("rotation").getAsInt();
                }

                List<Float> westFaces = new ArrayList<>();
                JsonObject westFaceJson = faces.get("west").getAsJsonObject();
                JsonArray jsonArrayWest = westFaceJson.get("uv").getAsJsonArray();
                westFaces.add(jsonArrayWest.get(0).getAsFloat());
                westFaces.add(jsonArrayWest.get(1).getAsFloat());
                westFaces.add(jsonArrayWest.get(2).getAsFloat());
                westFaces.add(jsonArrayWest.get(3).getAsFloat());
                int textureIdWest = westFaceJson.get("texture").getAsInt();
                String textureNameWest = "#" + textureIdWest;
                Texture textureWest = textures.get(textureIdWest);
                if(westFaceJson.has("rotation")){
                    westRotation = westFaceJson.get("rotation").getAsInt();
                }

                List<Float> upFaces = new ArrayList<>();
                JsonObject upFaceJson = faces.get("up").getAsJsonObject();
                JsonArray jsonArrayUp = upFaceJson.get("uv").getAsJsonArray();
                upFaces.add(jsonArrayUp.get(0).getAsFloat());
                upFaces.add(jsonArrayUp.get(1).getAsFloat());
                upFaces.add(jsonArrayUp.get(2).getAsFloat());
                upFaces.add(jsonArrayUp.get(3).getAsFloat());
                int textureIdUp = upFaceJson.get("texture").getAsInt();
                String textureNameUp = "#" + textureIdUp;
                Texture textureUp = textures.get(textureIdUp);
                if(upFaceJson.has("rotation")){
                    upRotation = upFaceJson.get("rotation").getAsInt();
                }

                List<Float> downFaces = new ArrayList<>();
                JsonObject downFaceJson = faces.get("down").getAsJsonObject();
                JsonArray jsonArrayDown = downFaceJson.get("uv").getAsJsonArray();
                downFaces.add(jsonArrayDown.get(0).getAsFloat());
                downFaces.add(jsonArrayDown.get(1).getAsFloat());
                downFaces.add(jsonArrayDown.get(2).getAsFloat());
                downFaces.add(jsonArrayDown.get(3).getAsFloat());
                int textureIdDown = downFaceJson.get("texture").getAsInt();
                String textureNameDown = "#" + textureIdDown;
                Texture textureDown = textures.get(textureIdDown);
                if(downFaceJson.has("rotation")){
                    downRotation = downFaceJson.get("rotation").getAsInt();
                }

                //List<Float> rotationOrigin = new ArrayList<>();
                //rotationOrigin.add((float) 0);
                //rotationOrigin.add((float) 0);
                //rotationOrigin.add((float) 0);

                String uuidString = jsonElement.get("uuid").getAsString();
                UUID uuid = UUID.fromString(uuidString);

                Element element = new Element(
                        from,
                        to,

                        rotationX,
                        rotationY,
                        rotationZ,
                        origin,

                        northFaces,
                        eastFaces,
                        southFaces,
                        westFaces,
                        upFaces,
                        downFaces,

                        textureNameNorth,
                        textureNameEast,
                        textureNameSouth,
                        textureNameWest,
                        textureNameUp,
                        textureNameDown,

                        name,

                        uuid,

                        resolutionWidth,
                        resolutionHeight,

                        downRotation,
                        eastRotation,
                        northRotation,
                        southRotation,
                        upRotation,
                        westRotation,

                        textureNorth,
                        textureDown,
                        textureEast,
                        textureSouth,
                        textureUp,
                        textureWest
                );

                elements.add(element);
            }

            return elements;
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java getAllBBModelElements() " + exception.getMessage());
        }
        return null;
    }

    private static AnimationController createAnimationController(File modelFile, List<Bone> bones){
        try {
            List<Animation> animations = getAnimations(modelFile, bones);

            if(animations == null){
                return null;
            }

            AnimationController animationController = new AnimationController(
                    animations
            );

            return animationController;
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java createAnimationController() " + exception.getMessage());
        }
        return null;
    }

    private static List<Animation> getAnimations(File modelFile, List<Bone> bones){
        try {
            List<Animation> animationList = new ArrayList<>();
            String jsonAnimations = getKeyValue(modelFile, "animations");
            if(jsonAnimations == null){
                return null;
            }
            List<JsonObject> jsonAnimationList = getObjects(jsonAnimations);
            for(JsonObject jsonAnimation: jsonAnimationList){
                String name = getKeyValue(String.valueOf(jsonAnimation), "name");
                name = name.replace("\"", "");
                String uuidString = getKeyValue(String.valueOf(jsonAnimation), "uuid");
                uuidString = uuidString.replace("\"", "");
                UUID uuid = UUID.fromString(uuidString);
                int length = (int) (jsonAnimation.get("length").getAsFloat() * 20);

                String loopString = getKeyValue(String.valueOf(jsonAnimation), "loop");
                loopString = loopString.replaceAll("\"", "");
                boolean loop = false;
                if(loopString.equals("loop")){
                    loop = true;
                }

                List<AnimationLine> animationLines = getAnimationLines(jsonAnimation, bones);

                if(animationLines == null){
                    continue;
                }

                int weight = 0; //вес для анимаций

                switch(name){
                    case "idle":
                        weight = 99999;
                        break;
                    case "walk":
                        weight = 7;
                        break;
                    case "jump":
                        weight = 1;
                        break;
                    case "fly":
                        weight = 3;
                        break;
                    case "jump_end":
                        weight = 2;
                        break;
                    case "elytra":
                        weight = 4;
                        break;
                    case "swim":
                        weight = 5;
                        break;
                    case "crawl":
                        weight = 6;
                        break;
                }

                Animation animation = new Animation(
                        name,
                        uuid,
                        loop,
                        animationLines,
                        length,
                        weight
                );
                animationList.add(animation);
            }

            return animationList;
        }catch (Exception e){
            Bukkit.getLogger().severe("MagicModels error Converter.java getAnimations() " + e.getMessage());
        }
        return null;
    }

    private static List<AnimationLine> getAnimationLines(JsonObject jsonObject, List<Bone> bones){
        try {
            List<AnimationLine> animationLines = new ArrayList<>();
            String jsonAnimators = getKeyValue(jsonObject.toString(), "animators");
            Map<String, JsonObject> jsonAnimatorMap = getObjectsWithKeys(jsonAnimators);
            for(String uuidString: jsonAnimatorMap.keySet()){
                if(uuidString.equals("effects")){
                    continue;
                }

                JsonObject jsonObjectAnimator =  jsonAnimatorMap.get(uuidString);

                List<AnimationKey> animationKeys = getAnimationKeys(jsonObjectAnimator);
                if(animationKeys == null){
                    continue;
                }

                UUID uuid = UUID.fromString(uuidString);

                /*Bone bone = getBoneForUUID(UUID.fromString(uuidString), bones);

                if(bone == null){
                    continue;
                }*/

                AnimationLine animationLine = new AnimationLine(
                        uuid,
                        animationKeys
                );

                animationLines.add(animationLine);

            }
            return animationLines;
        }catch (Exception e){
            Bukkit.getLogger().severe("MagicModels error Converter.java getAnimationLines() " + e.getMessage());
        }
        return null;
    }

    private static Bone findBoneForUUID(UUID uuid, List<Bone> bones){
        if(uuid == null || bones == null){
            return null;
        }

        for(Bone bone: bones){
            if(bone.getUuid().equals(uuid)){
                return bone;
            }
        }

        return null;
    }

    private static List<AnimationKey> getAnimationKeys(JsonObject jsonObject){
        try {
            List<AnimationKey> animationKeys = new ArrayList<>();

            String keyframes = getKeyValue(String.valueOf(jsonObject), "keyframes");
            if(keyframes == null){
                return null;
            }
            List<JsonObject> keyframeList = getObjects(keyframes);
            for(JsonObject jsonKeyframe: keyframeList) {
                AnimationKey animationKey = getAnimationKey(jsonKeyframe);
                if(animationKey != null){
                    animationKeys.add(animationKey);
                }
            }

            return animationKeys;
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java getAnimationKeys() " + exception.getMessage());
        }
        return null;
    }

    private static AnimationKey getAnimationKey(JsonObject jsonObject){
        try {
            String channel = jsonObject.get("channel").getAsString();
            float timeFloat = jsonObject.get("time").getAsFloat();
            timeFloat = timeFloat * 20;
            int time = Math.round(timeFloat);


            String dataPointsString = getKeyValue(jsonObject.toString(), "data_points");
            if(dataPointsString == null){
                return null;
            }

            float x = 0;
            float y = 0;
            float z = 0;

            List<JsonObject> dataPoints = getObjects(dataPointsString);
            for(JsonObject dataPoint: dataPoints){
                x = dataPoint.get("x").getAsFloat();
                y = dataPoint.get("y").getAsFloat();
                z = dataPoint.get("z").getAsFloat();
            }

            if(
                    channel.equals("rotation")
                            || channel.equals("position")
            ){
                x *= -1;
                y *= 1;
                z *= -1;
            }

            return new AnimationKey(
                    time,
                    x,
                    y,
                    z,
                    channel
            );

            //{
            //    "channel": "rotation",
            //        "data_points": [
            //            {
            //                "x": "0",
            //                    "y": "-36.1726424309",
            //                    "z": "0"
            //            }
            //        ],
            //    "uuid": "fd1088ed-5003-a724-63d1-f0256daf55cb",
            //    "time": 0,
            //    "color": -1,
            //    "interpolation": "catmullrom"
            //}
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java getAnimationKey() " + exception.getMessage());
        }
        return null;
    }

    private static List<Bone> getBones(File modelFile){
        try {
            List<Bone> bones = new ArrayList<>();

            String jsonGroups = getKeyValue(modelFile, "groups");
            List<JsonObject> jsonGroupsList = getObjects(jsonGroups);
            for(JsonObject jsonGroup: jsonGroupsList){
                String name = getKeyValue(String.valueOf(jsonGroup), "name");
                name = name.replaceAll("\"", "");
                List<Float> origins = jsonToFloat(getKeyValue(String.valueOf(jsonGroup), "origin"));
                List<Float> rotations = jsonToFloat(getKeyValue(String.valueOf(jsonGroup), "rotation"));
                String uuidString = getKeyValue(String.valueOf(jsonGroup), "uuid");
                uuidString = uuidString.replace("\"", "");
                UUID uuid = UUID.fromString(uuidString);
                float scope = jsonGroup.get("scope").getAsFloat();
                float color = jsonGroup.get("color").getAsFloat();

                Group group = new Group(
                        name,
                        origins,
                        scope,
                        color
                );

                Bone bone = new Bone(
                        origins.get(0) / 16 * -1,
                        origins.get(1) / 16,
                        origins.get(2) / 16 * -1,

                        rotations.get(0) * -1,
                        rotations.get(1),
                        rotations.get(2) * -1,

                        name,

                        uuid,

                        group,

                        modelFile.getName().replace(".bbmodel", "")
                );

                bones.add(bone);
            }
            return bones;

        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error: Converter.java getBones() " + exception.getMessage());
        }
        return null;
    }

    private static Model getModel(File modelFile, List<Bone> bones, List<Element> elementList, List<Texture> textures){
        try {
            String jsonOutlines = getKeyValue(modelFile, "outliner");

            List<Outliner> allOutliners = getOutliners(jsonOutlines, new ArrayList<>(), elementList, bones, textures, modelFile);
            List<Bone> headBones = new ArrayList<>();
            setChildBones(bones, allOutliners, headBones);

            Model model = null;
            for (Model modelOld : MagicModels.getPlugin().getModels()) {
                if (modelOld.getName().equals(modelFile.getName().replace(".bbmodel", ""))) {
                    model = modelOld;
                    model.setHeadBones(headBones);
                    break;
                }
            }

            if (model == null){
                model = new Model(
                        headBones,
                        modelFile.getName().replace(".bbmodel", "")
                );
                MagicModels.getPlugin().getModels().add(model);
            }

            return model;

        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels Converter.java error getModel() " + exception.getMessage());
        }
        return null;
    }

    private static void setChildBones(List<Bone> bones, List<Outliner> outliners, List<Bone> headBones){
        try {
            for(Outliner outliner: outliners){
                Bone headBone = getBoneForUUID(outliner.getUuid(), bones);

                if(headBones != null){
                    headBones.add(headBone);
                }

                List<Outliner> childOutliners = outliner.getChildOutliners();
                if(childOutliners.isEmpty()){
                    continue;
                }
                for(Outliner outlinerChild: childOutliners){
                    Bone childBone = getBoneForUUID(outlinerChild.getUuid(), bones);
                    if(headBone != null) {
                        headBone.addChildBone(childBone);
                    }
                }

                setChildBones(bones, childOutliners, null);
            }
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java setChildBones() " + exception.getMessage());
        }
    }

    private static Bone getBoneForUUID(UUID uuid, List<Bone> bones){
        for(Bone bone: bones){
            if(bone.getUuid().equals(uuid)) {
                return bone;
            }
        }
        return null;
    }

    private static List<Element> getNeedElements(List<Element> elementList, List<UUID> cubeUUIDList){
        List<Element> needElements = new ArrayList<>();
        for(Element element: elementList){
            for(UUID uuid: cubeUUIDList) {
                if (element.getUuid().equals(uuid)){
                    needElements.add(element);
                }
            }
        }

        return needElements;
    }

    private static Group getNeedGroup(
            String uuidString,
            List<Bone> bones
    ){
        UUID uuid = UUID.fromString(uuidString);
        for(Bone bone: bones){
            if(bone.getUuid().equals(uuid)){
                return bone.getGroup();
            }
        }

        return null;
    }

    private static List<Texture> getTextureNames(File modelFile){
        try {
            List<Texture> textures = new ArrayList<>();

            String jsonTextures = getKeyValue(modelFile, "textures");
            List<JsonObject> jsonTextureList = getObjects(jsonTextures);

            for (JsonObject jsonTexture : jsonTextureList) {
                String name = jsonTexture.get("name").getAsString();
                int uv_height = jsonTexture.get("uv_height").getAsInt();
                int uv_width = jsonTexture.get("uv_width").getAsInt();

                int width = jsonTexture.get("width").getAsInt();
                int height = jsonTexture.get("height").getAsInt();

                Texture texture = new Texture(
                        name,
                        width,
                        height,
                        uv_width,
                        uv_height
                );

                textures.add(texture);
            }

            return textures;
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java getTextureNames() " + exception.getMessage());
        }

        return null;
    }

    private static List<Outliner> getOutliners(
            String jsonOutlines, List<Outliner> outliners, List<Element> elementList, List<Bone> bones,
            List<Texture> textures,
            File modelFile
    ){
        try {
            List<JsonObject> jsonOutlinersList = getObjects(jsonOutlines);
            for(JsonObject jsonOutliner: jsonOutlinersList){

                String uuidString = getKeyValue(jsonOutliner.toString(),"uuid");
                uuidString = uuidString.replace("\"","");
                UUID uuid = UUID.fromString(uuidString);

                //========================для ресурспака====================================
                Group group = getNeedGroup(
                        uuidString,
                        bones
                );

                boolean debug = false;

                List<UUID> cubeUUIDList = getCubeOutliners(jsonOutliner.get("children").toString());
                List<Element> needElements = getNeedElements(elementList, cubeUUIDList);


                createBoneJsonResourcePack(modelFile.getName().replaceAll(".bbmodel", ""), textures, needElements, group);
                //-------------------------------------------------------------------------

                Outliner outliner = new Outliner(uuid);
                outliners.add(outliner);

                String newJsonOutlines = getKeyValue(jsonOutliner.toString(), "children");
                if(newJsonOutlines == null){
                    continue;
                }
                List<Outliner> childOutlinersNew = new ArrayList<>();
                List<Outliner> childOutliners = getOutliners(newJsonOutlines, childOutlinersNew, elementList, bones, textures, modelFile);

                for(Outliner outlinerChild: childOutliners){
                    outliner.addChildOutliner(outlinerChild);
                }
            }
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java getOutliners() " + exception.getMessage());
        }
        return outliners;
    }

    private static List<File> getAllBbModels() {
        File modelsDir = new File(MagicModels.getPlugin().getDataFolder(), "models");

        if (!modelsDir.exists()) {
            modelsDir.mkdirs();
            return List.of();
        }

        try {
            return Files.walk(modelsDir.toPath())
                    .filter(Files::isRegularFile)
                    .map(path -> path.toFile())
                    .filter(file -> file.getName().endsWith(".bbmodel"))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            getLogger().severe("Failed to scan bbmodel files: " + e.getMessage());
            return List.of();
        }
    }

    //###################################################################################
    //###################################################################################
    //####################################РАБОТА JSON####################################
    //###################################################################################
    //###################################################################################
    private static List<Float> jsonToFloat(String json){
        if(json == null){
            return null;
        }

        String[] jsonSplit = json.split(",");
        List<Float> floats = new ArrayList<>();
        for(String floatString: jsonSplit){
            try {
                floatString = floatString.
                        replace("[", "").
                        replace("]", "").
                        replace("\"", "");
                floats.add(Float.valueOf(floatString));
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicModels error Converter.java jsonToFloat() " + exception.getMessage());
            }
        }

        return floats;
    }

    private static String getKeyValue(File bbmodelFile, String key) {
        try (FileReader reader = new FileReader(bbmodelFile)) {
            return getKeyValue(reader, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getKeyValue(String jsonString, String key) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            return getFromJson(json, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getKeyValue(Reader reader, String key) {
        try {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return getFromJson(json, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFromJson(JsonObject json, String key) {
        if (json.has(key)) {
            JsonElement element = json.get(key);
            return element.isJsonNull() ? null : element.toString();
        }
        return null;
    }

    private static List<JsonObject> getObjects(String jsonString) {
        List<JsonObject> list = new ArrayList<>();

        try {
            JsonElement root = JsonParser.parseString(jsonString);

            if (root.isJsonArray()) {
                for (JsonElement element : root.getAsJsonArray()) {
                    if (element.isJsonObject()) {
                        list.add(element.getAsJsonObject());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Map<String, JsonObject> getObjectsWithKeys(String jsonString) {
        Map<String, JsonObject> map = new LinkedHashMap<>();

        try {
            JsonElement root = JsonParser.parseString(jsonString);

            if (root.isJsonObject()) {
                JsonObject rootObject = root.getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
                    if (entry.getValue().isJsonObject()) {
                        map.put(entry.getKey(), entry.getValue().getAsJsonObject());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private static List<UUID> getCubeOutliners(String json){
        List<UUID> uuidList = new ArrayList<>();

        try {
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : array) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {

                    String uuidString = element.getAsString();
                    UUID uuid = UUID.fromString(uuidString);

                    uuidList.add(uuid);
                }
            }
        }catch (Exception exception){
            Bukkit.getLogger().severe("MagicModels error Converter.java getCubeElement() " + exception.getMessage());
        }

        return uuidList;
    }
    //###################################################################################
    //###################################################################################
    //####################################РАБОТА JSON КОНЕЦ##############################
    //###################################################################################
    //###################################################################################
}
