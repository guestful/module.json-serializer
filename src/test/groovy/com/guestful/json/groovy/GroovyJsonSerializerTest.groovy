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
package com.guestful.json.groovy

import groovy.transform.Canonical
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import static org.junit.Assert.assertTrue

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@RunWith(JUnit4)
class GroovyJsonSerializerTest {

    GroovyJsonSerializer serializer = new GroovyJsonSerializer()

    static Throwable shouldFail(Closure code) {
        boolean failed = false;
        Throwable th = null;
        try {
            code.call();
        } catch (GroovyRuntimeException gre) {
            failed = true;
            th = ScriptBytecodeAdapter.unwrap(gre);
        } catch (Throwable e) {
            failed = true;
            th = e;
        }
        assertTrue("Closure " + code + " should have failed", failed);
        return th;
    }


    @Test
    void testEscapedKeys() {
        assert serializer.toJson(['my\nkey': 'my\nvalue']) == '{"my\\nkey":"my\\nvalue"}'
    }

    @Test
    void testBooleanValues() {
        assert serializer.toJson(Boolean.TRUE) == "true"
        assert serializer.toJson(Boolean.FALSE) == "false"
        assert serializer.toJson(true) == "true"
        assert serializer.toJson(false) == "false"
    }

    @Test
    void testNullValue() {
        assert serializer.toJson(null) == "null"
    }

    @Test
    void testNumbers() {
        assert serializer.toJson(-1) == "-1"
        assert serializer.toJson(1) == "1"
        assert serializer.toJson(0) == "0"
        assert serializer.toJson(100) == "100"
        assert serializer.toJson(100) == "100"

        assert serializer.toJson((short) 100) == "100"
        assert serializer.toJson((byte) 100) == "100"

        // Long
        assert serializer.toJson(1000000000000000000) == "1000000000000000000"
        // BigInteger
        assert serializer.toJson(1000000000000000000000000) == "1000000000000000000000000"

        // BigDecimal
        assert serializer.toJson(0.0) == "0.0"
        assert serializer.toJson(0.0) == "0.0"

        // Double
        assert serializer.toJson(Math.PI) == "3.141592653589793"
        // Float
        assert serializer.toJson(1.2345f) == "1.2345"

        // exponant
        assert serializer.toJson(1234.1234e12) == "1.2341234E+15"

        shouldFail { serializer.toJson(Double.NaN) }
        shouldFail { serializer.toJson(Double.POSITIVE_INFINITY) }
        shouldFail { serializer.toJson(Double.NEGATIVE_INFINITY) }
        shouldFail { serializer.toJson(Float.NaN) }
        shouldFail { serializer.toJson(Float.POSITIVE_INFINITY) }
        shouldFail { serializer.toJson(Float.NEGATIVE_INFINITY) }
    }

    @Test
    void testEmptyListOrArray() {
        assert serializer.toJson([]) == "[]"
        assert serializer.toJson([] as Object[]) == "[]"
    }

    @Test
    void testListOfPrimitives() {
        assert serializer.toJson([true, false, null, true, 4, 1.1234]) == "[true,false,null,true,4,1.1234]"
        assert serializer.toJson([true, [false, null], true, [4, [1.1234]]]) == "[true,[false,null],true,[4,[1.1234]]]"
    }

    @Test
    void testPrimitiveArray() {
        assert serializer.toJson([1, 2, 3, 4] as byte[]) == "[1,2,3,4]"
        assert serializer.toJson([1, 2, 3, 4] as short[]) == "[1,2,3,4]"
        assert serializer.toJson([1, 2, 3, 4] as int[]) == "[1,2,3,4]"
        assert serializer.toJson([1, 2, 3, 4] as long[]) == "[1,2,3,4]"
    }

    @Test
    void testEmptyMap() {
        assert serializer.toJson([:]) == "{}"
    }

    @Test
    void testMap() {
        assert serializer.toJson([a: 1]) == '{"a":1}'
        assert serializer.toJson([a: 1, b: 2]) == '{"a":1,"b":2}'
        assert serializer.toJson([a: 1, b: true, c: null, d: [], e: 'hello']) == '{"a":1,"b":true,"c":null,"d":[],"e":"hello"}'
    }

