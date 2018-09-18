package io.github.sunlaud.changegen.change.complex;

import io.github.sunlaud.changegen.Key;
import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.change.CompositeChange;
import io.github.sunlaud.changegen.change.basic.AddPkChange;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.change.basic.DropPkChange;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class PkDataTypeChange extends CompositeChange {
    @NonNull
    private final DataTypeChange dataTypeChange;
    @Getter
    @NonNull
    private final Key pk;

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.add(new DropPkChange(pk));
        changes.add(dataTypeChange);
        changes.add(new AddPkChange(pk));
        return changes;
    }
}
