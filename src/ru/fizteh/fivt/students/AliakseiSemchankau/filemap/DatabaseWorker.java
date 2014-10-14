package ru.fizteh.fivt.students.AliakseiSemchankau.filemap;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Vector;
import java.util.*;
/**
 * Created by Aliaksei Semchankau on 09.10.2014.
 */
public class DatabaseWorker {
    HashMap<String, String> dbMap;
    Path pathFile;
    HashMap<String, InterfaceCommand> commandHashMap;
    Queue<Vector<String>> listOfCommands;

    public DatabaseWorker(Path databasePath) {
        pathFile = databasePath;
        listOfCommands = new LinkedList<Vector<String>>();
        commandHashMap = new HashMap<String, InterfaceCommand>();
        commandHashMap.put("put", new PutCommand());
        commandHashMap.put("get", new GetCommand());
        commandHashMap.put("list", new ListCommand());
        commandHashMap.put("remove", new RemoveCommand());
        commandHashMap.put("exit", new ExitCommand());
    }

    public void readCommands(String[] args) {

        String toParse = "";
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                toParse += (args[i] + " ");
            }
        } else {
            Scanner line = new Scanner(System.in);
            toParse = line.nextLine();
        }

        if (toParse.length() == 0) {
            return;
        }

        String curArgument = "";

        for (int curSymbol = 0; ; ++curSymbol) {
            if (curSymbol != toParse.length() && toParse.charAt(curSymbol) != ';') {
                curArgument += toParse.charAt(curSymbol);
            } else {
                listOfCommands.add(processing(curArgument));
                curArgument = "";
                if (curSymbol == toParse.length()) {
                    break;
                }
            }

        }

       /*while (!listOfCommands.isEmpty())
        {
            Vector<String> curLine = listOfCommands.poll();
            for (int i = 0; i < curLine.size(); ++i)
                System.out.print(curLine.get(i) + " ");
            System.out.println(";");
        }*/
    }

    Vector<String> processing(String argLine) {
        Vector<String> argumentList = new Vector<String>();
        String argument = new String();
        argument = "";
        for (int symbol = 0; ; ++symbol) {
            if (symbol != argLine.length() && argLine.charAt(symbol) != ' ') {
                argument += argLine.charAt(symbol);
            } else {
                if (argument.length() > 0) {
                    argumentList.add(argument);
                    argument = "";
                }

                if (symbol == argLine.length()) {
                    break;
                }
            }
        }
        return argumentList;
    }

    public void doCommands(FileMap databaseInformation) {
        while (listOfCommands.size() > 0) {
            Vector<String> argList = listOfCommands.poll();
            InterfaceCommand commandToDo = commandHashMap.get(argList.elementAt(0));
            if (commandToDo == null) {
                throw new DatabaseException("this command does not exist!");
            }
            commandToDo.makeCommand(argList, databaseInformation);
        }
    }

}
