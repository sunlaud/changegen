package io.github.sunlaud.changegen.change;

import io.github.sunlaud.changegen.model.Column;

public interface ColumnChange extends Change {
    Column getColumn();
    ColumnChange applyTo(Column anotherColumn);
}
