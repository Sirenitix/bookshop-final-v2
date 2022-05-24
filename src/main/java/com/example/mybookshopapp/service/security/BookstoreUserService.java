package com.example.mybookshopapp.service.security;

import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.entity.security.UserContact;
import com.example.mybookshopapp.repository.UserContactRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookstoreUserService {

    @Value("${appEmail.email}")
    private String email;

    private final BookstoreUserRepository bookstoreUserRepository;
    private final UserContactRepository userContactRepository;
    private final UserContactService userContactService;

    @Autowired
    public BookstoreUserService(BookstoreUserRepository bookstoreUserRepository, UserContactRepository userContactRepository, UserContactService userContactService) {
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.userContactRepository = userContactRepository;
        this.userContactService = userContactService;
    }

    public List<BookstoreUser> getPageOfUsers(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit, Sort.by("regTime"));
        return bookstoreUserRepository.findAll(nextPage).getContent();
    }

    public void blockAccessUserFor(String hash, Integer hours) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByHash(hash);
        List<UserContact> userContacts = userContactRepository.findByUser(user);
        for (UserContact contact : userContacts) {
            contact.setCodeTime(LocalDateTime.now().plusHours(hours));
            if (contact.getType().equals(ContactType.EMAIL)) {
                userContactService.sendEmailNotice(
                        email, contact.getContact(), "Your account blocked for " + hours + " hours");
            }
            userContactRepository.save(contact);
        }
    }
}
