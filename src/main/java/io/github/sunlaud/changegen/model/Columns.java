package io.github.sunlaud.changegen.model;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

@Data
@RequiredArgsConstructor
public class Columns {
    @NonNull
    private final String tableName;
    @NonNull
    private final Collection<String> names;

    public Columns(@NonNull Collection<Column> columns) {
        Map<String, Collection<Column>> columnsByTable = Multimaps.index(columns, Column::getTableName).asMap();
        Entry<String, Collection<Column>> entry = Iterables.getOnlyElement(columnsByTable.entrySet());
        tableName = entry.getKey();
        names = entry.getValue().stream().map(Column::getName).collect(Collectors.toList());
    }

    public Columns(@NonNull Column column) {
        this(column.getTableName(), Collections.singletonList(column.getName()));
    }

    public String getNamesJoined() {
        return String.join(",", names);
    }

    public boolean contain(@NonNull Column column) {
        return tableName.equalsIgnoreCase(column.getTableName()) && names.stream().anyMatch(column.getName()::equalsIgnoreCase);
    }

    public Columns add(@NonNull Columns other) {
        checkState(tableName.equalsIgnoreCase(other.tableName));
        Set<String> composedNames = new HashSet<>(names);
        composedNames.addAll(other.names);
        return new Columns(tableName, composedNames);
    }
}
