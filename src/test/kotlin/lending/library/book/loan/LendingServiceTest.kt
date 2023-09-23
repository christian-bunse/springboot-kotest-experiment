package lending.library.book.loan

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode.InstancePerLeaf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.spring.SpringExtension
import org.mockito.Mock
import org.mockito.kotlin.*

@UnitTest
internal class LendingServiceTest : FreeSpec() {
    override fun isolationMode() = InstancePerLeaf

    @Mock lateinit var bookRepository: BookRepository
    @Mock lateinit var bookLoanRepository: BookLoanRepository

    init {
        extension(SpringExtension)

        "When I try to borrow a book " - {
            val service = BookLoanService(bookRepository, bookLoanRepository)

            "and the book cannot be found" - {
                "it should throw BookDoesNotExistException and not persist anything" {
                    shouldThrow<BookDoesNotExistException> {
                        service.borrowBook(BookId("myBookId"), UserId("myUserId"))
                    }
                    verify(bookLoanRepository, never()).save(any())
                }
            }

            "and the book exists" - {
                doAnswer { _ -> true }.whenever(bookRepository).existsById(BookId("myBookId"))

                "and has not been lent" - {
                    doAnswer { _ -> false }.whenever(bookLoanRepository).existsByBookIdAndActiveIsTrue(any())

                    "it should persist book loan" {
                        service.borrowBook(BookId("myBookId"), UserId("myUserId"))
                        verify(bookLoanRepository).save(any())
                    }
                }

                "but has already been lent" - {
                    doAnswer { _ -> true }.whenever(bookLoanRepository).existsByBookIdAndActiveIsTrue(any())

                    "it should throw BookAlreadyLentException and not persist anything" {
                        shouldThrow<BookAlreadyLentException> {
                            service.borrowBook(BookId("myBookId"), UserId("myUserId"))
                        }
                        verify(bookLoanRepository, never()).save(any())
                    }
                }
            }
        }
    }
}