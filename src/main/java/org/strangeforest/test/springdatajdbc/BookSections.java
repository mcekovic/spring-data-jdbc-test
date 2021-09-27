package org.strangeforest.test.springdatajdbc;

import java.util.*;

public record BookSections(List<BookSection> sections) {

	public BookSections(BookSection... sections) {
		this(List.of(sections));
	}
}
