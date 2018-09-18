package io.github.sunlaud.changegen.change;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class CompositeChange implements Change {
    private static final String CHANGES_SEPARATOR = System.lineSeparator();

    @Override
    public String generate() {
        return getChanges().stream()
                .map(Change::generate)
                .collect(Collectors.joining(CHANGES_SEPARATOR));
    }

    protected abstract Collection<Change> getChanges();
}
