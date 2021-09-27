package org.strangeforest.test.springdatajdbc;

import org.springframework.data.jdbc.repository.query.*;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

	Iterable<Book> findByTitle(String title);

	Iterable<Book> findByAuthor(String author);

	Iterable<Book> findByTitleAndAuthor(String title, String author);

	@Query("SELECT * FROM book WHERE title ILIKE '%' || :phrase || '%' OR author ILIKE '%' || :phrase || '%' OR jsonb_path_exists(sections, ('$[*].name ? (@ like_regex \"' || :phrase || '\" flag \"i\")')::JSONPATH)")
	Iterable<Book> search(String phrase);
}
