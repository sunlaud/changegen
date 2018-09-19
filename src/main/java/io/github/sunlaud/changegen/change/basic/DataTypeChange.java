package io.github.sunlaud.changegen.change.basic;


import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.change.ColumnChange;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataTypeChange implements ColumnChange {
    @Getter
    @NonNull
    private final Column column;
    @NonNull
    private final String newDataType;

    @Override
    public String generateXml() {
        return String.format("<modifyDataType tableName=\"%s\" columnName=\"%s\" newDataType=\"%s\"/>", column.getTableName(), column.getName(), newDataType);
    }

    public DataTypeChange applyTo(Column anotherColumn) {
        return new DataTypeChange(anotherColumn, newDataType);
    }
}
