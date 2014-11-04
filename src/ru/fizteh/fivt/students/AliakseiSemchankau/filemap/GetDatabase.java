package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Aliaksei Semchankau on 09.10.2014.
 */
public class GetDatabase {

    Path pathDatabase;
    boolean interactive;
    FileMap databaseInformation;

    public static void main(String[] args) {
        GetDatabase processDatabase = new GetDatabase(args);
    }

    GetDatabase(String[] args) {

        pathDatabase = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));

       // System.out.println(pathDatabase.toString());

        databaseInformation = new FileMap();

        databaseInformation.getPath(pathDatabase);

        interactive = args.length == 0;

        if (!Files.exists(pathDatabase)) {
            try {
                Files.createFile(pathDatabase);
            } catch (IOException ioexc) {
                throw new DatabaseException(pathDatabase + " cannot create such database");
            }

        }

        DatabaseWorker databaseProcess = new DatabaseWorker(pathDatabase);
        databaseInformation.readFile();

        if (interactive) {
            while (true) {
                databaseProcess.readCommands(null);
                try {
                    databaseProcess.doCommands(databaseInformation);
                } catch (DatabaseException dExc) {
                    System.out.println(dExc.getMessage());
                }
                if (databaseInformation.exit) {
                    break;
                }
            }
        } else {
            databaseProcess.readCommands(args);
            try {
                databaseProcess.doCommands(databaseInformation);
            } catch (DatabaseException dExc) {
                System.out.println(dExc.getMessage());
                System.exit(1);
            }
        }

        databaseInformation.writeFile();

    }

}
