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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.json.Json;
import java.util.HashMap;
import java.util.Map;

/**
 * date 2014-06-06
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@RunWith(JUnit4.class)
public class CustomizableJsonOutputTest {

    @Test
    public void test() {
        CustomizableJsonOutput output = new CustomizableJsonOutput();

        Map<String, Object> map = new HashMap<>();
        map.put("obj", Json.createObjectBuilder().add("k", "v").build());
        map.put("arr", Json.createArrayBuilder().add(1).add(2).build());

        Assert.assertEquals("{\"arr\":[1,2],\"obj\":{\"k\":\"v\"}}", output.toJson(map));
    }

}
