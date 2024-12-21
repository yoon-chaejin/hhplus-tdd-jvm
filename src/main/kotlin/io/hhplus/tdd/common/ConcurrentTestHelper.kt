package io.hhplus.tdd.common

import java.util.concurrent.CompletableFuture

object ConcurrentTestHelper {
    fun execute(count: Long, action: () -> Unit) {
        val asyncTasks = (1..count).map {
            CompletableFuture.runAsync { action() }
        }

        CompletableFuture.allOf(*asyncTasks.toTypedArray()).join()
    }
}