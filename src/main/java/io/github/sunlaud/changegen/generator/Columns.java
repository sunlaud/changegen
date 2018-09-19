package io.github.sunlaud.changegen.generator;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

@Data
@RequiredArgsConstructor
public class Columns {
    @NonNull
    private final String tableName;

    @NonNull
    private final Collection<String> names;

    public Columns(Column column) {
        this(column.getTableName(), Collections.singletonList(column.getName()));
    }

    public String getNamesJoined() {
        return String.join(",", names);
    }

    public boolean contain(Column column) {
        return tableName.equalsIgnoreCase(column.getTableName()) && names.stream().anyMatch(column.getName()::equalsIgnoreCase);
    }
}
