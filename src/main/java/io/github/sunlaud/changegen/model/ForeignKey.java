package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ForeignKey extends Key {
    private final Columns referenceColumns;

    public ForeignKey(@NonNull Key key, @NonNull Columns referenceColumns) {
        super(key.getName(), key.getColumns());
        this.referenceColumns = referenceColumns;
    }
}
