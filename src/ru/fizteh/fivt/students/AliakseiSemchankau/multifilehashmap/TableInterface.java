package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public interface TableInterface {

    void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo);
}
