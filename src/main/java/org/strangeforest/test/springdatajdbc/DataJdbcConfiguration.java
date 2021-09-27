package org.strangeforest.test.springdatajdbc;

import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.postgresql.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.*;
import org.springframework.data.convert.*;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.jdbc.repository.config.*;

@Configuration
public class DataJdbcConfiguration extends AbstractJdbcConfiguration {

	@Autowired
	private ObjectMapper mapper;

	@Bean
	@Override public JdbcCustomConversions jdbcCustomConversions() {
		return new JdbcCustomConversions(List.of(new BookSectionsReadConverter(), new BookSectionsWriteConverter()));
	}

	@ReadingConverter
	public class BookSectionsReadConverter implements Converter<PGobject, BookSections> {

		private static final TypeReference<List<BookSection>> TYPE_REFERENCE = new TypeReference<>() {};

		@Override public BookSections convert(PGobject source) {
			try {
				return new BookSections(mapper.readValue(source.getValue(), TYPE_REFERENCE));
			}
			catch (JsonProcessingException ex) {
				throw new IllegalArgumentException(ex);
			}
		}
	}

	@WritingConverter
	public class BookSectionsWriteConverter implements Converter<BookSections, PGobject> {

		@Override public PGobject convert(BookSections source) {
			try {
				var dbObject = new PGobject();
				dbObject.setType("jsonb");
				dbObject.setValue(mapper.writeValueAsString(source.sections()));
				return dbObject ;
			}
			catch (JsonProcessingException | SQLException ex) {
				throw new IllegalArgumentException(ex);
			}
		}
	}
}
