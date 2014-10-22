 package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class CommandList implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo) {

        if (args.size() != 1) {
            throw new DatabaseException("too many arguments for list");
        }

        TableInfo currentTable = referenceToTableInfo.get(dbInfo.currentTableName);

        if (currentTable.dbMap.size() == 0) {
            System.out.println("");
            return;
        }

        Iterator<String> it = currentTable.dbMap.keySet().iterator();
        System.out.print(it.next());

        while (it.hasNext()) {
            System.out.print(", ");
            System.out.print(it.next());
        }
        System.out.println("");
    }
}