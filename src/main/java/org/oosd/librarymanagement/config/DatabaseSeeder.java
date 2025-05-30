package org.oosd.librarymanagement.config;

//********************************************************************************************************************
// This is the DatabaseSeeder class that seeds the database with some initial data.
// It implements CommandLineRunner and is annotated with @Component so Spring Boot runs it on startup.
// It seeds members, books, authors, borrow records, and users (admin & librarian) if the database is empty.
//********************************************************************************************************************

import org.oosd.librarymanagement.models.*;
import org.oosd.librarymanagement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final LibraryMemberRepository libraryMemberRepository;
    private final MembershipCardRepository membershipCardRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DatabaseSeeder(LibraryMemberRepository libraryMemberRepository,
                          MembershipCardRepository membershipCardRepository,
                          BookRepository bookRepository,
                          AuthorRepository authorRepository,
                          BorrowRecordRepository borrowRecordRepository) {
        this.libraryMemberRepository = libraryMemberRepository;
        this.membershipCardRepository = membershipCardRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (libraryMemberRepository.count() > 0 || bookRepository.count() > 0 || authorRepository.count() > 0) {
            System.out.println("Database already seeded. Skipping...");
            return;
        }

        // Create Library Members
        LibraryMember member1 = new LibraryMember();
        member1.setName("John Doe");
        member1.setEmail("john.doe@example.com");
        member1.setMembershipDate(new Date());

        LibraryMember member2 = new LibraryMember();
        member2.setName("Alice Smith");
        member2.setEmail("alice.smith@example.com");
        member2.setMembershipDate(new Date());

        libraryMemberRepository.saveAll(Arrays.asList(member1, member2));

        // Create Membership Cards
        MembershipCard card1 = new MembershipCard();
        card1.setCardNumber("M12345");
        card1.setIssueDate(new Date());
        card1.setExpiryDate(new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000))); // 1 year later

        MembershipCard card2 = new MembershipCard();
        card2.setCardNumber("M67890");
        card2.setIssueDate(new Date());
        card2.setExpiryDate(new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)));

        card1.setLibraryMember(member1);
        card2.setLibraryMember(member2);

        member1.setMembershipCard(card1);
        member2.setMembershipCard(card2);

        membershipCardRepository.saveAll(Arrays.asList(card1, card2));

        // Create Authors
        Author author1 = new Author();
        author1.setName("J.K. Rowling");
        author1.setBiography("Famous for writing Harry Potter.");

        Author author2 = new Author();
        author2.setName("George R.R. Martin");
        author2.setBiography("Famous for writing Game of Thrones.");

        authorRepository.saveAll(Arrays.asList(author1, author2));

        // Create Books
        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Sorcerer's Stone");
        book1.setIsbn("978-0747532699");
        book1.setPublicationYear(1997);

        Book book2 = new Book();
        book2.setTitle("A Game of Thrones");
        book2.setIsbn("978-0553103540");
        book2.setPublicationYear(1996);

        // Link Books and Authors
        author1.setBooks(Arrays.asList(book1));
        author2.setBooks(Arrays.asList(book2));
        book1.setAuthors(Arrays.asList(author1));
        book2.setAuthors(Arrays.asList(author2));

        bookRepository.saveAll(Arrays.asList(book1, book2));

        // Create Borrow Record
        BorrowRecord record1 = new BorrowRecord();
        record1.setBorrowDate(new Date());
        record1.setLibraryMember(member1);
        record1.setBook(book1);

        borrowRecordRepository.save(record1);

        // Create Users (Admin & Librarian)
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);

        User librarian = new User();
        librarian.setUsername("lib");
        librarian.setPassword(passwordEncoder.encode("lib123"));
        librarian.setEmail("lib@example.com");
        librarian.setRole(Role.LIBRARIAN);

        userRepository.saveAll(List.of(admin, librarian));

        System.out.println("✅ Database Seeded Successfully!");
    }
}
