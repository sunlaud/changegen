package io.github.sunlaud.changegen.dbinfo.key;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.model.ColumnReference;
import io.github.sunlaud.changegen.model.Columns;
import io.github.sunlaud.changegen.model.ForeignKey;
import io.github.sunlaud.changegen.model.Key;
import io.github.sunlaud.changegen.model.TypedColumn;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.singletonList;

public class JdbcDbMetadataExtractor implements DbMetadataExtractor {
    private final DataSource dataSource;
    private final DefaultResultSetHandlerFactoryBuilder resultSetHandlerFactoryBuilder;

    @FunctionalInterface
    private interface MetadaResultsetProvider {
        ResultSet provideFrom(DatabaseMetaData metaData) throws SQLException;
    }

    public JdbcDbMetadataExtractor(DataSource dataSource) {
        this.dataSource = dataSource;
        resultSetHandlerFactoryBuilder = new DefaultResultSetHandlerFactoryBuilder();
        resultSetHandlerFactoryBuilder.setQuirks(new Sql2o(this.dataSource).getQuirks());
        resultSetHandlerFactoryBuilder.setAutoDeriveColumnNames(true);
        resultSetHandlerFactoryBuilder.setCaseSensitive(false);
    }

    @Override
    public Optional<Key> getPk(@NonNull String tableName) {
        getTableColumnNames(tableName); //just check if table exists, exception if not
        List<PkDto> pkDtos = queryForMeta(PkDto.class, metaData -> metaData.getPrimaryKeys(null, null, tableName));
        return pkDtos.stream()
                .map(dto -> new Key(dto.getPkName(), new Column(dto.getTableName(), dto.getColumnName())))
                .reduce(Key::compose);
    }

    @Override
    public TypedColumn getColumnInfo(@NonNull Column column) {
        getTableColumnNames(column.getTableName()).contains(column.getName())
        List<ColumnDto> columnDtos = queryForMeta(ColumnDto.class, metaData -> metaData.getColumns(null, null, column.getTableName(), column.getName()));
        checkState(columnDtos.size() == 1, "Expected exactly 1 column with name %s in table %s, but got %s: %s", column.getName(), column.getTableName(), columnDtos.size(), columnDtos);
        ColumnDto dto = Iterables.getOnlyElement(columnDtos);
        return new TypedColumn(dto.getTableName(), dto.getColumnName(), dto.getType(), dto.getColumnSize(), dto.isNotNull());
     }

    @Override
    public Collection<ForeignKey> getFkReferncing(@NonNull Columns referencedColumns) {
        List<FkDto> keys = queryForMeta(FkDto.class, metadata -> metadata.getExportedKeys(null, null, referencedColumns.getTableName()));
        Map<String, Collection<FkDto>> keysByTable = Multimaps.index(keys, FkDto::getFkName).asMap();
        return keysByTable.entrySet().stream()
                .map(Entry::getValue)
                .map(this::buildFk)
                .filter(fk -> fk.getReferencedColumns().contain(referencedColumns))
                .collect(Collectors.toList());
    }

    private void columnExists(Column column) {

    }

    @SneakyThrows
    private Set<String> getTableColumnNames(String tableName) {
        try(Connection connection = dataSource.getConnection()) {
            //use prepared statement to query columns metadata for performance (sic!) (see https://www.progress.com/tutorials/jdbc/designing-performance#database-metadata-methods)
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE 1 = 0"); // query is never executed on the server - only prepared
            ResultSetMetaData metaData = statement.getMetaData();
            Set<String> columnNames = new HashSet<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            return columnNames;
        }
    }

    @SneakyThrows
    private <T> List<T> queryForMeta(Class<T> resultClass, MetadaResultsetProvider resultsetProvider) {
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet metadataResultSet = resultsetProvider.provideFrom(metaData);
            ResultSetHandlerFactory<T> resultSetHandlerFactory = resultSetHandlerFactoryBuilder.newFactory(resultClass);
            PojoResultSetIterator<T> resultSetIterator = new PojoResultSetIterator<>(metadataResultSet, true, resultSetHandlerFactoryBuilder.getQuirks(), resultSetHandlerFactory);
            return Lists.newArrayList(resultSetIterator);
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

    /** Field names depend on columns from {@link DatabaseMetaData#getColumns} */
    @Data
    private static class ColumnDto {
        private final String tableName;
        private final String columnName;
        private final String typeName;
        private final int dataType;
        private final int columnSize;
        @Getter(AccessLevel.NONE)
        private final int nullable;

        public JDBCType getType() {
            return JDBCType.valueOf(dataType);
        }

        public boolean isNotNull() {
            return nullable == DatabaseMetaData.columnNoNulls;
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
}
