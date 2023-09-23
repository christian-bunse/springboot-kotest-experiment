package lending.library.book.loan

import mu.KotlinLogging.logger
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController


@RestController
internal class BookLoanController(val libraryService: BookLoanService) {
    private val log = logger {}

    @RequestMapping(method = [POST], value = ["/book-loan"])
    fun borrowBook(@RequestBody bookLoanRequest: BookLoanRequest) {
        val (bookId, userId) = bookLoanRequest
        libraryService.borrowBook(bookId, userId)

        log.info { "Book with $bookId was successfully lent to $userId" }
    }

    @RequestMapping(method = [DELETE], value = ["/book-loan"])
    fun returnBook(@RequestBody bookId: BookId) {
        libraryService.returnBook(bookId)
        log.info { "Book with $bookId was successfully returned" }
    }

    @RequestMapping(method = [POST], value = ["/lending/extension"])
    fun extendBookLoan(@RequestBody bookId: BookId) {
        libraryService.extendBookLoan(bookId)
        log.info { "Loan of book with $bookId was successfully extended" }
    }
}

internal data class BookLoanRequest(
    val bookId: BookId,
    val userId: UserId
)
