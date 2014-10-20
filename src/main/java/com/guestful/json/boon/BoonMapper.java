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
package com.guestful.json.boon;

import com.guestful.json.JsonMapper;
import com.guestful.json.JsonMapperException;
import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.ObjectMapperImpl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class BoonMapper implements JsonMapper {

    private final ObjectMapper mapper;

    public BoonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public BoonMapper() {
        mapper = new ObjectMapperImpl(
            new JsonParserFactory().lax(),
            new JsonSerializerFactory());
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> type) throws JsonMapperException, IOException {
        return mapper.fromJson(reader, type);
    }

    @Override
    public void toJson(Object o, Writer writer) throws JsonMapperException, IOException {
        mapper.toJson(o, writer);
    }

}
