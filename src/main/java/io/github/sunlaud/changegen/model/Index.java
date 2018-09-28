package io.github.sunlaud.changegen.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import static com.google.common.base.Preconditions.checkState;

@EqualsAndHashCode(callSuper = true)
@Data
public class Index extends Key {
    private final boolean unique;

    public Index(Key key, boolean unique) {
        this(key.getName(), key.getColumns(), unique);
    }

    public Index(String name, Columns columns, boolean unique) {
        super(name, columns);
        this.unique = unique;
    }

    public Index(String name, Column column, boolean unique) {
        super(name, column);
        this.unique = unique;
    }

    public Index compose(@NonNull Index other) {
        checkState(unique == other.unique);
        return new Index(super.compose(other), unique);
    }
}
