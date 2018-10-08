package io.github.sunlaud.changegen.change.basic;

import com.sun.istack.internal.NotNull;
import io.github.sunlaud.changegen.model.Column;
import lombok.NonNull;

public class AddNotNullConstraintChange extends DataTypeChange {
    private final String comment;

    public AddNotNullConstraintChange(@NonNull Column column, @NotNull String newDataType, String comment) {
        super(column, newDataType);
        this.comment = comment;
    }

    @Override
    public String generateXml() {
        String xml = String.format("<addNotNullConstraint tableName=\"%s\" columnName=\"%s\" columnDataType=\"%s\"/>", column.getTableName(), column.getName(), dataType);
        if (comment != null) {
            xml = xml + " <!-- " + comment + " -->";
        }
        return xml;
    }

    @Override
    public DataTypeChange applyTo(Column anotherColumn) {
        return new AddNotNullConstraintChange(anotherColumn, dataType, comment);
    }
}
