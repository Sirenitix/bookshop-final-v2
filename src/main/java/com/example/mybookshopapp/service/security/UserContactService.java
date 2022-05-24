package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.errs.security.NotFoundUserWithContactException;
import com.example.mybookshopapp.errs.security.WrongCodeLoginException;
import com.example.mybookshopapp.errs.security.WrongCodeRegException;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserContactService {

    @Value("${twilio.ACCOUNT_SID}")
    private String accountSid;

    @Value("${twilio.AUTH_TOKEN}")
    private String authToken;

    @Value("${twilio.TWILIO_NUMBER}")
    private String twilioNumber;

    private final Random random = new Random();

    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Autowired
    public UserContactService(UserContactRepository userContactRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender) {
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    public void sendSecretCodeSms(String contact, String code) {
        Twilio.init(accountSid, authToken);
        String formattedContact = contact.replaceAll("[( )-]", "");
        Message.creator(
                new PhoneNumber(formattedContact),
                new PhoneNumber(twilioNumber),
                "Your secret code is: " + code
        ).create();
    }

    public String generateCode() {
        //nnn nnn - pattern
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(random.nextInt(9));
        }
        sb.insert(3, " ");
        return sb.toString();
    }

    public void saveNewCode(String code, String contact) throws NotFoundUserWithContactException, WrongCodeLoginException {
        UserContact userContact = userContactRepository.findByContact(contact);
        if (userContact == null || userContact.getApproved() == 0) {
            throw new NotFoundUserWithContactException("User with specified contact is not exists");
        }
//        if (userContact.getCodeTime().isAfter(LocalDateTime.now())) {
//            throw new WrongCodeLoginException("You were blocked. Try to log in later");
//        }
        userContact.setCode(passwordEncoder.encode(code));
        userContact.setCodeTime(LocalDateTime.now());
        userContact.setCodeTrials(0);
        userContactRepository.save(userContact);
    }


    public void saveNewCodeForReg(String code, String contact) throws WrongCodeRegException {
        UserContact userContact = userContactRepository.findByContact(contact);
        if (userContact != null) {
            if (userContact.getApproved() == 1) {
                throw new WrongCodeRegException("Contact already approved");
            }
            userContact.setCode(code);
            if (userContact.getCodeTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
                userContact.setCodeTrials(0);
            }
        } else {
            userContact = new UserContact(code, contact);
            userContact.setCodeTrials(0);
        }
        if (contact.contains("@")) {
            userContact.setType(ContactType.EMAIL);
        } else {
            userContact.setType(ContactType.PHONE);
        }
        userContactRepository.save(userContact);
    }

    public Boolean verifyCode(BookstoreUser user, String contact, String code) {
        UserContact userContact = userContactRepository.findByUserAndContact(user, contact);
        if (userContact == null || !userContact.getCode().equals(code)) {
            return false;
        }
        return LocalDateTime.now().isBefore(userContact.getCodeTime().plusSeconds(300));
    }

    public UserContact getByEmail(String email) {
        UserContact contactByEmail = userContactRepository.findByContact(email);
        if (contactByEmail == null || !contactByEmail.getType().equals(ContactType.EMAIL)) {
            return null;
        }
        return contactByEmail;
    }

    public UserContact getByPhone(String phone) {
        UserContact contactByEmail = userContactRepository.findByContact(phone);
        if (contactByEmail == null || !contactByEmail.getType().equals(ContactType.PHONE)) {
            return null;
        }
        return contactByEmail;
    }

    public void sendVerificationEmail(String emailFrom, String emailTo, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject("Bookstore email verification!");
        message.setText("Verification code is: " + code);
        javaMailSender.send(message);
    }

    public Boolean verifyCodeReg(String contact, String code) throws WrongCodeRegException {
        int countAttempts = 3;
        UserContact userContact = userContactRepository.findByContact(contact);

        if (userContact.getCodeTime().isAfter(LocalDateTime.now())) {
            String message = String.format("Число попыток подтверждения превышено, повторите попытку через %d минут",
                    Duration.between(LocalDateTime.now(), userContact.getCodeTime()).toMinutes());

            throw new WrongCodeRegException(message);
        }
        if (!userContact.getCode().equals(code)) {
            userContact.setCodeTrials(userContact.getCodeTrials() + 1);
            if (userContact.getCodeTrials() == countAttempts) {
                userContact.setCodeTrials(0);
                userContact.setCodeTime(LocalDateTime.now().plusMinutes(5));
                userContactRepository.save(userContact);

                throw new WrongCodeRegException("Число попыток подтверждения превышено, повторите попытку через 5 минут");
            }
            int attemptsCountCurrent = countAttempts - userContact.getCodeTrials();
            userContactRepository.save(userContact);
            throw new WrongCodeRegException("Код подтверждения введён неверно. У вас осталось " + attemptsCountCurrent + " попыток");
        }
        return true;
    }

    public void verifyCodeLogin(String code, String contact) throws WrongCodeLoginException {
        String phone = "phone";
        String email = "e-mail";
        int countAttempts = 3;
        UserContact userContact = userContactRepository.findByContact(contact);
        String currentType = userContact.getType().equals(ContactType.PHONE) ? phone : email;
        String otherType = currentType.equals(phone) ? email : phone;

        if (userContact.getCodeTime().isAfter(LocalDateTime.now())) {
            String message = String.format("Количество попыток входа по %s исчерпано, попробуйте войти по %s " +
                            "или повторить вход по %s через %d минут", currentType, otherType, currentType,
                    Duration.between(LocalDateTime.now(), userContact.getCodeTime()).toMinutes());

            throw new WrongCodeLoginException(message);
        }

        if (!passwordEncoder.matches(code, userContact.getCode())) {
            userContact.setCodeTrials(userContact.getCodeTrials() + 1);
            if (userContact.getCodeTrials() == countAttempts) {
                userContact.setCodeTrials(0);
                userContact.setCodeTime(LocalDateTime.now().plusMinutes(5));
                userContactRepository.save(userContact);
                String message = String.format("Количество попыток входа по %s исчерпано, попробуйте войти по %s " +
                        "или повторить вход по %s через 5 минут", currentType, otherType, currentType);

                throw new WrongCodeLoginException(message);
            }
            int attemptsCountCurrent = countAttempts - userContact.getCodeTrials();
            userContactRepository.save(userContact);
            throw new WrongCodeLoginException("Код подтверждения введён неверно. У вас осталось " + attemptsCountCurrent + " попыток");
        }
        userContact.setCodeTrials(0);
        userContactRepository.save(userContact);
    }

    public List<UserContact> getContactsByUser(BookstoreUser user) {
        return userContactRepository.findByUser(user);
    }

    public void sendEmailNotice(String emailFrom, String emailTo, String notice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject("Bookstore notice!");
        message.setText(notice);
        javaMailSender.send(message);
    }
}
