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
import static java.util.stream.Collectors.toList;

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
        names = entry.getValue().stream().map(Column::getName).collect(toList());
    }

    public Columns(@NonNull Column column) {
        this(column.getTableName(), Collections.singletonList(column.getName()));
    }

    public String getNamesJoined() {
        return String.join(",", names);
    }

    public boolean contain(@NonNull Column column) {
        return contain(new Columns(column));
    }

    public boolean contain(@NonNull Columns columns) {
        Set<String> otherNamesUppercase = columns.getNames().stream().map(String::toUpperCase).collect(Collectors.toSet());
        Set<String> thisNamesUppercase = this.getNames().stream().map(String::toUpperCase).collect(Collectors.toSet());
        return tableName.equalsIgnoreCase(columns.getTableName()) && thisNamesUppercase.containsAll(otherNamesUppercase);
    }

    public Columns add(@NonNull Columns other) {
        checkState(tableName.equalsIgnoreCase(other.tableName));
        Set<String> composedNames = new HashSet<>(names);
        composedNames.addAll(other.names);
        return new Columns(tableName, composedNames);
    }
}
