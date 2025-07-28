/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mattbertolini.spring.web.bind.support;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MapValueResolverTest {
    @Test
    void resolveValueReturnsMapValueFromKey() {
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        MapValueResolver valueResolver = new MapValueResolver(map);
        assertThat(valueResolver.resolveValue("key1", String.class)).isEqualTo("value1");
    }

    @Test
    void getNamesReturnsMapKeys() {
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        MapValueResolver valueResolver = new MapValueResolver(map);
        assertThat(valueResolver.getNames()).contains("key1", "key2");
    }

    @Test
    void valuesAccessorIsUnmodifiable() {
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        MapValueResolver valueResolver = new MapValueResolver(map);
        assertThat(valueResolver.values())
            .isUnmodifiable()
            .isEqualTo(map);
    }
}
