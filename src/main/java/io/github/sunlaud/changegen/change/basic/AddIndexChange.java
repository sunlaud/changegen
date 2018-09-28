package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.model.Index;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddIndexChange implements Change {
    @NonNull
    private final Index index;

    @Override
    public String generateXml() {
        return String.format("-<addIndex tableName=\"%s\" indexName=\"%s\"/>", index.getTableName(), index.getName());
    }
}
