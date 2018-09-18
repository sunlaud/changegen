package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.Key;
import io.github.sunlaud.changegen.change.Change;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropPkChange implements Change {
    @NonNull
    private final Key key;

    @Override
    public String generate() {
        return String.format("<dropPrimaryKey tableName=\"%s\" constraintName=\"%s\"/>", key.getTableName(), key.getName());
    }
}
