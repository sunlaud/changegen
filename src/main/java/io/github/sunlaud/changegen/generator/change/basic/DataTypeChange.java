package io.github.sunlaud.changegen.generator.change.basic;


import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.change.SingleColumnChange;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataTypeChange implements SingleColumnChange {
    @Getter
    @NonNull
    private final Column column;
    @NonNull
    private final String newDataType;

    @Override
    public String generateXml() {
        return String.format("<modifyDataType tableName=\"%s\" columnName=\"%s\" newDataType=\"%s\"/>", column.getTableName(), column.getName(), newDataType);
    }
}
