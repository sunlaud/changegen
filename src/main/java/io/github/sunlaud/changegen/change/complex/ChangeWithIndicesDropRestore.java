package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.change.basic.AddIndexChange;
import io.github.sunlaud.changegen.change.basic.AddUniqueConstraintChange;
import io.github.sunlaud.changegen.change.basic.DropIndexChange;
import io.github.sunlaud.changegen.change.basic.DropUniqueConstraintChange;
import io.github.sunlaud.changegen.model.Index;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ChangeWithIndicesDropRestore extends CompositeChange {
    @NonNull
    private final Change change;
    @NonNull
    private final Collection<Index> indices;

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.addAll(dropIndices());
        changes.add(change);
        changes.addAll(addIndices());
        return changes;
    }

    private Collection<? extends Change> dropIndices() {
        return indices.stream().map(index -> index.isUnique() ? new DropUniqueConstraintChange(index) : new DropIndexChange(index)).collect(toList());
    }

    private Collection<? extends Change> addIndices() {
        return indices.stream().map(index -> index.isUnique() ? new AddUniqueConstraintChange(index) : new AddIndexChange(index)).collect(toList());
    }
}
