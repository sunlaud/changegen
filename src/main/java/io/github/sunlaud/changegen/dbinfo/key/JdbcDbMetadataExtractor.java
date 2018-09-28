package io.github.sunlaud.changegen.dbinfo.key;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.model.ColumnReference;
import io.github.sunlaud.changegen.model.Columns;
import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.model.Index;
import io.github.sunlaud.changegen.model.Key;
import io.github.sunlaud.changegen.model.TypedColumn;
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
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.singletonList;

public class JdbcDbMetadataExtractor implements DbMetadataExtractor {
    private final DataSource dataSource;
    private final DefaultResultSetHandlerFactoryBuilder resultSetHandlerFactoryBuilder;

    public JdbcDbMetadataExtractor(DataSource dataSource) {
        this.dataSource = dataSource;
        resultSetHandlerFactoryBuilder = new DefaultResultSetHandlerFactoryBuilder();
        resultSetHandlerFactoryBuilder.setQuirks(new Sql2o(this.dataSource).getQuirks());
        resultSetHandlerFactoryBuilder.setAutoDeriveColumnNames(true);
        resultSetHandlerFactoryBuilder.setCaseSensitive(false);
    }

    @SneakyThrows
    @Override
    public Optional<Key> getPk(@NonNull String tableName) {
        //TODO check if table exists
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeysInfo = metaData.getPrimaryKeys(null, null, tableName);
            ResultSetHandlerFactory<PkDto> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(PkDto.class);
            PojoResultSetIterator<PkDto> keysIterator = new PojoResultSetIterator<>(primaryKeysInfo, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);
            return Lists.newArrayList(keysIterator).stream()
                    .map(dto -> new Key(dto.getPkName(), new Column(dto.getTableName(), dto.getColumnName())))
                    .reduce(Key::compose);
        }
    }

    @SneakyThrows
    @Override
    public Collection<Index> getIndexesContaining(@NonNull Column column) {
        if (column.getTableName().equalsIgnoreCase("fp_redraw")) {
            System.out.println("----------get index for " + column);
        }
        //TODO check if table exists
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeysInfo = metaData.getIndexInfo(null, null, column.getTableName(), false, false);
            ResultSetHandlerFactory<IndexDto> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(IndexDto.class);
            PojoResultSetIterator<IndexDto> indexIterator = new PojoResultSetIterator<>(primaryKeysInfo, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);

            ArrayList<IndexDto> indexDtos1 = Lists.newArrayList(indexIterator);
            List<IndexDto> indexDtos = indexDtos1.stream()
                    .filter(dto -> dto.getIndexName() != null)
                    .filter(dto -> !dto.isStatistic())
                    .collect(Collectors.toList());
            Map<String, Collection<IndexDto>> indeciesByName = Multimaps.index(indexDtos, IndexDto::getIndexName).asMap();
            return indeciesByName.entrySet().stream()
                    .map(Entry::getValue)
                    .map(this::buildIndex)
                    .filter(index -> index.getColumns().contain(column))
                    .collect(Collectors.toList());
        }
    }

    @Override
    @SneakyThrows
    public TypedColumn getColumnInfo(@NonNull Column column) {
        //TODO check if table exists
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columnsInfo = metaData.getColumns(null, null, column.getTableName(), column.getName());
            ResultSetHandlerFactory<ColumnDto> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(ColumnDto.class);
            PojoResultSetIterator<ColumnDto> columnsIterator = new PojoResultSetIterator<>(columnsInfo, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);
            ArrayList<ColumnDto> columnDtos = Lists.newArrayList(columnsIterator);
            checkState(columnDtos.size() == 1, "Expected exactly 1 column with name %s in table %s, but got %s: %s", column.getName(), column.getTableName(), columnDtos.size(), columnDtos);
            ColumnDto dto = Iterables.getOnlyElement(columnDtos);
            return new TypedColumn(dto.getTableName(), dto.getColumnName(), dto.getType(), dto.getColumnSize(), dto.isNullable());
        }
    }

    @SneakyThrows
    @Override
    public Collection<ForeignKey> getFkReferncing(@NonNull Columns referencedColumns) {
        //TODO check if table exists
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet exportedKeysInfo = metaData.getExportedKeys(null, null, referencedColumns.getTableName());
            ResultSetHandlerFactory<FkDto> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(FkDto.class);
            PojoResultSetIterator<FkDto> keysIterator = new PojoResultSetIterator<>(exportedKeysInfo, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);
            Map<String, Collection<FkDto>> keysByTable = Multimaps.index(keysIterator, FkDto::getFkName).asMap();
            return keysByTable.entrySet().stream()
                    .map(Entry::getValue)
                    .map(this::buildFk)
                    .filter(fk -> fk.getReferencedColumns().contain(referencedColumns))
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

    private Index buildIndex(Collection<IndexDto> indexDtos) {
        checkArgument(!indexDtos.isEmpty());
        return indexDtos.stream()
                .map(dto -> new io.github.sunlaud.changegen.model.Index(dto.getIndexName(), new Column(dto.getTableName(), dto.getColumnName()), !dto.isNonUnique()))
                .reduce(Index::compose)
                .get();
    }

    private ColumnReference asColumnReference(FkDto dto) {
        return new ColumnReference(new Column(dto.getFktableName(), dto.getFkcolumnName()), new Column(dto.getPktableName(), dto.getPkcolumnName()));
    }

    /** Field names depend on columns from {@link DatabaseMetaData#getColumns} */
    @Data
    private static class ColumnDto {
        private final String tableName;
        private final String columnName;
        private final String typeName;
        private final int dataType;
        private final int columnSize;
        private final boolean nullable;

        public JDBCType getType() {
            return JDBCType.valueOf(dataType);
        }
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

    /** Field names depend on columns from {@link DatabaseMetaData#getIndexInfo} */
    @Data
    private static class IndexDto {
        private final String tableName;
        private final String columnName;
        private final boolean nonUnique;
        private final String indexName;
        private final short type;

        public boolean isClustered() {
            return type == DatabaseMetaData.tableIndexClustered;
        }

        public boolean isStatistic() {
            return type == DatabaseMetaData.tableIndexStatistic;
        }
    }
}
