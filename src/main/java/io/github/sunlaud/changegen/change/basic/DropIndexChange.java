package io.github.sunlaud.changegen.change.basic;

import io.github.sunlaud.changegen.change.Change;
import io.github.sunlaud.changegen.model.Index;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropIndexChange implements Change {
    @NonNull
    private final Index index;

    @Override
    public String generateXml() {
        return String.format("-<dropIndex tableName=\"%s\" indexName=\"%s\"/>", index.getTableName(), index.getName());
    }
}
