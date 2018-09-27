package io.github.sunlaud.changegen;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.change.ColumnChange;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.change.complex.ChangeWithFksDropRestore;
import io.github.sunlaud.changegen.change.complex.ChangeWithPkDropRestore;
import io.github.sunlaud.changegen.change.complex.CompositeChange;
import io.github.sunlaud.changegen.dbinfo.key.DbMetadataExtractor;
import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.model.Key;
import io.github.sunlaud.changegen.model.TypedColumn;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ChangeSetGenerator {
    @NonNull
    private final DbMetadataExtractor metadataExtractor;

    public String generateChangeset(@NonNull ColumnChange change) {
        Change changeToUse = buildDependencyAwareChange(change);
        return changeToUse.generateXml();
    }

    private Change buildDependencyAwareChange(@NonNull ColumnChange change) {
        Change changeToUse = change;
        Column affectedColumn = change.getColumn();
        Optional<Key> maybePk = metadataExtractor.getPk(affectedColumn.getTableName());
        if (!maybePk.isPresent()) {
            return changeToUse;
        }
        Key pk = maybePk.get();
        if (pk.getColumns().contain(affectedColumn)) {
            if (changeToUse instanceof DataTypeChange) {
                //add not null constraint to column, coz it is lost after datatype changed
                changeToUse = ((DataTypeChange) changeToUse).withNotNull();
            }
            changeToUse = new ChangeWithPkDropRestore(changeToUse, pk);
            Collection<ForeignKey> fks = metadataExtractor.getFkReferncing(pk.getColumns());
            if (!fks.isEmpty()) {
                CompositeChange dependentFksChange = fks.stream()
                        .map(fk -> fk.getColumnReferencing(affectedColumn))
                        .map(column -> applyChange(change, column))
                        .map(this::buildDependencyAwareChange) //search for PK/FK referencing this FK
                        .collect(collectingAndThen(toList(), CompositeChange::of));
                changeToUse = new ChangeWithFksDropRestore(CompositeChange.of(asList(changeToUse, dependentFksChange)), fks);
            }
        }
        return changeToUse;
    }

    private ColumnChange applyChange(ColumnChange change, Column column) {
        TypedColumn typedColumn = metadataExtractor.getColumnInfo(column);
        ColumnChange changeToUse = (change instanceof DataTypeChange && !typedColumn.isNullable())
                ? ((DataTypeChange) change).withNotNull() //add not null constraint to column, coz it is lost after datatype changed
                : change;
        return  changeToUse.applyTo(column);
    }
}
