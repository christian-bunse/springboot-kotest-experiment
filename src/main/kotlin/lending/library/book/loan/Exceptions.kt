package lending.library.book.loan

import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ResponseStatus

open class BookLoanException(message: String) : Exception(message)

@ResponseStatus(NOT_FOUND)
class BookDoesNotExistException(bookId: BookId) : BookLoanException("Book with $bookId could not found.")

@ResponseStatus(CONFLICT)
class BookAlreadyLentException(bookId: BookId) : BookLoanException("Book with $bookId is not available, it has already been lent.")

@ResponseStatus(FORBIDDEN)
class BookLoanAlreadyExtendedTooOftenException(bookId: BookId) : BookLoanException("Loan for book with $bookId cannot be extended anymore, maximal number of extensions reached.")

@ResponseStatus(NOT_FOUND)
class BookLoanNotFoundException(bookId: BookId) : BookLoanException("No active loan for book with $bookId could be found.")

@ResponseStatus(INTERNAL_SERVER_ERROR)
class MultipleActiveBookLoanFoundException(bookId: BookId) : BookLoanException("Cannot process request: Multiple loans for book with $bookId could be found.")
