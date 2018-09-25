package io.github.sunlaud.changegen.model;

import com.google.common.collect.Iterables;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@EqualsAndHashCode(callSuper = true)
@Data
public class ForeignKey extends Key {
    private final Collection<ColumnReference> references;

    public ForeignKey(@NonNull Key key, @NonNull Collection<ColumnReference> references) {
        super(key.getName(), key.getColumns());
        this.references = references;
    }

    /** Returns composite key with columns of this key and columns from other key */
    public ForeignKey compose(@NonNull ForeignKey other) {
        Key composedKey = super.compose(other);
        Set<ColumnReference> composedReferences = new HashSet<>(references);
        composedReferences.addAll(other.references);
        return new ForeignKey(composedKey, composedReferences);
    }

    public Columns getReferenceColumns() {
        return references.stream()
                .map(ColumnReference::getTargetColumn)
                .collect(collectingAndThen(toList(), Columns::new));
    }

    public Column getColumnReferencing(@NonNull Column target) {
        List<Column> targets = references.stream()
                .filter(ref -> ref.getTargetColumn().equals(target))
                .map(ColumnReference::getSourceColumn)
                .collect(toList());
        return Iterables.getOnlyElement(targets);
    }
}
