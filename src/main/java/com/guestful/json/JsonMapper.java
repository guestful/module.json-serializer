/**
 * Copyright (C) 2013 Guestful (info@guestful.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guestful.json;

import javax.json.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public interface JsonMapper {

    default <T> T fromJson(JsonValue json, Class<T> type) throws JsonMapperException {
        return fromJson(json.toString(), type);
    }

    default <T> T fromJson(String json, Class<T> type) throws JsonMapperException {
        try {
            return fromJson(new StringReader(json), type);
        } catch (IOException e) {
            throw new JsonMapperException(e);
        }
    }

    <T> T fromJson(Reader reader, Class<T> type) throws JsonMapperException, IOException;

    default <T> T fromJson(InputStream is, Charset charset, Class<T> type) throws JsonMapperException, IOException {
        return fromJson(new InputStreamReader(is, charset), type);
    }

    default <T> T fromJson(File file, Class<T> type) throws JsonMapperException, IOException {
        return fromJson(new FileInputStream(file), StandardCharsets.UTF_8, type);
    }

    default Object fromJson(String json) throws JsonMapperException {
        return fromJson(json, Object.class);
    }

    default Object fromJson(Reader reader) throws JsonMapperException, IOException {
        return fromJson(reader, Object.class);
    }

    default Object fromJson(File file) throws JsonMapperException, IOException {
        return fromJson(file, Object.class);
    };

    default JsonObject toJsonObject(Object o) throws JsonMapperException {
        try(JsonReader r = Json.createReader(new StringReader(toJson(o)))) {
            return r.readObject();
        }
    }

    default JsonArray toJsonArray(Object o) throws JsonMapperException {
        try(JsonReader r = Json.createReader(new StringReader(toJson(o)))) {
            return r.readArray();
        }
    }

    default String toJson(Object o) throws JsonMapperException {
        StringWriter sw = new StringWriter();
        try {
            toJson(o, sw);
        } catch (IOException e) {
            throw new JsonMapperException(e);
        }
        return sw.toString();
    }

    void toJson(Object o, Writer writer) throws JsonMapperException, IOException;

    default void toJson(Object o, OutputStream os, Charset charset) throws JsonMapperException, IOException {
        toJson(o, new OutputStreamWriter(os, charset));
    }

    default void toJson(Object o, File f) throws JsonMapperException, IOException {
        toJson(o, new FileOutputStream(f), StandardCharsets.UTF_8);
    }

}
