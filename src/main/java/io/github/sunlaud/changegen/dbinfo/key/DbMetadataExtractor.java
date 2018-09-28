package io.github.sunlaud.changegen.dbinfo.key;

import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.model.Columns;
import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.model.Index;
import io.github.sunlaud.changegen.model.Key;
import io.github.sunlaud.changegen.model.TypedColumn;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.Optional;

public interface DbMetadataExtractor {
    Optional<Key> getPk(String tableName);

    @SneakyThrows
    Collection<Index> getIndexesContaining(@NonNull Column column);

    @SneakyThrows
    TypedColumn getColumnInfo(@NonNull Column column);

    Collection<ForeignKey> getFkReferncing(Columns referencedColumns);
}
