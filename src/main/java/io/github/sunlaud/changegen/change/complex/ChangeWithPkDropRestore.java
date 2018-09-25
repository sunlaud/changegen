package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.change.basic.AddPkChange;
import io.github.sunlaud.changegen.change.basic.DropPkChange;
import io.github.sunlaud.changegen.model.Key;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ChangeWithPkDropRestore extends CompositeChange {
    @NonNull
    private final Change change;
    @NonNull
    private final Key pk;

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.add(new DropPkChange(pk));
        changes.add(change);
        changes.add(new AddPkChange(pk));
        return changes;
    }
}
