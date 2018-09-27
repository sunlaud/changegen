package io.github.sunlaud.changegen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sunlaud.changegen.change.ColumnChange;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.dbinfo.key.DbMetadataExtractor;
import io.github.sunlaud.changegen.dbinfo.key.JdbcDbMetadataExtractor;
import io.github.sunlaud.changegen.model.Column;
import io.github.sunlaud.changegen.model.TypedColumn;

import javax.sql.DataSource;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        DbMetadataExtractor metadataExtractor = new JdbcDbMetadataExtractor(getDataSource());
        ChangeSetGenerator changeSetGenerator = new ChangeSetGenerator(metadataExtractor);

        Collection<TypedColumn> columns = metadataExtractor.findColumnsByName("%_FIRST_NAME");
        columns.stream()
                //.filter(column -> column.getSize() != 10)
                .map(column -> {
                    DataTypeChange change = new DataTypeChange(column.withoutType(), "varchar(20)");
                    if (column.isNullable()) return change;
                    else return change.withNotNull();
                })
                .map(changeSetGenerator::generateChangeset)
                .forEach(System.out::println);

        ColumnChange change = new DataTypeChange(new Column("EMPLOYEE", "FIRST_NAME"), "varchar(20)");

        //System.out.println(changeSetGenerator.generateChangeset(change));
    }

    private static DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig("/datasource.properties");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }
}
