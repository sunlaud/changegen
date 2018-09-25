package io.github.sunlaud.changegen.dbinfo.key;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.model.ColumnReference;
import io.github.sunlaud.changegen.model.Columns;
import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.model.Key;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.sql2o.DefaultResultSetHandlerFactoryBuilder;
import org.sql2o.PojoResultSetIterator;
import org.sql2o.ResultSetHandlerFactory;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;

public class JdbcKeyExtractor implements KeyExtractor {
    private final DataSource dataSource;
    private final DefaultResultSetHandlerFactoryBuilder resultSetHandlerFactoryBuilder;

    public JdbcKeyExtractor(DataSource dataSource) {
        this.dataSource = dataSource;
        resultSetHandlerFactoryBuilder = new DefaultResultSetHandlerFactoryBuilder();
        resultSetHandlerFactoryBuilder.setQuirks(new Sql2o(this.dataSource).getQuirks());
        resultSetHandlerFactoryBuilder.setAutoDeriveColumnNames(true);
        resultSetHandlerFactoryBuilder.setCaseSensitive(false);
    }

    @SneakyThrows
    @Override
    public Key getPk(@NonNull String tableName) {
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeysInfo = metaData.getPrimaryKeys(null, null, tableName);
            ResultSetHandlerFactory<PkDto> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(PkDto.class);
            PojoResultSetIterator<PkDto> keysIterator = new PojoResultSetIterator<>(primaryKeysInfo, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);
            return Lists.newArrayList(keysIterator).stream()
                    .map(dto -> new Key(dto.getPkName(), new Column(dto.getTableName(), dto.getColumnName())))
                    .reduce(Key::compose)
                    .orElseThrow(() -> new IllegalStateException("Table " + tableName + " has no primary key (table exists? name in wrong case? no PK?)"));
        }
    }

    @SneakyThrows
    @Override
    public Collection<ForeignKey> getFkReferncing(@NonNull Columns referencedColumns) {
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet exportedKeysInfo = metaData.getExportedKeys(null, null, referencedColumns.getTableName());
            ResultSetHandlerFactory<FkDto> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(FkDto.class);
            PojoResultSetIterator<FkDto> keysIterator = new PojoResultSetIterator<>(exportedKeysInfo, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);
            Map<String, Collection<FkDto>> keysByTable = Multimaps.index(keysIterator, FkDto::getFkName).asMap();
            return keysByTable.entrySet().stream()
                    .map(Entry::getValue)
                    .map(this::buildFk)
                    .collect(Collectors.toList());
        }
    }

    private ForeignKey buildFk(Collection<FkDto> fkDtos) {
        checkArgument(!fkDtos.isEmpty());
        return fkDtos.stream()
                .map(dto -> new ForeignKey(new Key(dto.getFkName(), new Column(dto.getFktableName(), dto.getFkcolumnName())), singletonList(asColumnReference(dto))))
                .reduce(ForeignKey::compose)
                .get();
    }

    private ColumnReference asColumnReference(FkDto dto) {
        return new ColumnReference(new Column(dto.getFktableName(), dto.getFkcolumnName()), new Column(dto.getPktableName(), dto.getPkcolumnName()));
    }

    /** Field names depend on columns from {@link DatabaseMetaData#getPrimaryKeys} */
    @Data
    private static class PkDto {
        private final String tableName;
        private final String columnName;
        private final String pkName;
    }

    /** Field names depend on columns from {@link DatabaseMetaData#getExportedKeys} */
    @Data
    private static class FkDto {
        private final String pktableName;
        private final String pkcolumnName;
        private final String fktableName;
        private final String fkcolumnName;
        private final String fkName;
    }
}
