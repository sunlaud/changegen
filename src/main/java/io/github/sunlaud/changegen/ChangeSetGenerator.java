package io.github.sunlaud.changegen;

import com.google.common.collect.Iterables;
import io.github.sunlaud.changegen.extractor.db.key.KeyExtractor;
import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.Change;
import io.github.sunlaud.changegen.generator.change.ColumnChange;
import io.github.sunlaud.changegen.generator.change.CompositeChange;
import io.github.sunlaud.changegen.generator.change.complex.PkAndFksChangeWithDropRestore;
import io.github.sunlaud.changegen.generator.change.complex.PkChangeWithDropRestore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ChangeSetGenerator {

    @NonNull
    private final KeyExtractor keyExtractor;

    public String generateChangeset(@NonNull ColumnChange change) {
        Change changeToUse = change;
        Column affectedColumn = change.getColumn();
        Key pk = keyExtractor.getPk(affectedColumn.getTableName());
        if (pk.getColumns().contain(affectedColumn)) {
            changeToUse = new PkChangeWithDropRestore(changeToUse, pk);
            Collection<Key> fks = keyExtractor.getFkReferncing(pk.getColumns());
            if (!fks.isEmpty()) {
                CompositeChange dependentFksChange = fks.stream()
                        .map(Key::getColumns)
                        .map(columns -> new Column(columns.getTableName(), Iterables.getOnlyElement(columns.getNames()))) //for now multi-column FKs are not supported
                        .map(change::applyTo)
                        .collect(collectingAndThen(toList(), CompositeChange::of));
                changeToUse = new PkAndFksChangeWithDropRestore(CompositeChange.of(asList(changeToUse, dependentFksChange)), pk, fks);
            }
        }

        return changeToUse.generateXml();
    }
}
