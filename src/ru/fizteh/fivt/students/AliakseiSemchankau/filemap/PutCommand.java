package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class PutCommand implements InterfaceCommand {

    @Override
    public void makeCommand(Vector<String> args, FileMap databaseInformation) {
        if (args.size() != 3) {
            throw new DatabaseException("incorrect number of arguments(put)");
        }
        String key = args.elementAt(1);
        String newValue = args.elementAt(2);

        String oldValue = databaseInformation.dbMap.get(key);

        databaseInformation.dbMap.put(key, newValue);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }

}
