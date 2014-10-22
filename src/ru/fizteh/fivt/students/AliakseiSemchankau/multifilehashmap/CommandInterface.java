 package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Vector;

public interface CommandInterface {

    void makeCommand(Vector<String> args, HashMap<String, TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo);

}