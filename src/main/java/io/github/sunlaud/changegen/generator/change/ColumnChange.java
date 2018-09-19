package io.github.sunlaud.changegen.generator.change;

import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.change.basic.DataTypeChange;

public interface ColumnChange extends Change {
    Column getColumn();
    DataTypeChange applyTo(Column anotherColumn);
}
