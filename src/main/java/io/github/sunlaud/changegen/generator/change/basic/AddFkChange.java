package io.github.sunlaud.changegen.generator.change.basic;

import io.github.sunlaud.changegen.generator.Columns;
import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.Change;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddFkChange implements Change {
    @NonNull
    private final Key key;
    @NonNull
    private final Columns referenceColumns;

    @Override
    public String generateXml() {
        return String.format("<addForeignKeyConstraint baseTableName=\"%s\" baseColumnNames=\"%s\" referencedTableName=\"%s\" referencedColumnNames=\"%s\" constraintName=\"%s\"/>",
                key.getTableName(), key.getColumnNames(), referenceColumns.getTableName(), referenceColumns.getNamesJoined(), key.getName());
    }
}
