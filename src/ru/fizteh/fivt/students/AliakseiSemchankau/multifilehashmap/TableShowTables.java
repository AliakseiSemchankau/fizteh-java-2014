package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableShowTables implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo) {

        for (Map.Entry<String, TableInfo> entry : referenceToTableInfo.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().dbMap.size());
        }
    }
}
