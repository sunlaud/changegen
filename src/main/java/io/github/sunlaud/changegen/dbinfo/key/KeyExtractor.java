package io.github.sunlaud.changegen.dbinfo.key;

import io.github.sunlaud.changegen.model.Columns;
import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.model.Key;

import java.util.Collection;

public interface KeyExtractor {
    Key getPk(String tableName);
    Collection<ForeignKey> getFkReferncing(Columns referencedColumns);
}
