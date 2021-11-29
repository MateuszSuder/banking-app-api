package banking.application.global.utils.Mailer;

import banking.application.routes.Account.BankAccount.Code;
import banking.application.routes.Account.BankAccount.IBAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for simple mailer service
 * Runs on thread as it takes a longer while
 */
@Component
public class MailerService {
    @Autowired
    private JavaMailSender emailSender;

    /**
     * Mailer service constructor.
     * Sends message on creation by Runnable class
     * @param to recipient
     * @param subject title of mail
     * @param text body (text) of mail
     */
    public void sendMessage(String to, String subject, String text) {
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(new MailSender(to, subject, text));
    }

    /**
     * Overloaded method passing arguments to main method
     * @param to email of recipient
     * @param codes list of codes
     * @param iban iban of account
     */
    public void sendCodes(String to, List<Code> codes, IBAN iban) {
        this.sendCodes(to, codes, iban.toString());
    }

    /**
     * Method to send codes to user
     * @param to email of recipient
     * @param codes list of codes
     * @param iban iban of account
     */
    public void sendCodes(String to, List<Code> codes, String iban) {
        // Prepare message to send
        StringBuilder message = new StringBuilder("Kody potwierdzające transakcje dla konta: " + iban);
        for (Code c : codes) {
            message.append("\n").append(c.getId()).append(": ").append(c.getCode());
        }
        this.sendMessage(to, "Kody potwierdzające", message.toString());
    }

    /**
     * Simple class executed by thread.
     * Gets arguments from parent class
     */
    class MailSender implements Runnable {
        String to;
        String subject;
        String text;

        MailSender(String to, String subject, String text) {
            this.to = to;
            this.subject = subject;
            this.text = text;
        }

        @Override
        public void run() {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bankingappMDM@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        }
    }
}

