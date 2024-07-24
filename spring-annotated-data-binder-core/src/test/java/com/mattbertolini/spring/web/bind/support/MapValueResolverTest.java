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
