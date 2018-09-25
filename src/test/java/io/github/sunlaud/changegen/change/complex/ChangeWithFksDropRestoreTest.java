package io.github.sunlaud.changegen.change.complex;

class ChangeWithFksDropRestoreTest {

//    @Test
//    void name() {
//        //GIVEN
//        Column changedeColumn = new Column("users", "id");
//        DataTypeChange dataTypeChange = new DataTypeChange(changedeColumn, "varchar(10)");
//        Columns pkColumns = new Columns(changedeColumn);
//        Key pk = new Key("pk_users", pkColumns);
//        ForeignKey fk1 = new ForeignKey(new Key("fk_phones_users", new Column("phones", "user_id")), pkColumns);
//        ForeignKey fk2 = new ForeignKey(new Key("fk_addresses_users", new Column("addresses", "user_id")), pkColumns);
//
//        ChangeWithPkDropRestore pkDataTypeChange = new ChangeWithPkDropRestore(dataTypeChange, pk);
//        ChangeWithFksDropRestore change = new ChangeWithFksDropRestore(pkDataTypeChange, Arrays.asList(fk1, fk2));
//
//        //WHEN
//        String generated = change.generateXml();
//
//        //THEN
//        System.out.println(generated);
//    }
}