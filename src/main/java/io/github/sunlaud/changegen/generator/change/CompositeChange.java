package io.github.sunlaud.changegen.generator.change;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class CompositeChange implements Change {
    private static final String CHANGES_SEPARATOR = System.lineSeparator();

    @Override
    public String generateXml() {
        return getChanges().stream()
                .map(Change::generateXml)
                .collect(Collectors.joining(CHANGES_SEPARATOR));
    }

    protected abstract Collection<Change> getChanges();
}
