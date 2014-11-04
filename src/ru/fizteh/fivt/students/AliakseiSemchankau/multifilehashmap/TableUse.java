package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableUse implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo,
                            DatabaseFullInformation dbInfo) {
        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments for use");
        }

        if (referenceToTableInfo.get(args.elementAt(1)) == null) {
            System.out.println("no such table");
            return;
        }
        dbInfo.currentTableName = args.elementAt(1);
        dbInfo.currentTable = Paths.get(dbInfo.pathDatabase.toString()).resolve(dbInfo.currentTableName);

    }
}
