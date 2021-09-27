package org.strangeforest.test.springdatajdbc;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import static org.apache.logging.log4j.util.Strings.*;

@RestController
@RequestMapping("/books")
public class BookResource {

	@Autowired
	private BookRepository repository;

	@GetMapping
	public Iterable<Book> allBooks(
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String search
	) {
		if (isEmpty(title)) {
			if (isEmpty(author)) {
				if (isEmpty(search))
					return repository.findAll();
				else
					return repository.search(search);
			} else
				return repository.findByAuthor(author);
		} else {
			if (isEmpty(author))
				return repository.findByTitle(title);
			else
				return repository.findByTitleAndAuthor(title, author);

		}
	}

	@GetMapping("/{id}")
	public Optional<Book> book(@PathVariable long id) {
		return repository.findById(id);
	}

	@PostMapping
	public Book save(@RequestBody Book book) {
		return repository.save(book);
	}
}
