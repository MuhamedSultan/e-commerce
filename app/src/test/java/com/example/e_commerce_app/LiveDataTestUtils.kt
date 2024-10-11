import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeoutException
import java.util.concurrent.TimeUnit

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
suspend fun <T> StateFlow<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterCollect: () -> Unit = {}
): T {
    var data: T? = null
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    try {
        withContext(Dispatchers.Default.limitedParallelism(1)) {
            withTimeout(timeUnit.toMillis(time)) {
                scope.launch {
                    collect { value ->
                        data = value
                        job.complete()
                    }
                }
                afterCollect()
            //   job.join()
            }
        }
    } catch (e: TimeoutException) {
        throw TimeoutException("StateFlow value was never set within the time limit.")
    } finally {
        job.cancel() // Ensure the job is cancelled after completion or timeout
    }

    return data ?: throw IllegalStateException("StateFlow did not emit any value.")
}
