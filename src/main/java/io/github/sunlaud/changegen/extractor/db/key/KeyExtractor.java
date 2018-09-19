package io.github.sunlaud.changegen.extractor.db.key;

import io.github.sunlaud.changegen.generator.Columns;
import io.github.sunlaud.changegen.generator.Key;

import java.util.Collection;

public interface KeyExtractor {
    Key getPk(String tableName);
    Collection<Key> getFkReferncing(Columns referencedColumns);
}
