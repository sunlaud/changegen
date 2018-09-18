package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.Columns;
import io.github.sunlaud.changegen.Key;
import io.github.sunlaud.changegen.change.Change;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddFkChange implements Change {
    @NonNull
    private final Key key;
    @NonNull
    private final Columns referenceColumns;

    @Override
    public String generate() {
        return String.format("<addForeignKeyConstraint baseTableName=\"%s\" baseColumnNames=\"%s\" referencedTableName=\"%s\" referencedColumnNames=\"%s\" constraintName=\"%s\"/>",
                key.getTableName(), key.getColumnNames(), referenceColumns.getTableName(), referenceColumns.getNames(), key.getName());
    }
}
