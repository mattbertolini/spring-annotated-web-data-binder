package com.mattbertolini.spring.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;

import java.util.Map;
import java.util.Set;

public record MapValueResolver(Map<String, Object> values) implements DataBinder.ValueResolver {
    @Override
    @Nullable
    public Object resolveValue(String name, Class<?> type) {
        return values.get(name);
    }

    @Override
    public Set<String> getNames() {
        return Set.copyOf(values.keySet());
    }
}
