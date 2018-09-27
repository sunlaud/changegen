package io.github.sunlaud.changegen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sunlaud.changegen.change.ColumnChange;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.dbinfo.key.JdbcDbMetadataExtractor;
import io.github.sunlaud.changegen.dbinfo.key.DbMetadataExtractor;
import io.github.sunlaud.changegen.model.Column;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        DbMetadataExtractor metadataExtractor = new JdbcDbMetadataExtractor(getDataSource());
        ChangeSetGenerator changeSetGenerator = new ChangeSetGenerator(metadataExtractor);

        ColumnChange change = new DataTypeChange(new Column("EMPLOYEE", "FIRST_NAME"), "varchar(10)");

        System.out.println(changeSetGenerator.generateChangeset(change));
    }

    private static DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig("/datasource.properties");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }
}
