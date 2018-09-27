package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.JDBCType;

@EqualsAndHashCode(callSuper = true)
@Data
public class TypedColumn extends Column {
    private final JDBCType dataType;
    private final int size;
    private final boolean nullable;

    public TypedColumn(String tableName, String name, JDBCType dataType, int size, boolean nullable) {
        super(tableName, name);
        this.dataType = dataType;
        this.size = size;
        this.nullable = nullable;
    }

    public Column withoutType() {
        return new Column(getTableName(), getName());
    }
}
