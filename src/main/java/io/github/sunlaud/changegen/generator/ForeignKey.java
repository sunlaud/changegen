package io.github.sunlaud.changegen.generator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ForeignKey extends Key {
    private final Columns referenceColumns;

    public ForeignKey(Key key, Columns referenceColumns) {
        super(key.getName(), key.getColumns());
        this.referenceColumns = referenceColumns;
    }
}
