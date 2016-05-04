/*
 * Copyright (C) 2015-2016 Lukoh Nam, goForer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goforer.base.model;


import com.goforer.base.model.data.Image;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

public class ImageMap extends HashMap<String, Image> {
    static class ImageMapDeserializer implements JsonDeserializer<ImageMap> {
        @Override
        public ImageMap deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();
            ImageMap imageMap = new ImageMap();
            while (iterator.hasNext()) {
                JsonElement elem = iterator.next();
                if (elem.isJsonNull()) return imageMap;
                JsonObject obj = elem.getAsJsonObject();
                if (obj.get("image_key") == null) return imageMap;
                if (obj.isJsonNull()) return imageMap;
                String key = obj.get("image_key").getAsString();
                Image image = context.deserialize(obj, Image.class);
                imageMap.put(key, image);
            }
            return imageMap;
        }
    }

    static class ImageMapSerializer implements JsonSerializer<ImageMap> {
        @Override
        public JsonElement serialize(ImageMap src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            for (String key : src.keySet()) {
                Image image = src.get(key);
                JsonElement element = context.serialize(image, Image.class);
                array.add(element);
            }
            return array;
        }
    }
}
