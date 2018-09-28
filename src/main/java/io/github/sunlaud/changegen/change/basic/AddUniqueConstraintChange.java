package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.model.Index;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddUniqueConstraintChange implements Change {
    @NonNull
    private final Index index;

    @Override
    public String generateXml() {
        return String.format("-<addUniqueConstraint tableName=\"%s\" columnNames=\"%s\" constraintName=\"%s\"/>", index.getTableName(), index.getColumns().getNamesJoined(), index.getName());
    }
}
