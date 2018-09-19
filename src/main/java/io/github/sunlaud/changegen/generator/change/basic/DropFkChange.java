package io.github.sunlaud.changegen.generator.change.basic;

import io.github.sunlaud.changegen.generator.Key;
import io.github.sunlaud.changegen.generator.change.Change;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropFkChange implements Change {
    @NonNull
    private final Key key;

    @Override
    public String generateXml() {
        return String.format("<dropForeignKeyConstraint baseTableName=\"%s\" constraintName=\"%s\"/>", key.getTableName(), key.getName());
    }
}
