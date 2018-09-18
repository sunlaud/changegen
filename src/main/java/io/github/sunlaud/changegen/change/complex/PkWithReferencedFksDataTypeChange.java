package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.Key;
import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.change.basic.AddFkChange;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.change.basic.DropFkChange;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PkWithReferencedFksDataTypeChange extends PkDataTypeChange {
    private final Collection<Key> foreignKeys;

    public PkWithReferencedFksDataTypeChange(DataTypeChange dataTypeChange, Key pk, @NonNull Collection<Key> foreignKeys) {
        super(dataTypeChange, pk);
        this.foreignKeys = foreignKeys;
    }

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.addAll(dropFks());
        changes.addAll(super.getChanges());
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
                .map(fk -> new AddFkChange(fk, getPk().getColumns()))
                .collect(Collectors.toList());
    }

}
