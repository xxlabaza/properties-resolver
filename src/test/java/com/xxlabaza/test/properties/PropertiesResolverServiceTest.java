/*
 * Copyright 2017 Artem Labazin <xxlabaza@gmail.com>.
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
package com.xxlabaza.test.properties;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 22.02.2017
 */
public class PropertiesResolverServiceTest {

    private final PropertiesResolverService service;

    public PropertiesResolverServiceTest () {
        service = new PropertiesResolverService();
    }

    @Test
    public void noReplaces () {
        Map<String, String> properties = new HashMap<>(1, 1.F);
        properties.put("key", "value");

        Map<String, String> result = service.resolve(properties);

        assertEquals(properties, result);
    }

    @Test
    public void simpleReplace () {
        Map<String, String> properties = new HashMap<>(2, 1.F);
        properties.put("key1", "value");
        properties.put("key2", "another ${key1}");

        Map<String, String> result = service.resolve(properties);

        assertEquals(properties.size(), result.size());
        assertEquals(properties.get("key1"), result.get("key1"));
        assertEquals("another value", result.get("key2"));
    }

    @Test
    public void twoReplacesInValue () {
        Map<String, String> properties = new HashMap<>(3, 1.F);
        properties.put("key1", "value");
        properties.put("key2", "${key1} is ${key1}");

        Map<String, String> result = service.resolve(properties);

        assertEquals(properties.size(), result.size());
        assertEquals(properties.get("key1"), result.get("key1"));
        assertEquals("value is value", result.get("key2"));
    }

    @Test
    public void hardReplace () {
        Map<String, String> properties = new HashMap<>(11, 1.F);
        properties.put("binary-name", "main");
        properties.put("main-file-name", "ncb");
        properties.put("ncb-file-name", "build.ncb");
        properties.put("build.folder", "build");

        properties.put("build.sources.main.folder", "${build.folder}/sources/main");
        properties.put("build.sources.test.folder", "${build.folder}/sources/test");
        properties.put("build.resources.main.folder", "${build.folder}/resources/main");
        properties.put("build.resources.test.folder", "${build.folder}/resources/test");
        properties.put("build.cache.folder", "${build.folder}/cache");
        properties.put("build.target.folder", "${build.folder}/target");
        properties.put("build.binary.file", "${build.target.folder}/${binary-name}");

        Map<String, String> result = service.resolve(properties);

        assertEquals(properties.size(), result.size());
        assertEquals(properties.get("binary-name"), result.get("binary-name"));
        assertEquals(properties.get("main-file-name"), result.get("main-file-name"));
        assertEquals(properties.get("ncb-file-name"), result.get("ncb-file-name"));
        assertEquals(properties.get("build.folder"), result.get("build.folder"));

        assertEquals("build/sources/main", result.get("build.sources.main.folder"));
        assertEquals("build/sources/test", result.get("build.sources.test.folder"));
        assertEquals("build/resources/main", result.get("build.resources.main.folder"));
        assertEquals("build/resources/test", result.get("build.resources.test.folder"));
        assertEquals("build/cache", result.get("build.cache.folder"));
        assertEquals("build/target", result.get("build.target.folder"));
        assertEquals("build/target/main", result.get("build.binary.file"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownKey () {
        Map<String, String> properties = new HashMap<>(2, 1.F);
        properties.put("key1", "value");
        properties.put("key2", "non existent ${key3}");

        service.resolve(properties);
    }

    @Test(expected = Exception.class)
    public void valueEvaluationRecursion1 () {
        Map<String, String> properties = new HashMap<>(2, 1.F);
        properties.put("key1", "value");
        properties.put("key2", "recursion ${key2}");

        service.resolve(properties);
    }

    @Test(expected = Exception.class)
    public void valueEvaluationRecursion2 () {
        Map<String, String> properties = new HashMap<>(2, 1.F);
        properties.put("key1", "${key2}");
        properties.put("key2", "${key2}");

        service.resolve(properties);
    }

//    @Test(expected = Exception.class)
//    public void valueCrossEvaluation () {
//        Map<String, String> properties = new HashMap<>(2, 1.F);
//        properties.put("key1", "${key2}");
//        properties.put("key2", "${key1}");
//
//        service.resolve(properties);
//    }
}
