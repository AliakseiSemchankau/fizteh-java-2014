package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableDrop implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo) {
        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments for Drop");
        }
        if (referenceToTableInfo.get(args.elementAt(1)) == null) {
            System.out.println("no such table");
            return;
        }
        Path pathToRemovingTable = referenceToTableInfo.get(args.elementAt(1)).pathToTable;
        if (Files.exists(pathToRemovingTable)) {
            DeleteFunctions.deleteTable(pathToRemovingTable);
        }
        referenceToTableInfo.remove(args.elementAt(1));
    }
}
