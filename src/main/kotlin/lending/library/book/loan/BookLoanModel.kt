package lending.library.book.loan

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.UUID.randomUUID

@Entity
data class BookLoan(
    @Id val id: UUID,
    val userId: UserId,
    val bookId: BookId,
    val active: Boolean,
    val startDate: LocalDate,
    val extensions: Int
)

fun BookLoan.deactivate(): BookLoan {
    return copy(active = false)
}

fun BookLoan.extend(): BookLoan {
    return copy(extensions = extensions + 1)
}

fun BookLoan.canBeExtended(): Boolean {
    return extensions < 2
}

@Suppress("unused")
fun BookLoan.endDate(): LocalDate {
    val defaultLendingDuration = 14
    val maximalLendingDurationInDays = defaultLendingDuration * (extensions + 1)
    return startDate.plus(maximalLendingDurationInDays.toLong(), ChronoUnit.DAYS)
}

fun bookLoanOf(bookId: BookId, userId: UserId) = BookLoan(
    id = randomUUID(),
    userId = userId,
    bookId = bookId,
    active = true,
    startDate = LocalDate.now(),
    extensions = 0
)
