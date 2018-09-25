package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.model.Column;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddNotNullConstraintChange implements Change {
    @NonNull
    private final Column column;
    @NonNull
    private final String dataType; //required for MSSQL, may be optional for other DBMS

    @Override
    public String generateXml() {
        return String.format("<addNotNullConstraint tableName=\"%s\" columnName=\"%s\" columnDataType=\"%s\"/>", column.getTableName(), column.getName(), dataType);
    }
}
