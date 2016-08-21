package pl.com.bottega.documentmanagement.application;

import java.util.Collection;
import java.util.Scanner;

/**
 * Created by Dell on 2016-08-21.
 */
public abstract class ConsoleApplication {

    protected Scanner scanner = new Scanner(System.in);

    public void run() {
        while(true) {
            printMenu();
            String cmd = getCommand();
            if (cmd.equals("quit"))
                return;
            execute(cmd);
        }
    }

    protected abstract void execute(String cmd);

    private void printMenu() {
        Collection<String> menuItems = menuItems();
        for (String item : menuItems)
            System.out.println(item);
        System.out.println("Type quit to exit");
    }

    protected abstract Collection<String> menuItems();

    public String getCommand() {
        return scanner.next();
    }
}
