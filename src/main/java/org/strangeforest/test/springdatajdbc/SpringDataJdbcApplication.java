package org.strangeforest.test.springdatajdbc;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jdbc.repository.query.*;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import static org.apache.logging.log4j.util.Strings.*;

@SpringBootApplication
public class SpringDataJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJdbcApplication.class, args);
	}
}

record Book(@Id Long id, String title, String author) {}

@Repository
interface BookRepository extends CrudRepository<Book, Long> {

	Iterable<Book> findByTitle(String title);
	Iterable<Book> findByAuthor(String author);
	Iterable<Book> findByTitleAndAuthor(String title, String author);
	@Query("SELECT * FROM book WHERE title ILIKE '%' || :phrase || '%' OR author ILIKE '%' || :phrase || '%'")
	Iterable<Book> search(String phrase);
}

@RestController
@RequestMapping("/books")
class BookResource {

	@Autowired BookRepository repository;

	@GetMapping
	Iterable<Book> allBooks(
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
			}
			else
				return repository.findByAuthor(author);
		}
		else {
			if (isEmpty(author))
				return repository.findByTitle(title);
			else
				return repository.findByTitleAndAuthor(title, author);

		}
	}

	@GetMapping("/{id}")
	Optional<Book> book(@PathVariable long id) {
		return repository.findById(id);
	}

	@PostMapping
	Book save(@RequestBody Book book) {
		return repository.save(book);
	}
}
