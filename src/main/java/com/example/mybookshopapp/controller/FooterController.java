package com.example.mybookshopapp.controller;

import com.example.mybookshopapp.dto.MessageDto;
import com.example.mybookshopapp.entity.Message;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.entity.security.ContactType;
import com.example.mybookshopapp.repository.DocumentRepository;
import com.example.mybookshopapp.repository.FaqRepository;
import com.example.mybookshopapp.repository.MessageRepository;
import com.example.mybookshopapp.repository.UserContactRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FooterController {

    private final DocumentRepository documentRepository;
    private final FaqRepository faqRepository;
    private final MessageRepository messageRepository;
    private final UserContactRepository userContactRepository;

    public FooterController(DocumentRepository documentRepository, FaqRepository faqRepository, MessageRepository messageRepository, UserContactRepository userContactRepository) {
        this.documentRepository = documentRepository;
        this.faqRepository = faqRepository;
        this.messageRepository = messageRepository;
        this.userContactRepository = userContactRepository;
    }

    @GetMapping("/documents")
    public String documentsPage(Model model) {
        model.addAttribute("documents", documentRepository.findAll(Sort.by("sortIndex")));
        return "documents/index";
    }

    @GetMapping("/documents/{slug}")
    public String documentSlugPage(@PathVariable String slug, Model model) {
        model.addAttribute("document", documentRepository.findBySlug(slug));
        return "documents/slug";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @GetMapping("/faq")
    public String faqPage(Model model) {
        model.addAttribute("faqs", faqRepository.findAll(Sort.by("sortIndex")));
        return "faq";
    }

    @GetMapping("/contacts")
    public String contactsPage(Model model) {
        model.addAttribute("messageDto", new MessageDto());
        return "contacts";
    }

    @PostMapping("/contacts")
    public String handleSendMessage(@AuthenticationPrincipal BookstoreUserDetails userDetails,
                                    @ModelAttribute MessageDto messageDto) {
        Message message;
        if (userDetails != null) {
            String emailByUser = userContactRepository.findByUserAndType(userDetails.getBookstoreUser(), ContactType.EMAIL).getContact();
            message = new Message(userDetails.getBookstoreUser(), messageDto, emailByUser);
        } else {
            message = new Message(messageDto);
        }
        messageRepository.save(message);
        return "redirect:/contacts";
    }
}
