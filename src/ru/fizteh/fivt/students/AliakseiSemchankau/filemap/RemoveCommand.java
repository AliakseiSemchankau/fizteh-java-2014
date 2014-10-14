    package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

    import java.util.Vector;

    /**
     * Created by Aliaksei Semchankau on 13.10.2014.
     */
    public class RemoveCommand implements InterfaceCommand {

        @Override
        public void makeCommand(Vector<String> args, FileMap databaseInformation) {
            if (args.size() != 2) {
                throw new DatabaseException("incorrect number for arguments(remove)");
            }
            String key = args.elementAt(1);
            String value = databaseInformation.dbMap.get(key);
            if (value == null) {
                System.out.println("not found");
                return;
            }
            databaseInformation.dbMap.remove(key);
            System.out.println("removed");
        }

    }


