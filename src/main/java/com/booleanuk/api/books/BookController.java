package com.booleanuk.api.books;

import com.booleanuk.api.authors.Author;
import com.booleanuk.api.authors.AuthorRepository;
import com.booleanuk.api.publishers.Publisher;
import com.booleanuk.api.publishers.PublisherRepository;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PublisherRepository publisherRepository;

    @GetMapping
    public List<Book> getAll(){
        return this.bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getOne(@PathVariable int id){
        return new ResponseEntity<>(
                this.bookRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)),
                HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Book> addOne(@Valid @RequestBody BookRequest book){
        Author author = this.authorRepository
                .findById(book.getAuthor_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a valid author id"));

        Publisher publisher = this.publisherRepository
                .findById(book.getPublisher_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a valid publisher id"));

        Book actualBook = new Book(book.getTitle(), book.getGenre(), author, publisher);

        return new ResponseEntity<>(this.bookRepository.save(actualBook), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateOne(@PathVariable int id, @Valid @RequestBody BookRequest book){
        Book actualBook = this.bookRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book found with id"));

        Author author = this.authorRepository
                .findById(book.getAuthor_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a valid author id"));

        Publisher publisher = this.publisherRepository
                .findById(book.getPublisher_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a valid publisher id"));

        actualBook.setTitle(book.getTitle());
        actualBook.setGenre(book.getGenre());
        actualBook.setAuthor(author);
        actualBook.setPublisher(publisher);

        return new ResponseEntity<>(this.bookRepository.save(actualBook), HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteOne(@PathVariable int id){
        Book book = this.bookRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book found with id"));

        this.bookRepository.delete(book);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
