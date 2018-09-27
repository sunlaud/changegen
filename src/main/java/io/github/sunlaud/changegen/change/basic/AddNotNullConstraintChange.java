package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.change.ColumnChange;
import io.github.sunlaud.changegen.model.Column;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddNotNullConstraintChange implements ColumnChange {
    @NonNull
    @Getter
    private final Column column;
    @NonNull
    private final String dataType; //required for MSSQL, may be optional for other DBMS
    private final String comment;

    @Override
    public String generateXml() {
        String xml = String.format("<addNotNullConstraint tableName=\"%s\" columnName=\"%s\" columnDataType=\"%s\"/>", column.getTableName(), column.getName(), dataType);
        if (comment != null) {
            xml = xml + " <!-- " + comment + " -->";
        }
        return xml;
    }

    @Override
    public ColumnChange applyTo(Column anotherColumn) {
        return new AddNotNullConstraintChange(anotherColumn, dataType, comment);
    }
}
