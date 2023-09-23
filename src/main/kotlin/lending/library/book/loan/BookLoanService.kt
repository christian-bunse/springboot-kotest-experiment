package lending.library.book.loan

import org.hibernate.NonUniqueResultException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

@Service
internal class BookLoanService(val bookRepository: BookRepository, val bookLoanRepository: BookLoanRepository) {
    fun borrowBook(bookId: BookId, userId: UserId) {
        ensureBookExists(bookId)
        ensureBookIsNotYetLent(bookId)

        bookLoanRepository.save(
            bookLoanOf(bookId, userId)
        )
    }

    fun returnBook(bookId: BookId) {
        val activeBookLoan = loadActiveBookLoan(bookId)

        bookLoanRepository.save(
            activeBookLoan.deactivate()
        )
    }

    fun extendBookLoan(bookId: BookId) {
        val activeBookLoan = loadActiveBookLoan(bookId)

        ensureLendingCanBeExtended(activeBookLoan)

        bookLoanRepository.save(
            activeBookLoan.extend()
        )
    }

    private fun ensureBookExists(bookId: BookId) {
        if (!bookRepository.existsById(bookId)) {
            throw BookDoesNotExistException(bookId)
        }
    }

    private fun ensureBookIsNotYetLent(bookId: BookId) {
        if (bookLoanRepository.existsByBookIdAndActiveIsTrue(bookId.toString())) {
            throw BookAlreadyLentException(bookId)
        }
    }

    private fun ensureLendingCanBeExtended(activeBookLoan: BookLoan) {
        if (!activeBookLoan.canBeExtended()) {
            throw BookLoanAlreadyExtendedTooOftenException(activeBookLoan.bookId)
        }
    }

    private fun loadActiveBookLoan(bookId: BookId): BookLoan {
        return try {
            bookLoanRepository.findByBookIdAndActiveIsTrue(bookId.toString())
        } catch (exception: EmptyResultDataAccessException) {
            throw BookLoanNotFoundException(bookId)
        } catch(exception: NonUniqueResultException) {
            throw MultipleActiveBookLoanFoundException(bookId)
        }
    }
}
