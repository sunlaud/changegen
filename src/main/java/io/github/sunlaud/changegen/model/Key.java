package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.google.common.base.Preconditions.checkState;

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

    /** Returns composite key with columns of this key and columns from other key */
    public Key compose(@NonNull Key other) {
        checkState(name.equalsIgnoreCase(other.name));
        return new Key(name, columns.add(other.columns));
    }
}
