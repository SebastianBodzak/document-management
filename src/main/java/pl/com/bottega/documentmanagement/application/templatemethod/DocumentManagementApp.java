package pl.com.bottega.documentmanagement.application.templatemethod;

import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.com.bottega.documentmanagement.api.SignupResultDto;
import pl.com.bottega.documentmanagement.api.UserManager;

import java.util.Collection;
import java.util.Scanner;

/**
 * Created by Dell on 2016-08-21.
 */
public class DocumentManagementApp extends ConsoleApplication {

    private ApplicationContext applicationContext;

    public DocumentManagementApp() {
        applicationContext = new ClassPathXmlApplicationContext(new String[] {"application.xml"});
        promptLogin();
    }

    @Override
    protected CommandFactory commandFactory() {
        return new DocumentManagementCommandFactory(applicationContext);
    }

    private void promptLogin() {
        System.out.println("Login: ");
        String login = new Scanner(System.in).nextLine();
        String password = new Scanner(System.in).nextLine();
        UserManager userManager = applicationContext.getBean(UserManager.class);
        SignupResultDto signupResultDto = userManager.login(login, password);
        if(!signupResultDto.isSuccess()) {
            System.out.println(signupResultDto.getFailureReason());
            promptLogin();
        }
    }

    @Override
    protected Collection<String> menuItems() {
        return Lists.newArrayList(
                "1. Create document",
                "2. Search documents",
                "3. Edit document",
                "4. Verify document"
        );
    }

    public static void main(String[] args) {
        new DocumentManagementApp().run();
    }
}
