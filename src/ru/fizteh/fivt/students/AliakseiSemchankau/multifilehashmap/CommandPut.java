 package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class CommandPut implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo,
                            DatabaseFullInformation dbInfo) {

        if (args.size() != 3) {
            throw new DatabaseException("incorrect number of arguments(put)");
        }
        String key = args.elementAt(1);
        String newValue = args.elementAt(2);

        TableInfo currentTable = referenceToTableInfo.get(dbInfo.currentTableName);

        String oldValue = currentTable.dbMap.get(key);

        currentTable.dbMap.put(key, newValue);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }

}
