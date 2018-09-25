package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ColumnReference {
    @NonNull
    private final Column sourceColumn;
    @NonNull
    private final Column targetColumn;
}
