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
package com.guestful.json.groovy;

import com.guestful.json.JsonMapper;
import com.guestful.json.JsonMapperException;
import groovy.json.JsonParserType;
import groovy.json.JsonSlurper;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class GroovyJsonMapper implements JsonMapper {

    private static final int BUFFER_SIZE = 8192;

    private final CustomizableJsonOutput serializer;

    public GroovyJsonMapper() {
        this.serializer = new CustomizableJsonOutput().ignoreNullMapValues();
    }

    public GroovyJsonMapper(CustomizableJsonOutput serializer) {
        this.serializer = serializer;
    }

    public CustomizableJsonOutput getSerializer() {
        return serializer;
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonMapperException, IOException {
        try {
            JsonSlurper slurper = new JsonSlurper();
            slurper.setType(JsonParserType.CHAR_BUFFER);
            slurper.setCheckDates(false);
            slurper.setChop(false);
            slurper.setLazyChop(false);
            Object o = slurper.parse(reader);
            if (type.isAssignableFrom(o.getClass())) return type.cast(o);
            if (o instanceof Collection) return DefaultGroovyMethods.asType((Collection) o, type);
            if (o instanceof Map) return DefaultGroovyMethods.asType((Map) o, type);
            return DefaultGroovyMethods.asType(o, type);
        } catch (RuntimeException e) {
            throw new JsonMapperException("Error reading JSON as " + type.getSimpleName(), e);
        }
    }

    @Override
    public void toJson(Object o, Writer writer) throws IOException {
        writer = new BufferedWriter(writer);
        try {
            writer.write(serializer.toJson(o));
            writer.flush();
        } catch (RuntimeException e) {
            throw new JsonMapperException("Error writing object to JSON: " + o, e);
        }
    }

    public <T> GroovyJsonMapper addSerializer(Class<T> type, CustomizableJsonOutput.Serializer<? super T> serializer) {
        this.serializer.addHook(type, serializer);
        return this;
    }

    public GroovyJsonMapper addToStringSerializer(Class<?>... types) {
        serializer.addHookString(types);
        return this;
    }

}