    @Test
    void testString() {
        assert serializer.toJson("") == '""'

        assert serializer.toJson("a") == '"a"'
        assert serializer.toJson("abcdef") == '"abcdef"'

        assert serializer.toJson("\b") == '"\\b"'
        assert serializer.toJson("\f") == '"\\f"'
        assert serializer.toJson("\n") == '"\\n"'
        assert serializer.toJson("\r") == '"\\r"'
        assert serializer.toJson("\t") == '"\\t"'

        assert serializer.toJson('"') == '"\\""'
        assert serializer.toJson("/") == '"/"'
        assert serializer.toJson("\\") == '"\\\\"'

        assert serializer.toJson("\u0001") == '"\\u0001"'
        assert serializer.toJson("\u0002") == '"\\u0002"'
        assert serializer.toJson("\u0003") == '"\\u0003"'
        assert serializer.toJson("\u0004") == '"\\u0004"'
        assert serializer.toJson("\u0005") == '"\\u0005"'
        assert serializer.toJson("\u0006") == '"\\u0006"'
        assert serializer.toJson("\u0007") == '"\\u0007"'
        assert serializer.toJson("\u0010") == '"\\u0010"'
        assert serializer.toJson("\u0011") == '"\\u0011"'
        assert serializer.toJson("\u0012") == '"\\u0012"'
        assert serializer.toJson("\u0013") == '"\\u0013"'
        assert serializer.toJson("\u0014") == '"\\u0014"'
        assert serializer.toJson("\u0015") == '"\\u0015"'
        assert serializer.toJson("\u0016") == '"\\u0016"'
        assert serializer.toJson("\u0017") == '"\\u0017"'
        assert serializer.toJson("\u0018") == '"\\u0018"'
        assert serializer.toJson("\u0019") == '"\\u0019"'
    }

    @Test
    void testComplexObject() {
        assert serializer.toJson([name: 'Guillaume', age: 33, address: [line1: "1 main street", line2: "", zip: 1234], pets: ['dog', 'cat']]) ==
            '{"name":"Guillaume","age":33,"address":{"line1":"1 main street","line2":"","zip":1234},"pets":["dog","cat"]}'

        assert serializer.toJson([[:], [:]]) == '[{},{}]'
    }

    @Test
    void testClosure() {
        assert serializer.toJson({
            a 1
            b {
                c 2
                d {
                    e 3, {
                        f 4
                    }
                }
            }
        }) == '{"a":1,"b":{"c":2,"d":{"e":[3,{"f":4}]}}}'
    }

    @Test
    void testIteratorEnumeration() {
        assert serializer.toJson([1, 2, 3].iterator()) == '[1,2,3]'
        assert serializer.toJson(Collections.enumeration([1, 2, 3])) == '[1,2,3]'
    }

    @Test
    void testSerializePogos() {
        def city = new JsonCity("Paris", [
            new JsonDistrict(1, [
                new JsonStreet("Saint-Honore", JsonStreetKind.street),
                new JsonStreet("de l'Opera", JsonStreetKind.avenue)
            ] as JsonStreet[]),
            new JsonDistrict(2, [
                new JsonStreet("des Italiens", JsonStreetKind.boulevard),
                new JsonStreet("Bonne Nouvelle", JsonStreetKind.boulevard)
            ] as JsonStreet[])
        ])

        assert serializer.toPrettyJson(city) == '''\
            {
                "name": "Paris",
                "districts": [
                    {
                        "streets": [
                            {
                                "kind": "street",
                                "streetName": "Saint-Honore"
                            },
                            {
                                "kind": "avenue",
                                "streetName": "de l'Opera"
                            }
                        ],
                        "number": 1
                    },
                    {
                        "streets": [
                            {
                                "kind": "boulevard",
                                "streetName": "des Italiens"
                            },
                            {
                                "kind": "boulevard",
                                "streetName": "Bonne Nouvelle"
                            }
                        ],
                        "number": 2
                    }
                ]
            }'''.stripIndent()
    }
}

@Canonical
class JsonCity {
    String name
    List<JsonDistrict> districts
}

@Canonical
class JsonDistrict {
    int number
    JsonStreet[] streets
}

@Canonical
class JsonStreet {
    String streetName
    JsonStreetKind kind
}

enum JsonStreetKind {
    street, boulevard, avenue
}