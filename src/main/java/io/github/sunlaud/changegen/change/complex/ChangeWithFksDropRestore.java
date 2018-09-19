package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.change.basic.AddFkChange;
import io.github.sunlaud.changegen.change.basic.DropFkChange;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ChangeWithFksDropRestore extends CompositeChange {
    @NonNull
    private final Change change;
    @NonNull
    private final Collection<ForeignKey> foreignKeys;

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.addAll(dropFks());
        changes.add(change);
        changes.addAll(addFks());
        return changes;
    }

    private Collection<? extends Change> dropFks() {
        return foreignKeys.stream().map(DropFkChange::new).collect(toList());
    }

    private Collection<? extends Change> addFks() {
        return foreignKeys.stream().map(AddFkChange::new).collect(toList());
    }

}
