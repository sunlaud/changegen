package io.github.sunlaud.changegen.extractor.db.key;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.Columns;
import io.github.sunlaud.changegen.generator.Key;
import lombok.RequiredArgsConstructor;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

@RequiredArgsConstructor
public abstract class AbstractKeyExtractor implements KeyExtractor {
    private final DBI dbi;

    @Override
    public Key getPk(String tableName) {
        try (Handle handle = dbi.open()) {
            List<Map<String, Object>> keys = handle
                    .createQuery(getPkQuerySql())
                    .bind("tableName", tableName)
                    .list();

            checkState(!keys.isEmpty(), "Table %s has no primary key", tableName);
            checkState(keys.size() < 2, "Support of multi-column primary keys is not implemented yet: PK in table %s has %s columns", tableName, keys.size());
            return extractKey(keys.get(0));
        }
    }

    @Override
    public Collection<Key> getFkReferncing(Columns referencedColumns) {
        Preconditions.checkArgument(referencedColumns.getNames().size() < 2, "Support of multi-column foreign keys is not implemented yet");

        try (Handle handle = dbi.open()) {
            List<Map<String, Object>> keys = handle
                    .createQuery(getFkQuerySql())
                    .bind("tableName", referencedColumns.getTableName())
                    .bind("columnName", Iterables.getOnlyElement(referencedColumns.getNames()))
                    .list();

            return keys.stream()
                    .map(this::extractKey)
                    .collect(Collectors.toList());
        }
    }

    private Key extractKey(Map<String, Object> keyInfo) {
        String constraintName = (String) keyInfo.get("constraint_name");
        String columnName = (String) keyInfo.get("column_name");
        String tableName = (String) keyInfo.get("table_name");
        return new Key(constraintName, new Column(tableName, columnName));
    }

    protected abstract String getFkQuerySql();

    protected abstract String getPkQuerySql();
}
