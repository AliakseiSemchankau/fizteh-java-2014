package ru.fizteh.fivt.students.AliakseiSemchankau.parallel;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Aliaksei Semchankau on 01.12.2014.
 */
public class DatabaseTableDiff {

    DatabaseTable dTable;
    ReentrantReadWriteLock lock;
    int localVersion = 0;

    public DatabaseTableDiff(DatabaseTable tableToSet, ReentrantReadWriteLock lockToSet) {
        dTable = tableToSet;
        lock = lockToSet;
    }

    public void fixVersion() {
        lock.readLock().lock();
        lock.writeLock().lock();

        try {
            if (dTable.getVersion() > localVersion) {
                localVersion = dTable.getVersion();
            } else {
                return;
            }



        } finally {
            lock.readLock().unlock();
            lock.writeLock().unlock();
        }
    }


}
