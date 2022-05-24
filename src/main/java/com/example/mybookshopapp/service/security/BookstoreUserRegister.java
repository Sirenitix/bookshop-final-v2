package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.dto.ChangeUserForm;
import com.example.mybookshopapp.dto.ContactConfirmationPayload;
import com.example.mybookshopapp.dto.ContactConfirmationResponse;
import com.example.mybookshopapp.dto.RegistrationForm;
import com.example.mybookshopapp.entity.security.*;
import com.example.mybookshopapp.errs.WrongCredentialsException;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import com.example.mybookshopapp.util.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookstoreUserRegister {

    private final BookstoreUserRepository bookstoreUserRepository;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    private final UserContactService userContactService;
    private final JavaMailSender javaMailSender;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${appEmail.email}")
    private String email;

    @Autowired
    public BookstoreUserRegister(BookstoreUserRepository bookstoreUserRepository, AuthenticationManager authenticationManager, BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil, UserContactService userContactService, JavaMailSender javaMailSender, UserContactRepository userContactRepository, PasswordEncoder passwordEncoder) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.authenticationManager = authenticationManager;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.userContactService = userContactService;
        this.javaMailSender = javaMailSender;
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Boolean registerNewUser(RegistrationForm registrationForm) {

        BookstoreUser userByEmail = bookstoreUserRepository.findBookstoreUserByContact(registrationForm.getEmail());
        BookstoreUser userByPhone = bookstoreUserRepository.findBookstoreUserByContact(registrationForm.getPhone());

        UserContact userContactByEmail = userContactService.getByEmail(registrationForm.getEmail());
        UserContact userContactByPhone = userContactService.getByPhone(registrationForm.getPhone());

        if (userByEmail == null && userByPhone == null && userContactByEmail != null && userContactByPhone != null) {
            BookstoreUser user = new BookstoreUser();
            user.setName(registrationForm.getName());
            user.setHash(UUID.randomUUID().toString());
            user.setRoles(Collections.singleton(Role.USER));
            user = bookstoreUserRepository.save(user);

            UserContact userContactEmailForUser = new UserContact(user, userContactByEmail.getContact(), 1, ContactType.EMAIL);
            UserContact userContactPhoneForUser = new UserContact(user, userContactByPhone.getContact(), 1, ContactType.PHONE);

            userContactRepository.save(userContactEmailForUser);
            userContactRepository.save(userContactPhoneForUser);
            userContactRepository.delete(userContactByEmail);
            userContactRepository.delete(userContactByPhone);

            return true;
        } else {
            return false;
        }
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                        payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public Object getCurrentUser() {
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getBookstoreUser();
    }

    public void updateUser(ChangeUserForm changeUserForm) throws WrongCredentialsException {
        BookstoreUser currentUser = (BookstoreUser) getCurrentUser();
        UserContact userContactEmailForCurrentUser = userContactRepository.findByUserAndType(currentUser, ContactType.EMAIL);
        String emailOldUser = userContactEmailForCurrentUser.getContact();

        BookstoreUser updateUser = new BookstoreUser();
        UserContact updateUserContactEmail = new UserContact();
        UserContact updateUserContactPhone = new UserContact();
        updateUserContactEmail.setType(ContactType.EMAIL);
        updateUserContactPhone.setType(ContactType.PHONE);

        if (verifyUserName(changeUserForm.getName())) {
            updateUser.setName(changeUserForm.getName());
        } else {
            throw new WrongCredentialsException("Incorrect name");
        }
        if (verifyEmail(changeUserForm.getMail())) {
            updateUserContactEmail.setContact(changeUserForm.getMail());
        } else {
            throw new WrongCredentialsException("Incorrect email");
        }
        if (verifyPhone(changeUserForm.getPhone())) {
            updateUserContactPhone.setContact(changeUserForm.getPhone());
        } else {
            throw new WrongCredentialsException("Incorrect phone");
        }

        String code = userContactService.generateCode();

        updateUser = bookstoreUserRepository.save(updateUser);
        updateUserContactEmail.setUser(updateUser);
        updateUserContactPhone.setUser(updateUser);
        updateUserContactEmail.setCode(code);
        updateUserContactPhone.setCode(code);
        userContactRepository.save(updateUserContactEmail);
        userContactRepository.save(updateUserContactPhone);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(emailOldUser);
        message.setSubject("Changing credentials!");
        message.setText("Please, visit next link: http://localhost:8080/changeCredentials/"
                + updateUser.getId() + "/"
                + currentUser.getId() + "/"
                + code.replace(" ", "_"));
        javaMailSender.send(message);
    }

    public void approveCredentials(Integer updateUserId, Integer currentUserId, String code) throws WrongCredentialsException {
        BookstoreUser currentUser = bookstoreUserRepository.getOne(currentUserId);
        UserContact currentUserContactEmail = userContactRepository.findByUserAndType(currentUser, ContactType.EMAIL);
        UserContact currentUserContactPhone = userContactRepository.findByUserAndType(currentUser, ContactType.PHONE);

        Optional<BookstoreUser> updateUser = bookstoreUserRepository.findById(updateUserId);
        if (!updateUser.isPresent()) {
            throw new WrongCredentialsException("Changes already confirmed");
        }
        UserContact updateUserContactEmail = userContactRepository.findByUserAndType(updateUser.get(), ContactType.EMAIL);
        UserContact updateUserContactPhone = userContactRepository.findByUserAndType(updateUser.get(), ContactType.PHONE);

        if (Boolean.FALSE.equals(userContactService.verifyCode(updateUser.get(), updateUserContactEmail.getContact(), code.replace("_", " ")))
                || Boolean.FALSE.equals(userContactService.verifyCode(updateUser.get(), updateUserContactPhone.getContact(), code.replace("_", " ")))
                || !currentUser.getId().equals(currentUserId)) {
            throw new WrongCredentialsException("Confirmation code expired");
        }
        currentUser.setName(updateUser.get().getName());
        currentUserContactEmail.setContact(updateUserContactEmail.getContact());
        currentUserContactPhone.setContact(updateUserContactPhone.getContact());

        String codeForAuth = updateUserContactEmail.getCode();

        currentUserContactEmail.setCode(passwordEncoder.encode(codeForAuth));
        currentUserContactPhone.setCode(passwordEncoder.encode(codeForAuth));

        userContactRepository.delete(updateUserContactEmail);
        userContactRepository.delete(updateUserContactPhone);
        bookstoreUserRepository.delete(updateUser.get());
        bookstoreUserRepository.save(currentUser);
        userContactRepository.save(currentUserContactPhone);
        UserContact contact = userContactRepository.save(currentUserContactEmail);

        String token = generateTokenForUpdateUser(contact, codeForAuth);

        authenticateUpdatedUser(contact, token);
    }

    private boolean verifyPhone(String phone) {
        return (phone != null && !phone.isEmpty());
    }

    private boolean verifyEmail(String email) {
        return (email != null && email.contains("@"));
    }

    private boolean verifyUserName(String name) {
        return (name != null && name.length() > 2);
    }

    public String generateTokenForUpdateUser(UserContact contact, String code) {
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact(contact.getContact());
        payload.setCode(code);
        ContactConfirmationResponse response = jwtLogin(payload);
        return response.getResult();
    }

    public void authenticateUpdatedUser(UserContact contact, String token) {
        UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(contact.getContact());
        if (Boolean.TRUE.equals(jwtUtil.validateToken(token, userDetails))) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
