package com.example.mybookshopapp.service;

import com.example.mybookshopapp.entity.BalanceTransaction;
import com.example.mybookshopapp.entity.Book;
import com.example.mybookshopapp.entity.BookUser;
import com.example.mybookshopapp.entity.TypeBookToUser;
import com.example.mybookshopapp.repository.BalanceTransactionRepository;
import com.example.mybookshopapp.repository.BookRepository;
import com.example.mybookshopapp.repository.BookUserRepository;
import com.example.mybookshopapp.errs.NoEnoughFundsForPayment;
import com.example.mybookshopapp.entity.security.BookstoreUser;
import com.example.mybookshopapp.entity.security.BookstoreUserDetails;
import com.example.mybookshopapp.repository.BookUserTypeRepository;
import com.example.mybookshopapp.repository.security.BookstoreUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;

    private final BookRepository bookRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final BalanceTransactionRepository balanceTransactionRepository;
    private final BookUserRepository bookUserRepository;
    private final BookUserTypeRepository bookUserTypeRepository;

    public PaymentService(BookRepository bookRepository, BookstoreUserRepository bookstoreUserRepository, BalanceTransactionRepository balanceTransactionRepository, BookUserRepository bookUserRepository, BookUserTypeRepository bookUserTypeRepository) {
        this.bookRepository = bookRepository;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.bookUserRepository = bookUserRepository;
        this.bookUserTypeRepository = bookUserTypeRepository;
    }

    public String getPaymentUrl(Integer sum, Integer invId) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin + ":" + sum + ":" + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + sum +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1";
    }

    @Transactional(rollbackFor = NoEnoughFundsForPayment.class)
    public Boolean buyBooksByUser(BookstoreUser user) throws NoEnoughFundsForPayment {
        List<Book> bookListBuy = bookRepository.findBooksByUserAndType(user, TypeBookToUser.CART);
        Integer sumBooks = bookListBuy.stream()
                .map(book -> balanceTransactionRepository.save(new BalanceTransaction(user,
                        book,
                        book.discountPrice(),
                        "Покупка книги: " + book.getTitle())))
                .map(BalanceTransaction::getValue).mapToInt(Integer::intValue).sum();
        if (user.getBalance() < sumBooks) {
            throw new NoEnoughFundsForPayment("No Enough Funds, make deposit to the account");
        }
        user.setBalance(user.getBalance() - sumBooks);
        bookstoreUserRepository.save(user);
        for (Book book : bookListBuy) {
            BookUser bookUser = bookUserRepository.findByBookAndUserAndType(book, user, bookUserTypeRepository.findByCode(TypeBookToUser.CART));
            bookUser.setType(bookUserTypeRepository.findByCode(TypeBookToUser.PAID));
            bookUserRepository.save(bookUser);
        }
        return true;
    }

    public String deposit(BookstoreUserDetails user, Integer sum) throws NoSuchAlgorithmException {
        BookstoreUser currentUser = user.getBookstoreUser();
        BalanceTransaction transaction = balanceTransactionRepository.save(new BalanceTransaction(currentUser, null, sum, "Depositing funds through Robokassa"));
        currentUser.setBalance(currentUser.getBalance() + sum);
        bookstoreUserRepository.save(currentUser);
        return getPaymentUrl(sum, transaction.getId());
    }
}
