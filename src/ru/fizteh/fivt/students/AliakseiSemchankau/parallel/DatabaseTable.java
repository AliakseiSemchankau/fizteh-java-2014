package ru.fizteh.fivt.students.AliakseiSemchankau.parallel;

import javafx.util.Pair;
import ru.fizteh.fivt.storage.structured.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class DatabaseTable implements Table {

    Path pathToTable;
    HashMap<String, Storeable> realDBMap;
   // HashMap<String, Storeable> localDBMap;
    int unsavedChanges;
    private ArrayList<Class<?>> signature = new ArrayList<Class<?>>();
    SerializeFunctions serializer = new SerializeFunctions();
    Path fatherDirectory;
    String tableName;
    ReentrantReadWriteLock lock;
    ThreadLocal<DatabaseTableDiff> diff;
    int version = 0;

    DatabaseTable(Path pathTable, Path directory) {
        pathToTable = pathTable;
        pathToTable.normalize();

        realDBMap = new HashMap<String, Storeable>();
        //localDBMap = new HashMap<String, Storeable>();
        unsavedChanges = 0;
        fatherDirectory = directory;
        lock = new ReentrantReadWriteLock();
        lock.readLock().lock();

        try {
            diff = new ThreadLocal<DatabaseTableDiff>() {
                @Override
                protected DatabaseTableDiff initialValue() {
                    return new DatabaseTableDiff(DatabaseTable.this, lock);
                }
            };
        } finally {
            lock.readLock().unlock();

        }
    }

    DatabaseTable(Path pathTable, Path directory, List<Class<?>> columnTypes) {
        ArrayList<Class<?>> arrayListColumnTypes = new ArrayList<Class<?>>(columnTypes);
        pathToTable = pathTable;
        pathToTable.normalize();

        realDBMap = new HashMap<String, Storeable>();
        //localDBMap = new HashMap<String, Storeable>();
        unsavedChanges = 0;
        signature = arrayListColumnTypes;
        fatherDirectory = directory;

        lock = new ReentrantReadWriteLock();
        lock.readLock().lock();

        try {
            diff = new ThreadLocal<DatabaseTableDiff>() {
                @Override
                protected DatabaseTableDiff initialValue() {
                    return new DatabaseTableDiff(DatabaseTable.this, lock);
                }
            };
        } finally {
            lock.readLock().unlock();
        }

    }

    public void openTable()  {

        lock.readLock().lock();
        try {
            takeSignature();

            //DirectoryStream<Path> listOfDirs;

            for (int i = 0; i < 16; ++i) {
                String name = Integer.toString(i);
                if (i > 0 && i < 10) {
                    name = ("0" + name);

                }
                name += ".dir";
                Path innerDirection = pathToTable.resolve(name);
                if (!Files.exists(innerDirection)) {
                    continue;
                }
                if (!Files.isDirectory(innerDirection)) {
                    throw new DatabaseException(innerDirection + ": isn't a directiion");
                }
                openDirection(innerDirection);

            }
        } finally {
            lock.readLock().unlock();
        }

       /* for (Path innerDirection : listOfDirs) {
            if (!Files.isDirectory(innerDirection))
            openDirection(innerDirection);
        }
        try {
            listOfDirs.close();
        } catch (IOException ioexc) {
            throw new DatabaseException("can't close list of directories of " + pathToTable.toString());
        }*/

        //localDBMap = new HashMap<String, Storeable>(dbMap);

    }

    public void openDirection(Path innerDirection) {

        DirectoryStream<Path> listOfFiles;
        try {
            listOfFiles = Files.newDirectoryStream(innerDirection);
        } catch (IOException ioexc) {
            throw new DatabaseException(pathToTable + ": can't make a list of files");
        }

        for (Path innerFile : listOfFiles) {
            if (!Files.exists(innerFile)) {
                throw new DatabaseException(innerFile + ": isn't a file");
            }
            openFile(innerFile);
        }

        try {
            listOfFiles.close();
        } catch (IOException ioexc) {
            throw new DatabaseException("can't close list of directories of " + innerDirection.toString());
        }
    }

    public void openFile(Path innerFile) {

        if (!Files.exists(innerFile)) {
            throw new DatabaseException("database doesnt exist");
        }

        try {
            DataInputStream databaseFile = new DataInputStream(Files.newInputStream(innerFile));

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
                try {
                    DatabaseStoreable curStore = serializer.deserialize(signature, new String(value, "UTF-8"));
                    realDBMap.put(new String(key, "UTF-8"), curStore);
                } catch (ParseException pexc) {
                    throw new DatabaseException("can't deserialize " + value.toString());
                }
            }

            try {
                databaseFile.close();
            } catch (IOException ioexc) {
                throw new DatabaseException("can't close databaseFile " + innerFile.toString());
            }
        } catch (IOException ioexc) {
            throw new DatabaseException("can't open file as an input stream " + innerFile);
        }

    }

    public void takeSignature() {
        try  {
            //BufferedReader bReader = new BufferedReader(new FileReader(pathToTable.toString()));
            BufferedReader bReader = Files.newBufferedReader(pathToTable.resolve("signature.tsv"));

            String signatures = bReader.readLine();
            if (signatures == null || signatures.length() == 0) {
                throw new DatabaseException(pathToTable.resolve("signature.tsv") + " is empty");
            }
            for (String currentType : signatures.split(" ")) {
                signature.add(serializer.toClass(currentType));
            }

            bReader.close();
        } catch (IOException ioexc) {
            throw new DatabaseException("impossible to take signature from " + pathToTable.toString());
        }
    }

    public void makeSignature() {

        Path signatureToCreate = pathToTable.resolve("signature.tsv");

        try {
            Files.createFile(signatureToCreate);
        } catch (IOException ioexc) {
            throw new DatabaseException("can't create signature for " + pathToTable.toString());
        }
        try  {
            //BufferedReader bReader = new BufferedReader(new FileReader(pathToTable.toString()));
            BufferedWriter bWriter = Files.newBufferedWriter(signatureToCreate);

            for (Class<?> currentType : signature) {
                bWriter.write(serializer.toString(currentType) + " ");
            }

            bWriter.close();

        } catch (IOException ioexc) {
            throw new DatabaseException("impossible to take signature from " + pathToTable.toString());
        }
    }

    public void writeTable() {

        Vector<Vector<Vector<Pair<String, Storeable>>>> mapDirectoriesFiles =
                new Vector<Vector<Vector<Pair<String, Storeable>>>>();

        for (int i = 0; i < 16; ++i) {
            mapDirectoriesFiles.add(new Vector<Vector<Pair<String, Storeable>>>());
            for (int j = 0; j < 16; ++j) {
                mapDirectoriesFiles.elementAt(i).add(new Vector<Pair<String, Storeable>>());
            }
        }

        lock.writeLock().lock();
        try {
            if (!Files.exists(pathToTable)) {
                try {
                    Files.createDirectory(pathToTable);
                } catch (IOException ioexc) {
                    throw new DatabaseException("can't create " + pathToTable);
                }
            }

            DirectoryStream<Path> listOfDirectories;        // --------- delete all directories

            try {
                listOfDirectories = Files.newDirectoryStream(pathToTable);

                for (Path directoryToDelete : listOfDirectories) {
                    //System.out.println(directoryToDelete.toString());
                    if (directoryToDelete.equals(pathToTable.resolve("signature.tsv"))) {
                        DeleteFunctions.deleteFile(directoryToDelete);
                        continue;

                    }
                    DeleteFunctions.deleteDirectory(directoryToDelete);
                }
                try {
                    listOfDirectories.close();
                } catch (IOException ioexc2) {
                    throw new DatabaseException("can't close listOfDirectories " + pathToTable.toString());
                }
            } catch (IOException ioexc) {
                throw new DatabaseException("cant open list of directories " + pathToTable);
            }

            // ------------ delete all directories

            makeSignature();

            for (Map.Entry<String, Storeable> entry : realDBMap.entrySet()) {
                int hash = entry.getKey().hashCode();
                int nDirectory = ((hash % 16) + 16) % 16;
                int nFile = (((hash / 16) % 16) + 16) % 16;
            /*System.out.println(entry.getKey());
            System.out.println(hash);
            System.out.println(nDirectory);
            System.out.println(nFile);
            System.out.println(pathToTable.toString());*/
                mapDirectoriesFiles.get(nDirectory).get(nFile).
                        add(new Pair<String, Storeable>(entry.getKey(), entry.getValue()));

            }

            if (!Files.exists(pathToTable)) {
                try {
                    Files.createDirectory(pathToTable);
                } catch (IOException ioexc) {
                    throw new DatabaseException("couldnt create directory for table " + pathToTable.toString());
                }
            }

            for (int i = 0; i < 16; ++i) {
                String directoryName = Integer.toString(i) + ".dir";
                if (i > 0 && i < 10) {
                    directoryName = "0" + directoryName;
                }
                int curNumber = i;
                writeDirectory(curNumber, directoryName, mapDirectoriesFiles);
            }
        } finally {
            lock.writeLock().unlock();
        }


    }

    public void writeDirectory(int numberOfDirectory, String arg,
                               Vector<Vector<Vector<Pair<String, Storeable>>>> mapDirectoriesFiles) {

        int keysInDirectory = 0;
        Path currentDirectory = Paths.get(pathToTable.toString()).resolve(arg);

        for (int i = 0; i < 16; ++i) {
            keysInDirectory += mapDirectoriesFiles.get(numberOfDirectory).get(i).size();
        }

        if (!Files.exists(currentDirectory) && keysInDirectory == 0) {
            return;
        }

        if (!Files.exists(currentDirectory) && keysInDirectory > 0) {
            try {
                Files.createDirectory(currentDirectory);
            } catch (IOException ioexc) {
                throw new DatabaseException("couldnt create " + currentDirectory.toString());
            }
        }

        for (int i = 0; i < 16; ++i) {
            String fileName = Integer.toString(i) + ".dat";
            if (i > 0 && i < 10) {
                fileName = "0" + fileName;
            }
            int iFile = i;
            writeFile(numberOfDirectory, iFile, fileName, mapDirectoriesFiles, currentDirectory);
        }

      /*  if (keysInDirectory == 0){
            try {
                Files.delete(currentDirectory);
            } catch (IOException ioexc) {
                throw new DatabaseException("cant delete empty direction " + currentDirectory.toString());
            }
        }*/
    }
    public void writeFile(int numberOfDirectory, int numberOfFile, String arg,
                          Vector<Vector<Vector<Pair<String, Storeable>>>> mapDirectoriesFiles,
                          Path currentDirectory) {



        int keysInFile = 0;
        Path currentFile = Paths.get(currentDirectory.toString()).resolve(arg);

        keysInFile = mapDirectoriesFiles.get(numberOfDirectory).get(numberOfFile).size();

        if (!Files.exists(currentFile) && keysInFile == 0) {
            return;
        }

        if (!Files.exists(currentFile) && keysInFile > 0) {
            try {
                Files.createFile(currentFile);
            } catch (IOException ioexc) {
                throw new DatabaseException("couldnt create " + currentFile.toString());
            }
        }

      /*  if (keysInFile == 0){
            try {
                Files.delete(currentFile);
            } catch (IOException ioexc) {
                throw new DatabaseException("cant delete empty file " + currentFile.toString());
            }
            return;
        }*/

        try {
            DataOutputStream fileToWrite = new DataOutputStream(Files.newOutputStream(currentFile));

            for (Pair<String, Storeable> currentPair
                    : mapDirectoriesFiles.get(numberOfDirectory).get(numberOfFile)) {
                String key = currentPair.getKey();
                Storeable storeValue = currentPair.getValue();
                String value;
                try {
                    value = serializer.serialize(this, storeValue);
                } catch (ParseException pexc) {
                    throw new DatabaseException("couldn`t serialize value for key " + key);
                }
                byte[] byteKey = key.getBytes("UTF-8");
                byte[] byteValue = value.getBytes("UTF-8");
                fileToWrite.writeInt(byteKey.length);
                fileToWrite.write(byteKey);
                fileToWrite.writeInt(byteValue.length);
                fileToWrite.write(byteValue);

            }
            try  {
                fileToWrite.close();
            } catch (IOException ioexc) {
                throw new DatabaseException("can't close DataOutputStream for" + currentFile.toString());
            }
        } catch (IOException iexc) {
            throw new DatabaseException("something wrong with writing to file");
        }
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) {

        if (key == null) {
            throw new IllegalArgumentException("key cannot be a null for get");
        }
        return diff.get().get(key);

    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("arguments for put can't be nullpointers");
        }
       return diff.get().put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be a null for a remove");
        }
        return diff.get().remove(key);
    }

    @Override
    public int size() {
        int dbsize = realDBMap.size();
        int toPutSize = diff.get().toPut.size();
        int toRemoveSize = diff.get().toRemove.size();
        return dbsize + toPutSize - toRemoveSize;
        //return (realDBMap.size() + diff.get().toPut.size() - diff.get().toRemove.size());
    }

    @Override
    public List<String> list() {
        List<String> listOfKeys = new LinkedList<String>();
        for (String currentKey : realDBMap.keySet()) {
            listOfKeys.add(currentKey);
        }
        for (String keyToPut : diff.get().toPut.keySet()) {
            listOfKeys.add(keyToPut);
        }
        for (String keyToRemove : diff.get().toRemove.keySet()) {
            listOfKeys.remove(keyToRemove);
        }
        return listOfKeys;
    }

    @Override
    public int commit() {
        int committed = diff.get().changes();
        diff.get().commit();
        return committed;
    }

    @Override
    public int rollback() {
        int uncommitted = diff.get().changes();
        diff.get().rollback();
        return uncommitted;
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return diff.get().changes();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {

        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException(columnIndex + " columnIndex is out of bound 0-" + signature.toString());
        }
        return signature.get(columnIndex);
    }

    public int getVersion() {
        return version;
    }

    public Storeable realPut(String key, Storeable store) {
        return realDBMap.put(key, store);
    }

    public Storeable realGet(String key) {
        return realDBMap.get(key);
    }

    public Storeable realRemove(String key) {
        return realDBMap.get(key);
    }

}
