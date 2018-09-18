package io.github.sunlaud.changegen.change.basic;


import io.github.sunlaud.changegen.Column;
import io.github.sunlaud.changegen.change.Change;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataTypeChange implements Change {
    @NonNull
    private final Column column;
    @NonNull
    private final String newDataType;

    @Override
    public String generate() {
        return String.format("<modifyDataType tableName=\"%s\" columnName=\"%s\" newDataType=\"%s\"/>", column.getTableName(), column.getName(), newDataType);
    }
}
