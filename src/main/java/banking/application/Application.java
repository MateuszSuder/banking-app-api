package banking.application;

import banking.application.service.AccountService;
import banking.application.service.AuthService;
import banking.application.service.MailerService;
import banking.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	@Autowired
	protected UserService userService;

	@Autowired
	protected MailerService mailerService;

	@Autowired
	protected AuthService authService;

	@Autowired
	protected AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
