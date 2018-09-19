package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.change.Change;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddFkChange implements Change {
    @NonNull
    private final ForeignKey key;

    @Override
    public String generateXml() {
        return String.format("<addForeignKeyConstraint baseTableName=\"%s\" baseColumnNames=\"%s\" referencedTableName=\"%s\" referencedColumnNames=\"%s\" constraintName=\"%s\"/>",
                key.getTableName(), key.getColumnNamesJoined(), key.getReferenceColumns().getTableName(), key.getReferenceColumns().getNamesJoined(), key.getName());
    }
}
