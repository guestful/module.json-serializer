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
package com.guestful.json.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guestful.json.JsonMapper;
import com.guestful.json.JsonMapperException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * date 2014-05-29
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class JacksonJsonMapper implements JsonMapper {

    private final ObjectMapper mapper;

    public JacksonJsonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JacksonJsonMapper() {
        mapper = new ObjectMapper()
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonMapperException, IOException {
        return mapper.readValue(reader, type);
    }

    @Override
    public void toJson(Object o, Writer writer) throws JsonMapperException, IOException {
        mapper.writeValue(writer, o);
    }

}
