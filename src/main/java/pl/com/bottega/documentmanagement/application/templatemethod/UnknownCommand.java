package pl.com.bottega.documentmanagement.application.templatemethod;

/**
 * Created by Dell on 2016-08-21.
 */
public class UnknownCommand implements Command {

    @Override
    public void execute() {
        System.out.println("Sorry, I don't understand :/");
    }
}
