package lending.library.book.loan

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.extensions.spring.SpringExtension
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [BookLoanController::class])
internal class BookLoanControllerTest : FreeSpec() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var service: BookLoanService

    init {
        extension(SpringExtension)

        "when I create a new book loan" - {
            "with valid payload" - {
                val payload = """{"userId":"myUserId","bookId":"myBookId" }"""

                "it should call book service with correct bookId and userId" {
                    postBookLoanRequest(payload)
                    verify(service).borrowBook(BookId("myBookId"), UserId("myUserId"))
                }

                "and service works as expected" - {
                    "it should return 200 OK" {
                        postBookLoanRequest(payload).andExpect(status().isOk)
                    }
                }

                "and service throws BookDoesNotExistException" - {
                    // prefer to use any() but mockito and value classes seem to be incompatible in this case
                    doAnswer { _ ->
                        throw BookDoesNotExistException(BookId("myBookId"))
                    }.whenever(service).borrowBook(BookId("myBookId"), UserId("myUserId"))

                    "it should return 404 Not Found" {
                        postBookLoanRequest(payload).andExpect(status().isNotFound)
                    }
                }

                "and service throws BookAlreadyLentException" - {
                    // prefer to use any() but mockito and value classes seem to be incompatible in this case
                    doAnswer { _ ->
                        throw BookAlreadyLentException(BookId("myBookId"))
                    }.whenever(service).borrowBook(BookId("myBookId"), UserId("myUserId"))

                    "it should return 409 Conflict" {
                        postBookLoanRequest(payload).andExpect(status().isConflict)
                    }
                }
            }

            "with additional attribute" - {
                val payload = """{"userId":"myUserId","bookId":"myBookId","additionalAttribute":"anyValue" }"""

                "it should ignore additional attribute" {
                    postBookLoanRequest(payload)
                    verify(service).borrowBook(BookId("myBookId"), UserId("myUserId"))
                }
            }

            // Tests with invalid payload
            withData(
                mapOf(
                    "with invalid JSON" to "noJson",
                    "with missing bookId" to """{"userId":"myUserId"}""",
                    "with missing userId" to """{"bookId":"myBookId"}""",
                    "with bookId set to null" to """{"userId":"myUserId", "bookId":null}""",
                    "with userId set to null" to """{"userId":null, "bookId":"myBookId"}""",
                )
            ) { requestBody ->
                withData(
                    mapOf(
                        "it should not call service" to { payload: String ->
                            postBookLoanRequest(payload); verifyNoInteractions(service)
                        },
                        "it should return 400 Bad Request" to { payload: String ->
                            postBookLoanRequest(payload).andExpect(status().isBadRequest)
                        },
                    )
                ) { executeTest -> executeTest(requestBody) }
            }
        }
    }

    private fun postBookLoanRequest(payload: String): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.post("/book-loan")
                .contentType(APPLICATION_JSON)
                .content(payload)
        )
    }
}