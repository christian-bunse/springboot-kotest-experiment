package lending.library.book.loan

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
internal interface BookRepository : CrudRepository<Book, BookId>

@Repository
internal interface BookLoanRepository : CrudRepository<BookLoan, String> {
    fun findByBookIdAndActiveIsTrue(bookId: String): BookLoan
    fun existsByBookIdAndActiveIsTrue(bookId: String): Boolean
}
