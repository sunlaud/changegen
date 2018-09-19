package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Key {
    @NonNull
    private final String name;
    @NonNull
    private final Columns columns;

    public Key(@NonNull String name, @NonNull Column column) {
        this(name, new Columns(column));
    }

    public String getColumnNamesJoined() {
        return columns.getNamesJoined();
    }

    public String getTableName() {
        return columns.getTableName();
    }
}
