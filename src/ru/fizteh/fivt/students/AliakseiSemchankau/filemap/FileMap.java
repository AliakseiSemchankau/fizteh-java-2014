package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Aliaksei Semchankau on 09.10.2014.
 */
public class FileMap {
    Path pathFile;
    HashMap<String, String> dbMap;
    boolean exit;

    public void getPath(Path pathDatabase) {
        pathFile = pathDatabase;
        exit = false;
        dbMap = new HashMap<String, String>();
    }

    public void readFile() {
        if (!Files.exists(pathFile)) {
            throw new DatabaseException("database doesnt exist");
        }

        try {
            DataInputStream databaseFile = new DataInputStream(Files.newInputStream(pathFile));

            while (databaseFile.available() > 0) {
                int byteKeyLength = databaseFile.readInt();

               // System.out.println(byteKeyLength);

                if (databaseFile.available() < byteKeyLength) {
                    throw new DatabaseException("incorrect bytelength of key");
                }

                byte[] key = new byte[byteKeyLength];
                databaseFile.read(key, 0, byteKeyLength);

               // System.out.println(Key.toString());

                int byteValueLength = databaseFile.readInt();

               // System.out.println(byteValueLength);

                if (databaseFile.available() < byteValueLength) {
                    throw new DatabaseException("incorrect bytelength of value");
                }



                byte[] value = new byte[byteValueLength];
                databaseFile.read(value, 0, byteValueLength);

               // System.out.println(Value);

                dbMap.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            }

        } catch (IOException ioexc) {
            throw new DatabaseException("can't open file as an input stream");
        }
    }

    public void writeFile() {
        if (dbMap.size() == 0) {
            return;
        }
        try {
            DataOutputStream databaseFile = new DataOutputStream(Files.newOutputStream(pathFile));

            Iterator<String> it = dbMap.keySet().iterator();
            String key = it.next();
            String value = dbMap.get(key);
            byte[] byteKey = key.getBytes("UTF-8");
            byte[] byteValue = value.getBytes("UTF-8");
            databaseFile.writeInt(byteKey.length);
            databaseFile.write(byteKey);
            databaseFile.writeInt(byteValue.length);
            databaseFile.write(byteValue);


            while (it.hasNext()) {
                key = it.next();
                value = dbMap.get(key);
                byteKey = key.getBytes("UTF-8");
                byteValue = value.getBytes("UTF-8");
                databaseFile.writeInt(byteKey.length);
                databaseFile.write(byteKey);
                databaseFile.writeInt(byteValue.length);
                databaseFile.write(byteValue);

                }
        } catch (IOException iexc) {
            throw new DatabaseException("something wrong with writing to file");
        }
    }


}
