package lending.library.book.loan

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode.InstancePerLeaf
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.IncorrectResultSizeDataAccessException
import java.util.*


@DataJpaTest(showSql = false)
internal class BookLoanRepositoryTest(
    val repository: BookLoanRepository
) : FreeSpec({
    isolationMode = InstancePerLeaf

    val bookId = randomBookId()

    "when I search for an active book loan" - {
        "and an active loan is stored for a given bookId" - {
            repository.storeActiveBookLoan(bookId)
            val result = repository.findByBookIdAndActiveIsTrue(bookId.value)

            "it should return this book loan" {
                result.bookId shouldBe bookId
            }
        }

        "and only inactive loans are stored for a given bookId" - {
            repository.storeInactiveBookLoan(bookId)
            repository.storeInactiveBookLoan(bookId)

            "it should throw EmptyResultDataAccessException" {
                shouldThrow<EmptyResultDataAccessException> {
                    repository.findByBookIdAndActiveIsTrue(bookId.value)
                }
            }
        }

        "and multiple active loans are stored for a given bookId" - {
            repository.storeActiveBookLoan(bookId)
            repository.storeActiveBookLoan(bookId)

            "it should throw IncorrectResultSizeDataAccessException" {
                shouldThrow<IncorrectResultSizeDataAccessException> {
                    repository.findByBookIdAndActiveIsTrue(bookId.value)
                }
            }
        }
    }
})

private fun BookLoanRepository.storeActiveBookLoan(bookId: BookId) {
    save(bookLoanOf(bookId))
}

private fun BookLoanRepository.storeInactiveBookLoan(bookId: BookId) {
    val inactiveLoan = bookLoanOf(bookId).copy(active = false)
    save(inactiveLoan)
}

private fun bookLoanOf(bookId: BookId): BookLoan = bookLoanOf(
    bookId,
    UserId("defaultUserId")
)

private fun randomBookId() = BookId(UUID.randomUUID().toString())