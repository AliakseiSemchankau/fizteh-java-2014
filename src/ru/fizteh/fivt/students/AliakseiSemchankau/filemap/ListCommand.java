package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class ListCommand implements InterfaceCommand {

    @Override
    public void makeCommand(Vector<String> args, FileMap databaseInformation) {
        if (args.size() != 1) {
            throw new DatabaseException("too many arguments for list");
        }

        if (databaseInformation.dbMap.size() == 0) {
            System.out.println("");
            return;
        }

        Iterator<String> it = databaseInformation.dbMap.keySet().iterator();
        System.out.print(it.next());

        while (it.hasNext()) {
            System.out.print(", ");
            System.out.print(it.next());
        }
        System.out.println("");
    }
}
