package io.github.sunlaud.changegen;

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

    public Key(String name, Column column) {
        this(name, new Columns(column));
    }

    public String getColumnNames() {
        return columns.getNames();
    }

    public String getTableName() {
        return columns.getTableName();
    }
}
