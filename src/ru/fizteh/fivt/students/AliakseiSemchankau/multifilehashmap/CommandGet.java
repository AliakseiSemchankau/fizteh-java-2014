package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandGet implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo) {

        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments(get)");
        }

        TableInfo curTable = referenceToTableInfo.get(dbInfo.currentTableName);

        String key = args.elementAt(1);
        String value = curTable.dbMap.get(key);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

}
