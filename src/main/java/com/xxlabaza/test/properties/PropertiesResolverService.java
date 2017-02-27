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

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for resolving properties map.
 *
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 22.02.2017
 */
public final class PropertiesResolverService {

    /**
     * Evaluates properties map and resolve its values.
     * <p>
     * It resolves map values by the following syntax:
     * <p>
     * {@code key1 -> hello }
     * <p>
     * {@code key2 -> ${key1} world}
     * <p>
     * will produce a new map:
     * <p>
     * {@code key1 -> hello }
     * <p>
     * {@code key2 -> hello world }
     *
     * @param properties properties map to resolve.
     *
     * @return new {@link Map} instance with resolved properties.
     *
     * @throws RecursionPropertyEvaluationException in case of recursion key evaluation. A simple example:
     *                                              {@code key1 -> ${key1} }
     * @throws UnknownPropertyKeyException          in case of unknown property key for evaluation.
     */
    public static Map<String, String> resolve (Map<String, String> properties)
            throws UnknownPropertyKeyException, RecursionPropertyEvaluationException {
        Map<String, String> result = new HashMap<>(properties.size(), 1.F);
        for (String key : properties.keySet()) {
            if (result.containsKey(key)) {
                continue;
            }

            String value = evaluate(key, properties, result);
            result.put(key, value);
        }
        return result;
    }

    private static String evaluate (String key, Map<String, String> original, Map<String, String> evaluated)
            throws UnknownPropertyKeyException, RecursionPropertyEvaluationException {
        String value = original.get(key);
        if (value == null) {
            throw new UnknownPropertyKeyException();
        }
        evaluated.put(key, null);

        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        do {
            String token = parseUntil(value, "${", index);

            index += token.length();
            index += "${".length();

            stringBuilder.append(token);
            if (index >= value.length()) {
                break;
            }

            token = parseUntil(value, "}", index);

            index += token.length();
            index += "}".length();

            String evaluatedToken = evaluated.containsKey(token)
                                    ? evaluated.get(token)
                                    : evaluate(token, original, evaluated);

            if (evaluatedToken == null) {
                throw new RecursionPropertyEvaluationException();
            }

            stringBuilder.append(evaluatedToken);
        } while (index < value.length());

        String result = stringBuilder.toString();
        evaluated.put(key, result);
        return result;
    }

    private static String parseUntil (String string, String until, int start) {
        int index = string.indexOf(until, start);
        return index != -1
               ? string.substring(start, index)
               : string.substring(start);
    }

    private PropertiesResolverService () {
    }
}
