package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Column {
    @NonNull
    private final String tableName;
    @NonNull
    private final String name;
}
