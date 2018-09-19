package io.github.sunlaud.changegen;

import com.google.common.collect.ImmutableSet;
import io.github.sunlaud.changegen.extractor.db.key.KeyExtractor;
import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.Change;
import io.github.sunlaud.changegen.generator.change.SingleColumnChange;
import io.github.sunlaud.changegen.generator.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.generator.change.complex.PkAndFksChangeWithDropRestore;
import io.github.sunlaud.changegen.generator.change.complex.PkChangeWithDropRestore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class ChangeSetGenerator {
    private final Collection<Class<? extends SingleColumnChange>> CHANGES_NEED_DROP_RESTORE = ImmutableSet.of(DataTypeChange.class);

    @NonNull
    private final KeyExtractor keyExtractor;

    public String generateChangeset(@NonNull Change change) {
        boolean needDropRestore = CHANGES_NEED_DROP_RESTORE.stream()
                .anyMatch(c -> c.isAssignableFrom(change.getClass()));
        Change changeToUse = change;
        if (needDropRestore) {
            Column affectedColumn = ((SingleColumnChange) change).getColumn(); //cast is safe coz we checked isAssignableFrom above
            Key pk = keyExtractor.getPk(affectedColumn.getTableName());
            if (pk.getColumns().contain(affectedColumn)) {
                changeToUse = new PkChangeWithDropRestore(changeToUse, pk);
                Collection<Key> fks = keyExtractor.getFkReferncing(pk.getColumns());
                if (!fks.isEmpty()) {
                    changeToUse = new PkAndFksChangeWithDropRestore(changeToUse, pk, fks);
                }
            }
        }
        return changeToUse.generateXml();
    }
}
