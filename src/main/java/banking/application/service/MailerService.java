package banking.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

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

        /**
         * Overridden method run when Thread is started.
         * Sends mail with parameters given in class fields
         */
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

