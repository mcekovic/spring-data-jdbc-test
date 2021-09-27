package org.strangeforest.test.springdatajdbc;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.*;
import org.springframework.test.web.reactive.server.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:tc:postgresql:13:///")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpringDataJdbcApplicationTests {

	@Autowired WebTestClient webTestClient;

	@Test @Order(1)
	void book1IsCreated() {
		var book = new Book(null, "Domain-Driven Design", "Eric Evans", new BookSections(new BookSection(1, "Introduction", 10)));

		saveAndAssertBook(book);
	}

	@Test @Order(2)
	void book2IsCreated() {
		var book = new Book(null, "Refactoring", "Martin Fowler", new BookSections(new BookSection(1, "Introduction", 5)));

		saveAndAssertBook(book);
	}

	@Test @Order(3)
	void allBooksAreQueried() {
		webTestClient.get().uri("/books")
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectBodyList(Book.class)
			.hasSize(2)
			.value(books -> assertThat(books).extracting(Book::title).containsExactlyInAnyOrder("Domain-Driven Design", "Refactoring"));
	}

	@Test @Order(4)
	void book1IsUpdated() {
		var book = webTestClient.get().uri("/books?title=Domain-Driven Design")
			.exchange()
			.returnResult(Book.class)
			.getResponseBody().blockFirst();

		var updatedBook = book.withTitle(book.title() + ": Tackling Complexity in the Heart of Software");

		saveAndAssertBook(updatedBook);
	}

	@Test @Order(5)
	void book2IsUpdated() {
		var book = webTestClient.get().uri("/books?title=Refactoring")
			.exchange()
			.returnResult(Book.class)
			.getResponseBody().blockFirst();

		var updatedBook = book.withTitle(book.title() + ": Improving the Design of Existing Code");

		saveAndAssertBook(updatedBook);
	}

	@Test @Order(6)
	void bookIsNotFound() {
		var books = webTestClient.get().uri("/books?title=Design Patterns")
			.exchange()
			.returnResult(Book.class)
			.getResponseBody().collectList().block();

		assertThat(books).isEmpty();
	}

	@Test @Order(7)
	void booksAreListedByAuthor() {
		var books = webTestClient.get().uri("/books?author=Martin Fowler")
			.exchange()
			.returnResult(Book.class)
			.getResponseBody().collectList().block();

		assertThat(books).extracting(Book::author).containsOnly("Martin Fowler");
	}

	@Test @Order(8)
	void booksAreSearched() {
		var books = webTestClient.get().uri("/books?search=design")
			.exchange()
			.returnResult(Book.class)
			.getResponseBody().collectList().block();

		assertThat(books).isNotEmpty();
		assertThat(books).hasSize(2);
	}

	private void saveAndAssertBook(Book book) {
		webTestClient.post().uri("/books")
			.bodyValue(book)
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectBody(Book.class)
			.value(savedBook -> {
				assertThat(savedBook.id()).isNotNegative();
				assertThat(savedBook.title()).isEqualTo(book.title());
				assertThat(savedBook.author()).isEqualTo(book.author());
			});
	}
}
