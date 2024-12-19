package io.hhplus.tdd.point

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class PointServiceIntegrationTest (
    @Autowired val sut: PointServiceImpl,
) {

    @Test fun chargeConcurrencyTest() {
        //given
        val numOfThreads = 10
        val userId = 1L
        val amount = 50L

        val executorService = Executors.newFixedThreadPool(numOfThreads)
        val doneSignal = CountDownLatch(numOfThreads)

        //when
        for (i in 1..numOfThreads) {
            executorService.execute {
                sut.charge(userId, amount)
                doneSignal.countDown()
            }
        }

        doneSignal.await()
        executorService.shutdown()

        val result = sut.findUserPointById(userId)

        //then
        assertEquals(amount * numOfThreads, result.point)
    }

    @Test fun useConcurrencyTest() {
        //given
        val numOfThreads = 20
        val userId = 1L
        val amount = 50L
        sut.charge(userId, amount * numOfThreads)

        val executorService = Executors.newFixedThreadPool(numOfThreads)
        val doneSignal = CountDownLatch(numOfThreads)

        //when
        for (i in 1..numOfThreads) {
            executorService.execute {
                sut.use(userId, amount)
                doneSignal.countDown()
            }
        }

        doneSignal.await()
        executorService.shutdown()

        val result = sut.findUserPointById(userId)

        //then
        assertEquals(0, result.point)
    }
}