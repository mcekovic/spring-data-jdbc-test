package org.strangeforest.test.springdatajdbc;

import org.springframework.data.annotation.*;

public record Book(@Id Long id, String title, String author, BookSections sections) {

	public Book withTitle(String title) {
		return new Book(id, title, author, sections);
	}
}
