package io.github.sunlaud.changegen;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sunlaud.changegen.extractor.db.key.MsSqlKeyExtractor;
import io.github.sunlaud.changegen.generator.Column;
import io.github.sunlaud.changegen.generator.change.ColumnChange;
import io.github.sunlaud.changegen.generator.change.basic.DataTypeChange;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        MsSqlKeyExtractor keyExtractor = new MsSqlKeyExtractor(new DBI(getDataSource()));
        ChangeSetGenerator changeSetGenerator = new ChangeSetGenerator(keyExtractor);

        ColumnChange change = new DataTypeChange(new Column("business_category", "id"), "varchar(10)");

        System.out.println(changeSetGenerator.generateChangeset(change));
    }

    private static DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig("/datasource.properties");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }
}
