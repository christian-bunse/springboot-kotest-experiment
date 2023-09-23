package lending.library.book.loan

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class Book(
    @Id val id: UUID,
    val bookId: BookId
)

@JvmInline
value class UserId(val value: String)

@JvmInline
value class BookId(val value: String)
