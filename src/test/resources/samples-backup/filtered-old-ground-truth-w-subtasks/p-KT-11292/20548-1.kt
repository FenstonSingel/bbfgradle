// Original bug: KT-25644
// Duplicated bug: KT-20548

package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@DisplayName("GIVEN a slow greeting repository")
@ExtendWith(MockitoExtension.class)
internal class SlowGreetingServiceIT {
    private val greetingRepository = mockk<GreetingRepository>();

    @DisplayName("WHEN it is in progress")
    @Nested
    inner class InProgress {
        @DisplayName("THEN it shows progress")
        @Test
        fun shouldBePartDone() {
            val repository = SlowGreetingService(
                    2000, MILLISECONDS, greetingRepository)
            repository.create("Brian")
            SECONDS.sleep(1)
            val percentage = repository["Brian"].percentage
            repository.delete("Brian")
            assertTrue(percentage in 1 until 100,
                    "$percentage not between 0 and 100")
        }
    }

    @DisplayName("WHEN it is completed")
    @Nested
    inner class Completed {
        @DisplayName("THEN it shows 100% progress")
        @Test
        fun shouldBePartDone() {
            val repository = SlowGreetingService(1, MILLISECONDS, greetingRepository)
            repository.create("Brian")
            SECONDS.sleep(1)
            val percentage = repository["Brian"].percentage
            repository.delete("Brian")
            assertEquals(100, percentage)
        }
    }
}