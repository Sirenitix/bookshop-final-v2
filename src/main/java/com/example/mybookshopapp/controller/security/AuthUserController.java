package com.example.mybookshopapp.controller.security;

import com.example.mybookshopapp.dto.ContactConfirmationPayload;
import com.example.mybookshopapp.dto.ContactConfirmationResponse;
import com.example.mybookshopapp.dto.RegistrationForm;
import com.example.mybookshopapp.dto.SearchWordDto;
import com.example.mybookshopapp.errs.security.NotFoundUserWithContactException;
import com.example.mybookshopapp.errs.security.WrongCodeLoginException;
import com.example.mybookshopapp.errs.security.WrongCodeRegException;
import com.example.mybookshopapp.service.security.BookstoreUserRegister;
import com.example.mybookshopapp.service.security.UserContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthUserController {

    private final BookstoreUserRegister userRegister;
    private final UserContactService userContactService;


    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister, UserContactService userContactService) {
        this.userRegister = userRegister;
        this.userContactService = userContactService;
    }

    @Value("${appEmail.email}")
    private String email;

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/signin")
    public String handleSignIn() {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) throws NotFoundUserWithContactException, WrongCodeLoginException {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        String code = userContactService.generateCode();
        if (payload.getContact().contains("@")) {
            userContactService.saveNewCode(code, payload.getContact());
            userContactService.sendVerificationEmail(email, payload.getContact(), code);
        } else {
            userContactService.saveNewCode(code, payload.getContact()); //expires in 1 min.
            userContactService.sendSecretCodeSms(payload.getContact(), code);
        }
        response.setResult("true");
        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) throws WrongCodeRegException {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        String code = userContactService.generateCode();
        userContactService.saveNewCodeForReg(code, payload.getContact());
        userContactService.sendVerificationEmail(email, payload.getContact(), code);
        response.setResult("true");
        return response;
    }

    @PostMapping("/requestPhoneContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestPhoneConfirmation(@RequestBody ContactConfirmationPayload payload) throws WrongCodeRegException {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        String code = userContactService.generateCode();
        userContactService.sendSecretCodeSms(payload.getContact(), code);
        userContactService.saveNewCodeForReg(code, payload.getContact());
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) throws WrongCodeRegException {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        if (Boolean.TRUE.equals(userContactService.verifyCodeReg(payload.getContact(), payload.getCode()))) {
            response.setResult("true");
        }
        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) {
        if (Boolean.TRUE.equals(userRegister.registerNewUser(registrationForm))) {
            model.addAttribute("regOK", true);
        }
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) throws WrongCodeLoginException {
        userContactService.verifyCodeLogin(payload.getCode(), payload.getContact());
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }
}
