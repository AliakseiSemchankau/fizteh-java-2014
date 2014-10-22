package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableCreate implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo) {
        if (args.size() != 2) {
            throw new DatabaseException("wrong number of args for create");
        }

        Path pathToTable = Paths.get(dbInfo.pathDatabase.toString()).resolve(args.elementAt(1));
        referenceToTableInfo.put(args.elementAt(1), new TableInfo(pathToTable, dbInfo.pathDatabase));
    }
}
