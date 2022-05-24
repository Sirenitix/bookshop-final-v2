package com.example.mybookshopapp.controller.security;

import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.entity.security.*;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import com.example.mybookshopapp.service.BookService;
import com.example.mybookshopapp.service.BookUserService;
import com.example.mybookshopapp.service.security.BookstoreUserService;
import com.example.mybookshopapp.service.security.UserContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UsersController {

    private static final String USERS_SLUG = "users/slug";
    private static final String REDIRECT_USERS = "redirect:/users/";
    private static final String WARN_MESSAGE ="warnMessage";
    private static final String MESSAGE ="message";

    @Value("${appEmail.email}")
    private String email;

    private final BookstoreUserRepository bookstoreUserRepository;
    private final BookstoreUserService bookstoreUserService;
    private final UserContactService userContactService;
    private final BookService bookService;
    private final BookUserService bookUserService;

    @Autowired
    public UsersController(BookstoreUserRepository bookstoreUserRepository, BookstoreUserService bookstoreUserService, UserContactService userContactService, BookService bookService, BookUserService bookUserService) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.bookstoreUserService = bookstoreUserService;
        this.userContactService = userContactService;
        this.bookService = bookService;
        this.bookUserService = bookUserService;
    }

    @GetMapping("")
    public String usersPage(Model model) {
        model.addAttribute("users", bookstoreUserService.getPageOfUsers(0, 20));
        return "users/users";
    }

    @GetMapping("/{hash}")
    public String userSlugPage(@AuthenticationPrincipal BookstoreUserDetails bookstoreUserDetails,
                               @PathVariable String hash,
                               Model model) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByHash(hash);
        if (user == null) {
            model.addAttribute(WARN_MESSAGE, "User removed");
        }
        else if (user.getRoles().contains(Role.ANONYMOUS)) {
            model.addAttribute(WARN_MESSAGE, "User is not registered");
        } else {
            List<UserContact> userContacts = userContactService.getContactsByUser(user);
            if (userContacts.get(0).getCodeTime().minusMinutes(5).isAfter(LocalDateTime.now())) {
                Duration duration = Duration.between(LocalDateTime.now(), userContacts.get(0).getCodeTime());
                String messageBlock = "User is blocked, left " +
                        (duration.toHours() < 1 ? duration.toMinutes() + " minutes" : duration.toHours() + " hours");

                model.addAttribute("blocked", messageBlock);
            }
            if (Objects.equals(user.getId(), bookstoreUserDetails.getBookstoreUser().getId())) {
                model.addAttribute("current", true);
            }
            List<Book> paidBooksForUser = bookService.getPaidBooksForUser(user);
            List<Book> recommendBooksForUser = bookService.getPageOfRecommendedBooksForUser(user, 0, 10);
            model.addAttribute("paidBooks", bookService.getBookWithAuthorDtoList(paidBooksForUser));
            model.addAttribute("recommendBooks", bookService.getBookWithAuthorDtoList(recommendBooksForUser));
            model.addAttribute("user", user);
            model.addAttribute("contacts", userContacts);
        }
        return USERS_SLUG;
    }

    @GetMapping("/remove/{hash}")
    public String removeUser(@PathVariable String hash, Model model) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByHash(hash);
        List<UserContact> contacts = userContactService.getContactsByUser(user);
        for (UserContact contact : contacts) {
            String notice = "Your account was removed";
            if (contact.getType().equals(ContactType.EMAIL)) {
                userContactService.sendEmailNotice(email, contact.getContact(), notice);
            }
        }
        bookstoreUserRepository.delete(user);
        model.addAttribute(WARN_MESSAGE, "User removed");
        return USERS_SLUG;
    }

    @GetMapping("/{hash}/remove/role")
    public String removeRoleAdmin(@PathVariable String hash, RedirectAttributes redirectAttributes) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByHash(hash);
        user.getRoles().remove(Role.ADMIN);
        user = bookstoreUserRepository.save(user);
        redirectAttributes.addFlashAttribute(MESSAGE, "Role ADMIN removed from user " + user.getName());
        return REDIRECT_USERS + hash;
    }

    @GetMapping("/{hash}/add/role")
    public String addRoleAdmin(@PathVariable String hash, RedirectAttributes redirectAttributes) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByHash(hash);
        user.getRoles().add(Role.ADMIN);
        bookstoreUserRepository.save(user);
        redirectAttributes.addFlashAttribute(MESSAGE, "Role ADMIN added to user " + user.getName());
        return REDIRECT_USERS + hash;
    }

    @GetMapping("/list")
    @ResponseBody
    public List<BookstoreUser> getUsersList(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return bookstoreUserService.getPageOfUsers(offset, limit);
    }

    @GetMapping("/{hash}/block/{hours}")
    public String blockAccessUser(@PathVariable String hash, @PathVariable Integer hours) {
        bookstoreUserService.blockAccessUserFor(hash, hours);
        return REDIRECT_USERS + hash;
    }


    @GetMapping("/{userHash}/give/{bookSlug}")
    public String giveBookToUser(@PathVariable String userHash,
                                 @PathVariable String bookSlug,
                                 RedirectAttributes redirectAttributes) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByHash(userHash);
        Book book = bookService.getBySlug(bookSlug);
        UserContact userContact = userContactService
                .getContactsByUser(user)
                .stream()
                .filter(contact -> contact.getType().equals(ContactType.EMAIL)).findAny().orElse(null);
        bookUserService.changeBookStatusForUser(bookSlug, user, TypeBookToUser.PAID);
        if (userContact != null) {
            userContactService.sendEmailNotice(
                    email,
                    userContact.getContact(),
                    "You were given book: " + book.getTitle());
        }
        redirectAttributes.addFlashAttribute(MESSAGE, "Book " + book.getTitle() + " was added to user " + user.getName());
        return REDIRECT_USERS + userHash;
    }

}
