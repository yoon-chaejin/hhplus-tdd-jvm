package io.hhplus.tdd.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReentrantLockManagerTest {
    private val sut = ReentrantLockManager()

    @Test fun `후위 증가 연산자를 사용할 때 락을 사용하지 않으면, 실패한다`() {
        //given
        var count = 0L
        val expected = 1000L

        //when
        ConcurrentTestHelper.execute(expected) {
            count++
        }

        //then
        assert(count != expected)
    }

    @Test fun `후위 증가 연산자를 사용할 때 락을 사용하면, 성공한다`() {
        //given
        var count = 0L
        val expected = 1000L

        //when
        ConcurrentTestHelper.execute(expected) {
            sut.execute(1L) { count++ }
        }

        //then
        assertEquals(expected, count)
    }
}