package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */


public class CommandExit implements CommandInterface {
    @Override
    public void makeCommand(Vector<String> args, HashMap<String,
            TableInfo> referenceToTableInfo, DatabaseFullInformation dbInfo) {
        if (args.size() != 1) {
            throw new DatabaseException("wrong number of arguments for exit");
        }
        dbInfo.exitFlag = true;
    }
}
