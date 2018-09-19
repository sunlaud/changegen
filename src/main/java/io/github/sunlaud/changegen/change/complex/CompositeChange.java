package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.change.Change;
import lombok.RequiredArgsConstructor;

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

    public static CompositeChange of(Collection<Change> changes) {
        return new SimpleCompositeChange(changes);
    }

    @RequiredArgsConstructor
    private static class SimpleCompositeChange extends CompositeChange {
        private final Collection<Change> changes;

        @Override
        protected Collection<Change> getChanges() {
            return changes;
        }
    }
}
