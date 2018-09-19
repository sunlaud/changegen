package io.github.sunlaud.changegen.generator.change.complex;

import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.Change;
import io.github.sunlaud.changegen.generator.change.CompositeChange;
import io.github.sunlaud.changegen.generator.change.basic.AddFkChange;
import io.github.sunlaud.changegen.generator.change.basic.DropFkChange;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PkAndFksChangeWithDropRestore extends CompositeChange {
    @NonNull
    private final Change pkChange;
    @NonNull
    private final Key pk;
    @NonNull
    private final Collection<Key> foreignKeys;

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.addAll(dropFks());
        changes.add(pkChange);
        changes.addAll(addFks());
        return changes;
    }

    private Collection<? extends Change> dropFks() {
        return foreignKeys.stream()
                .map(DropFkChange::new)
                .collect(Collectors.toList());
    }

    private Collection<? extends Change> addFks() {
        return foreignKeys.stream()
                .map(fk -> new AddFkChange(fk, pk.getColumns()))
                .collect(Collectors.toList());
    }

}
