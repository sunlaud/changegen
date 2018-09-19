package io.github.sunlaud.changegen.generator.change.complex;

import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.Change;
import io.github.sunlaud.changegen.generator.change.CompositeChange;
import io.github.sunlaud.changegen.generator.change.basic.AddPkChange;
import io.github.sunlaud.changegen.generator.change.basic.DropPkChange;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class PkChangeWithDropRestore extends CompositeChange {
    @NonNull
    private final Change pkChange;
    @Getter
    @NonNull
    private final Key pk;

    @Override
    protected Collection<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        changes.add(new DropPkChange(pk));
        changes.add(pkChange);
        changes.add(new AddPkChange(pk));
        return changes;
    }
}
