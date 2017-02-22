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
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 22.02.2017
 */
public class PropertiesResolverService {

    public Map<String, String> resolve (Map<String, String> properties) {
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

    private String evaluate (String key, Map<String, String> original, Map<String, String> evaluated) {
        String value = original.get(key);
        if (value == null) {
            throw new IllegalArgumentException();
        }

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
            if (token.equals(key)) {
                throw new IllegalArgumentException();
            }

            index += token.length();
            index += "}".length();

            String evaluatedToken = evaluated.containsKey(token)
                                    ? evaluated.get(token)
                                    : evaluate(token, original, evaluated);

            stringBuilder.append(evaluatedToken);
        } while (index < value.length());

        String result = stringBuilder.toString();
        evaluated.put(key, result);
        return result;
    }

    private String parseUntil (String string, String until, int start) {
        for (int index = start; index < string.length(); index++) {
            if (string.charAt(index) != until.charAt(0)) {
                continue;
            }

            int untilIndex = 1;
            while (index + untilIndex < string.length() &&
                   untilIndex < until.length() &&
                   string.charAt(index + untilIndex) == until.charAt(untilIndex)) {

                untilIndex++;
            }

            if (untilIndex >= until.length()) {
                return string.substring(start, index);
            }
        }
        return string.substring(start);
    }
}
