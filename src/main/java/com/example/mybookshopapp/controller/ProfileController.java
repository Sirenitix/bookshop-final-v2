package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.BalanceTransactionDto;
import com.example.mybookshopapp.dto.ChangeUserForm;
import com.example.mybookshopapp.dto.PaymentDto;
import com.example.mybookshopapp.dto.UserWithContactsDto;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.errs.IncorrectAmountToEnterException;
import com.example.mybookshopapp.errs.NoEnoughFundsForPayment;
import com.example.mybookshopapp.errs.WrongCredentialsException;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.example.mybookshopapp.service.BalanceTransactionService;
import com.example.mybookshopapp.service.PaymentService;
import com.example.mybookshopapp.service.RatingService;
import com.example.mybookshopapp.service.security.BookstoreUserRegister;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

@Controller
public class ProfileController {

    private final PaymentService paymentService;
    private final BalanceTransactionService balanceTransactionService;
    private final BookstoreUserRegister userRegister;
    private final RatingService ratingService;
    private final UserContactRepository userContactRepository;

    public ProfileController(PaymentService paymentService, BalanceTransactionService balanceTransactionService, BookstoreUserRegister userRegister, RatingService ratingService, UserContactRepository userContactRepository) {
        this.paymentService = paymentService;
        this.balanceTransactionService = balanceTransactionService;
        this.userRegister = userRegister;
        this.ratingService = ratingService;
        this.userContactRepository = userContactRepository;
    }

    @GetMapping("/profile")
    public String handleProfile(@RequestParam(name = "part", required = false) String part, Model model) {
        BookstoreUser user = (BookstoreUser) userRegister.getCurrentUser();
        model.addAttribute("part", part);
        model.addAttribute("transactions", balanceTransactionService.getTransactionsByUserPage(user, 0, 50).getContent());
        model.addAttribute("ratingUser", ratingService.getRatingByUser(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String handleChangeProfile(ChangeUserForm changeUserForm) throws WrongCredentialsException {
        userRegister.updateUser(changeUserForm);
        return "redirect:/profile";
    }

    @GetMapping("/transactions")
    @ResponseBody
    public BalanceTransactionDto getNextPageTransactions(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        BookstoreUser user = (BookstoreUser) userRegister.getCurrentUser();
        return new BalanceTransactionDto(balanceTransactionService.getTransactionsByUserPage(user, offset, limit).getContent());
    }

    @GetMapping("/pay")
    public String handlePay(@AuthenticationPrincipal BookstoreUserDetails user) throws NoEnoughFundsForPayment {
        paymentService.buyBooksByUser(user.getBookstoreUser());
        return "redirect:/my";
    }

    @PostMapping("/payment")
    public RedirectView handlePayment(@AuthenticationPrincipal BookstoreUserDetails user,
                                      @RequestBody PaymentDto payload) throws NoSuchAlgorithmException, IncorrectAmountToEnterException {
        if (payload.getSum() == null || payload.getSum() <= 0) {
            throw new IncorrectAmountToEnterException("Amount to enter must be more 0");
        }
        String paymentUrl = paymentService.deposit(user, payload.getSum());
        return new RedirectView(paymentUrl);
    }

    @GetMapping("/changeCredentials/{updateUserId}/{currentUserId}/{code}")
    public String approveCredentials(@PathVariable Integer updateUserId,
                                     @PathVariable Integer currentUserId,
                                     @PathVariable String code,
                                     Model model) throws WrongCredentialsException {

        userRegister.approveCredentials(updateUserId, currentUserId, code);
        BookstoreUser user = (BookstoreUser) userRegister.getCurrentUser();
        UserContact userContactEmail = userContactRepository.findByUserAndType(user, ContactType.EMAIL);
        UserContact userContactPhone = userContactRepository.findByUserAndType(user, ContactType.PHONE);
        model.addAttribute("curUsr", new UserWithContactsDto(user.getHash(), user.getBalance(), user.getName(),
                userContactEmail.getContact(), userContactPhone.getContact()));
        model.addAttribute("part", null);
        model.addAttribute("credentialsSuccess", "Профиль успешно сохранен");
        model.addAttribute("transactions", balanceTransactionService.getTransactionsByUserPage(user, 0, 50).getContent());
        model.addAttribute("ratingUser", ratingService.getRatingByUser(user));
        return "/profile";
    }

}
