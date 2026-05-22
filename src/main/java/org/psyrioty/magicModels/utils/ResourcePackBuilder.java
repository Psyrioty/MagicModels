package org.psyrioty.magicModels.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.psyrioty.magicModels.MagicModels;
import org.psyrioty.magicModels.Objects.ResourcePack.Element;
import org.psyrioty.magicModels.Objects.ResourcePack.Group;
import org.psyrioty.magicModels.Objects.ResourcePack.Texture;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackBuilder {
    static List<JsonObject> caseList = new ArrayList<>();

    public static void createPackMcmeta() {
        try {
            JsonObject root = new JsonObject();

            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 15);
            pack.addProperty("min_format", 15);
            pack.addProperty("max_format", 32767);

            JsonArray supportedFormats = new JsonArray();
            supportedFormats.add(15);
            supportedFormats.add(32767);

            pack.add("supported_formats", supportedFormats);
            pack.addProperty("description", "Created by Psyrioty");

            root.add("pack", pack);

            Path path = MagicModels.getPlugin()
                    .getDataFolder()
                    .toPath()
                    .resolve("resourcepack")
                    .resolve("pack.mcmeta");

            Files.createDirectories(path.getParent());

            String json = new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(root);

            Files.writeString(
                    path,
                    json,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            Bukkit.getLogger().info("Created pack.mcmeta");

        } catch (Exception e) {
            Bukkit.getLogger().severe(
                    "createPackMcmeta error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    public static void createBoneJsonResourcePack(
            String name,
            List<Texture> textureList,
            List<Element> elements,
            Group group
    ) {
        JsonObject root = new JsonObject();

        JsonObject textures = new JsonObject();
        if (textureList != null && !textureList.isEmpty()) {
            int texturesIterator = 0;
            for (Texture texture : textureList) {
                textures.addProperty(String.valueOf(texturesIterator), "magicmodels:models/" + name + "/" + texture.getName().replace(".png", ""));
                texturesIterator++;
            }
            textures.addProperty("particle", "magicmodels:models/" + name + "/" + textureList.getFirst().getName().replace(".png", ""));
        }
        root.add("textures", textures);

        JsonArray elementsJson = new JsonArray();
        //JsonArray children = new JsonArray();

        //int elementIterator = 0;
        for (Element element : elements) {
            JsonObject jsonElement = new JsonObject();

            List<Float> newVectorFrom = mathVec3(element.getFrom(), group.getOrigin(), element.getPivotPoint());
            jsonElement.add("from", vec3(newVectorFrom));

            List<Float> newVectorTo = mathVec3(element.getTo(), group.getOrigin(), element.getPivotPoint());
            jsonElement.add("to", vec3(newVectorTo));

            JsonObject rotationObject = new JsonObject();

            int isCoordinate = isOneCoordinate(
                    element.getRotationX(),
                    element.getRotationY(),
                    element.getRotationZ()
            );

            if(isCoordinate == 0){
                rotationObject.addProperty("angle", 0);
                rotationObject.addProperty("axis", "y");
            }else if(isCoordinate == -1){
                rotationObject.addProperty("x", element.getRotationX());
                rotationObject.addProperty("y", element.getRotationY());
                rotationObject.addProperty("z", element.getRotationZ());
            }else{
                if(isCoordinate == 1){
                    rotationObject.addProperty("angle", element.getRotationX());
                    rotationObject.addProperty("axis", "x");
                }else if(isCoordinate == 2){
                    rotationObject.addProperty("angle", element.getRotationY());
                    rotationObject.addProperty("axis", "y");
                }else if(isCoordinate == 3){
                    rotationObject.addProperty("angle", element.getRotationZ());
                    rotationObject.addProperty("axis", "z");
                }
            }

            //if (element.getRotationX() == 0
            //        && element.getRotationY() == 0
            //        && element.getRotationZ() == 0) {
            //} else {
            //}

            //List<Float> newRotationOrigin = mathVec3(element.getRotationOrigin(), group.getOrigin());
            //rotationObject.add("origin", vec3(newRotationOrigin));
            rotationObject.add("origin", vec3(mathVec3(element.getRotationOrigin(), group.getOrigin(), element.getPivotPoint())));
            jsonElement.add("rotation", rotationObject);

            JsonObject faces = new JsonObject();
            faces.add(
                    "north",
                    face(
                            element.getNorthFaces(),
                            element.getNorthTextureName(),
                            element.getNorthTexture().getUv_width(),
                            element.getNorthTexture().getUv_height(),
                            element.getNorthRotation()
                    )
            );
            faces.add(
                    "east",
                    face(
                            element.getEastFaces(),
                            element.getEastTextureName(),
                            element.getEastTexture().getUv_width(),
                            element.getEastTexture().getUv_height(),
                            element.getEastRotation()
                    )
            );
            faces.add(
                    "south",
                    face(
                            element.getSouthFaces(),
                            element.getSouthTextureName(),
                            element.getSouthTexture().getUv_width(),
                            element.getSouthTexture().getUv_height(),
                            element.getSouthRotation()
                    )
            );
            faces.add(
                    "west",
                    face(
                            element.getWestFaces(),
                            element.getWestTextureName(),
                            element.getWestTexture().getUv_width(),
                            element.getWestTexture().getUv_height(),
                            element.getWestRotation()
                    )
            );
            faces.add(
                    "up",
                    face(
                            element.getUpFaces(),
                            element.getUpTextureName(),
                            element.getUpTexture().getUv_width(),
                            element.getUpTexture().getUv_height(),
                            element.getUpRotation()
                    )
            );
            faces.add(
                    "down",
                    face(
                            element.getDownFaces(),
                            element.getDownTextureName(),
                            element.getDownTexture().getUv_width(),
                            element.getDownTexture().getUv_height(),
                            element.getDownRotation()
                    )
            );
            jsonElement.add("faces", faces);

            //children.add(elementIterator++);
            elementsJson.add(jsonElement);
        }

        root.add("elements", elementsJson);

        /*JsonArray groups = new JsonArray();
        JsonObject groupsJson = new JsonObject();
        groupsJson.addProperty("name", group.getName());
        groupsJson.add("origin", vec3(group.getOrigin()));
        groupsJson.addProperty("scope", group.getScope());
        groupsJson.addProperty("color", group.getColor());
        groupsJson.add("children", children);
        groups.add(groupsJson);

        root.add("groups", groups);*/

        String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);

        try {
            Path baseDir = MagicModels.getPlugin().getDataFolder()
                    .toPath()
                    .resolve("resourcepack")
                    .resolve("assets")
                    .resolve("magicmodels")
                    .resolve("models");

            String folderName = safeFileName(name == null ? "default" : name);
            String fileName = safeFileName(group.getName()) + ".json";

            Path targetDir = baseDir.resolve(folderName);
            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(fileName);
            Files.writeString(targetFile, json);

            createCase(
                    name,
                    group.getName()
            );

        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save model json: " + e.getMessage());
        }
    }

    private static int isOneCoordinate(float x, float y, float z){
        int coordinate = 0;
        if(x != 0){
            coordinate = 1;
        }
        if(y != 0){
            if(coordinate != 0){
                return -1;
            }else{
                coordinate = 2;
            }
        }
        if(z != 0){
            if(coordinate != 0){
                return -1;
            }else{
                coordinate = 3;
            }
        }
        return coordinate;
    }

    public static void zipResourcePack() {
        try {
            Path resourcePackFolder = MagicModels.getPlugin()
                    .getDataFolder()
                    .toPath()
                    .resolve("resourcepack");

            Path zipFile = MagicModels.getPlugin()
                    .getDataFolder()
                    .toPath()
                    .resolve("resourcepack.zip");

            Files.deleteIfExists(zipFile);

            try (
                    ZipOutputStream zipOutputStream = new ZipOutputStream(
                            Files.newOutputStream(zipFile)
                    )
            ) {

                Files.walk(resourcePackFolder)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            try {
                                Path relativePath = resourcePackFolder.relativize(path);

                                ZipEntry zipEntry = new ZipEntry(
                                        relativePath.toString().replace("\\", "/")
                                );

                                zipOutputStream.putNextEntry(zipEntry);

                                Files.copy(path, zipOutputStream);

                                zipOutputStream.closeEntry();

                            } catch (Exception e) {
                                Bukkit.getLogger().severe(
                                        "Zip entry error: " + path + " -> " + e.getMessage()
                                );
                            }
                        });
            }

            Bukkit.getLogger().info("Resourcepack archived: " + zipFile);

        } catch (Exception e) {
            Bukkit.getLogger().severe("zipResourcePack error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createAtlasFile() {
        try {
            Path atlasPath = MagicModels.getPlugin()
                    .getDataFolder()
                    .toPath()
                    .resolve("resourcepack")
                    .resolve("assets")
                    .resolve("minecraft")
                    .resolve("atlases")
                    .resolve("blocks.json");

            Files.createDirectories(atlasPath.getParent());

            JsonObject root;

            if (Files.exists(atlasPath)) {
                try (Reader reader = Files.newBufferedReader(atlasPath, StandardCharsets.UTF_8)) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    root = parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
                }
            } else {
                root = new JsonObject();
            }

            JsonArray sources;
            if (root.has("sources") && root.get("sources").isJsonArray()) {
                sources = root.getAsJsonArray("sources");
            } else {
                sources = new JsonArray();
                root.add("sources", sources);
            }

            boolean exists = false;
            for (JsonElement element : sources) {
                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject obj = element.getAsJsonObject();
                if (!obj.has("type") || !obj.has("source") || !obj.has("prefix")) {
                    continue;
                }

                String type = obj.get("type").getAsString();
                String source = obj.get("source").getAsString();
                String prefix = obj.get("prefix").getAsString();

                if ("directory".equals(type)
                        && "models".equals(source)
                        && "models/".equals(prefix)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                JsonObject newSource = new JsonObject();
                newSource.addProperty("type", "directory");
                newSource.addProperty("source", "models");
                newSource.addProperty("prefix", "models/");
                sources.add(newSource);
            }

            String json = new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(root);

            Files.writeString(
                    atlasPath,
                    json,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            Bukkit.getLogger().info("Created/updated atlas file: " + atlasPath);
        } catch (Exception e) {
            Bukkit.getLogger().severe("createAtlasFile error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createItemFile() {
        JsonObject root = new JsonObject();

        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:select");
        model.addProperty("property", "minecraft:custom_model_data");

        JsonArray cases = new JsonArray();
        for (JsonObject caseObject : caseList) {
            cases.add(caseObject);
        }

        model.add("cases", cases);

        JsonObject fallback = new JsonObject();
        fallback.addProperty("type", "minecraft:model");
        fallback.addProperty("model", "minecraft:item/white_wool");
        model.add("fallback", fallback);

        root.add("model", model);

        String json = new GsonBuilder().setPrettyPrinting().create().toJson(root);

        try {
            Path target = MagicModels.getPlugin().getDataFolder()
                    .toPath()
                    .resolve("resourcepack")
                    .resolve("assets")
                    .resolve("minecraft")
                    .resolve("items")
                    .resolve("white_wool.json");

            Files.createDirectories(target.getParent());
            Files.writeString(target, json);

            Bukkit.getLogger().info("Saved item model: " + target);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save item model: " + e.getMessage());
        }

        caseList.clear();
    }

    private static void createCase(String modelName, String boneName){
        JsonObject caseObject = new JsonObject();
        caseObject.addProperty("when", modelName + "_" + boneName);

        JsonObject caseModel = new JsonObject();
        caseModel.addProperty("type", "minecraft:model");
        caseModel.addProperty("model", "magicmodels:" + modelName + "/" + boneName);

        caseObject.add("model", caseModel);

        caseList.add(caseObject);
    }

    public static void decodeTextures(File modelFile) {
        try (FileReader reader = new FileReader(modelFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            String modelName = root.has("name") && !root.get("name").isJsonNull()
                    ? root.get("name").getAsString()
                    : "unknown_model";

            if (!root.has("textures") || root.get("textures").isJsonNull()) {
                Bukkit.getLogger().warning("No textures in " + modelFile.getName());
                return;
            }

            JsonElement texturesElement = root.get("textures");

            if (texturesElement.isJsonArray()) {
                JsonArray texturesArray = texturesElement.getAsJsonArray();

                for (JsonElement textureElement : texturesArray) {
                    if (!textureElement.isJsonObject()) {
                        continue;
                    }

                    JsonObject textureObj = textureElement.getAsJsonObject();
                    saveTextureObject(textureObj, modelName);
                }

            } else if (texturesElement.isJsonObject()) {
                JsonObject texturesObj = texturesElement.getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : texturesObj.entrySet()) {
                    if (!entry.getValue().isJsonObject()) {
                        continue;
                    }

                    JsonObject textureObj = entry.getValue().getAsJsonObject();

                    if (!textureObj.has("name")) {
                        textureObj.addProperty("name", entry.getKey());
                    }

                    saveTextureObject(textureObj, modelName);
                }

            } else {
                Bukkit.getLogger().warning("Unsupported textures format in " + modelFile.getName());
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("decodeTextures error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveTextureObject(JsonObject textureObj, String modelName) {
        try {
            if (!textureObj.has("source") || textureObj.get("source").isJsonNull()) {
                return;
            }

            String source = textureObj.get("source").getAsString();

            if (!source.startsWith("data:")) {
                return;
            }

            int commaIndex = source.indexOf(",");
            if (commaIndex == -1) {
                return;
            }

            String base64 = source.substring(commaIndex + 1);
            byte[] imageBytes = Base64.getDecoder().decode(base64);

            String textureName = textureObj.has("name") && !textureObj.get("name").isJsonNull()
                    ? textureObj.get("name").getAsString()
                    : "texture";

            textureName = safeFileName(textureName);
            modelName = safeFileName(modelName);

            if (textureName.endsWith(".png")) {
                textureName = textureName.substring(0, textureName.length() - 4);
            }

            Path target = MagicModels.getPlugin().getDataFolder()
                    .toPath()
                    .resolve("resourcepack")
                    .resolve("assets")
                    .resolve("magicmodels")
                    .resolve("textures")
                    .resolve("models")
                    .resolve(modelName)
                    .resolve(textureName + ".png");

            Files.createDirectories(target.getParent());
            Files.write(target, imageBytes);

            Bukkit.getLogger().info("Saved texture: " + target);

            createMcmetaIfNeeded(target, textureName, textureObj);

        } catch (Exception e) {
            Bukkit.getLogger().severe("saveTextureObject error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createMcmetaIfNeeded(Path pngPath, String textureName, JsonObject textureObj) {
        try {
            int uv_height = textureObj.get("uv_height").getAsInt();
            int height = textureObj.get("height").getAsInt();

            if(height % uv_height != 0 || uv_height == height){
                return;
            }

            int fps = textureObj.get("fps").getAsInt();

            int frametime = Math.max(1, 20 / fps);

            Path mcmetaPath = Path.of(pngPath.toString() + ".mcmeta");

            String mcmeta = String.format("""
                                {
                                  "animation": {
                                    "frametime": %d
                                  }
                                }
                                """, frametime);

            Files.writeString(mcmetaPath, mcmeta, StandardCharsets.UTF_8);
            Bukkit.getLogger().info("Saved mcmeta: " + mcmetaPath);

        } catch (Exception e) {
            Bukkit.getLogger().severe("createMcmetaIfNeeded error: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private static List<Float> mathVec3(List<Float> cubeVec, List<Float> boneVec, List<Float> pivot){
        List<Float> newVector = new ArrayList<>();
        float x = (cubeVec.getFirst() - boneVec.getFirst()) * 0.5f + pivot.getFirst();
        float y = (cubeVec.get(1) - boneVec.get(1)) * 0.5f + pivot.get(1);
        float z = (cubeVec.get(2) - boneVec.get(2)) * 0.5f + pivot.get(2);
        newVector.add(x);
        newVector.add(y);
        newVector.add(z);

        /*for(int i = 0; i < cubeVec.size(); i++){
            float value = cubeVec.get(i) - boneVec.get(i);
            newVector.add(value);
        }*/
        return newVector;
    }

    private static JsonArray vec3(List<Float> values) {
        JsonArray arr = new JsonArray();
        for (float value : values) {
            arr.add(value);
        }
        return arr;
    }

    private static JsonObject face(
            List<Float> values,
            String textureName,
            int textureWidth,
            int textureHeight,
            int rotation
    ) {
        JsonObject f = new JsonObject();

        float u1 = values.get(0) / textureWidth * 16f;
        float v1 = values.get(1) / textureHeight * 16f;
        float u2 = values.get(2) / textureWidth * 16f;
        float v2 = values.get(3) / textureHeight * 16f;

        JsonArray uv = new JsonArray();
        uv.add(u1);
        uv.add(v1);
        uv.add(u2);
        uv.add(v2);

        f.add("uv", uv);

        if(rotation != 0){
            f.addProperty("rotation", rotation);
        }

        f.addProperty("texture", textureName);

        return f;
    }

    private static String safeFileName(String input) {
        return input.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    public static void clearResourcePackFolder() {
        Path resourcePackPath = MagicModels.getPlugin()
                .getDataFolder()
                .toPath()
                .resolve("resourcepack");

        try {

            if (Files.exists(resourcePackPath)) {

                Files.walk(resourcePackPath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                Bukkit.getLogger().severe(
                                        "Failed delete: " + path + " | " + e.getMessage()
                                );
                            }
                        });
            }

            Files.createDirectories(resourcePackPath);

            Bukkit.getLogger().info("Resourcepack folder cleared");

        } catch (Exception e) {
            Bukkit.getLogger().severe(
                    "clearResourcePackFolder error: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}