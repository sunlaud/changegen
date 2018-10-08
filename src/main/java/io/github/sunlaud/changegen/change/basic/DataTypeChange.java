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
    protected final Column column;
    @NonNull
    protected final String dataType;

    @Override
    public String generateXml() {
        return String.format("<modifyDataType tableName=\"%s\" columnName=\"%s\" newDataType=\"%s\"/>", column.getTableName(), column.getName(), dataType);
    }

    public DataTypeChange applyTo(Column anotherColumn) {
        return new DataTypeChange(anotherColumn, dataType);
    }

    public AddNotNullConstraintChange withNotNull() {
        return new AddNotNullConstraintChange(column, dataType, "used instead of modifyDataType coz latter removes not null");
    }
}
