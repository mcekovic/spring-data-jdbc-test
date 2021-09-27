CREATE TABLE book (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    sections JSONB
);

CREATE INDEX book_title ON book (title);
CREATE INDEX book_author ON book (author);