package io.github.sunlaud.changegen;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sunlaud.changegen.change.ColumnChange;
import io.github.sunlaud.changegen.change.basic.DataTypeChange;
import io.github.sunlaud.changegen.dbinfo.key.JdbcDbMetadataExtractor;
import io.github.sunlaud.changegen.dbinfo.key.DbMetadataExtractor;
import io.github.sunlaud.changegen.model.Column;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeSetGeneratorIntegrationTest {
    private static final String CHANGESETS_DIR = "test-data/changeset-generator-it/expected-changesets";
    private static final DbMetadataExtractor KEY_EXTRACTOR = new JdbcDbMetadataExtractor(getDataSource());
    private final ChangeSetGenerator sut = new ChangeSetGenerator(KEY_EXTRACTOR);

    private static Stream<Arguments> changes() {
        return Stream.of(
                Arguments.of("NOT_REFERENCED", "ID", "bigint", "not_referenced_id__datatype_change.xml"),
                Arguments.of("EMPLOYEE", "FIRST_NAME", "bigint", "employee_id__datatype_change.xml"),
                Arguments.of("DEPARTMENT", "ID", "bigint", "department_id__datatype_change.xml")
                //Arguments.of("INDEXED_CONSTRAINED", "IDX1", "bigint", "indexed_constrained_idx__datatype_change.xml"),
                //Arguments.of("INDEXED_CONSTRAINED", "UNI2", "bigint", "indexed_constrained_uni__datatype_change.xml")
        );//.flatMap(addLowercaseParams()); //duplicate tests with lowercase
    }

    @MethodSource("changes")
    @ParameterizedTest
    void generatesChangesetsForDatatypeChange(String tableName, String columnName, String newDataType, final String fileWithExpectedContent) throws Exception {
        //GIVEN
        Column column = new Column(tableName, columnName);
        ColumnChange change = new DataTypeChange(column, newDataType);

        //WHEN
        String generated = sut.generateChangeset(change);

        //THEN
        assertThat(stripComments(generated)).isEqualTo(loadFile(fileWithExpectedContent));
    }


    @MethodSource("changes")
    @ParameterizedTest
    void addsCommentsToAddNotNullChangesets(String tableName, String columnName, String newDataType, final String fileWithExpectedContent) throws Exception {
        //GIVEN
        Column column = new Column(tableName, columnName);
        ColumnChange change = new DataTypeChange(column, newDataType);

        //WHEN
        String generated = sut.generateChangeset(change);

        //THEN
        String[] generatedLines = generated.split("\\n");
        assertThat(generatedLines)
                .filteredOn(line -> line.startsWith("<addNotNullConstraint"))
                .allMatch(line -> line.endsWith(" <!-- used instead of modifyDataType coz latter removes not null -->"));
    }

    private String stripComments(String generated) {
        return generated.replaceAll(">\\s*<!--[^>]+>", ">");
    }

    private String loadFile(String fileWithExpectedContent) throws IOException {
        return Resources.toString(Resources.getResource(CHANGESETS_DIR + "/" + fileWithExpectedContent), Charsets.UTF_8);
    }


    private static DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig("/test-data/changeset-generator-it/datasource.properties");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    private static Function<Arguments, Stream<? extends Arguments>> addLowercaseParams() {
        return args -> Stream.concat(Stream.of(args), Stream.of(Arguments.of(Stream.of(args.get()).map(String.class::cast).map(String::toLowerCase).toArray())));
    }
}