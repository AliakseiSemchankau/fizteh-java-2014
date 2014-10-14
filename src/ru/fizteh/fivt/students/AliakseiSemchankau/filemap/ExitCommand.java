package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class ExitCommand implements InterfaceCommand {

    @Override
    public void makeCommand(Vector<String> args, FileMap databaseInformation) {
        if (args.size() != 1) {
            throw new DatabaseException("wrong number of arguments for exit");
        }
        databaseInformation.exit = true;
    }
}
