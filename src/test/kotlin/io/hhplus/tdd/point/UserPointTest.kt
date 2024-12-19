package io.hhplus.tdd.point

import io.hhplus.tdd.database.StubUserPointTable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserPointTest() {
    val sut = UserPoint(
        id = 1L,
        point = 0,
        updateMillis = System.currentTimeMillis(),
    )

    @BeforeEach fun setup() {
        sut.point = 0
    }

    @Test fun `충전하려는 금액이 최소 충전 금액보다 작은 경우, 실패한다`() {
        //given
        val amount = UserPoint.MIN_AMOUNT_PER_CHARGE - 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(amount)
        }
    }

    @Test fun `충전하려는 금액이 최대 충전 금액보다 큰 경우, 실패한다`() {
        //given
        val amount = UserPoint.MAX_AMOUNT_PER_CHARGE + 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(amount)
        }
    }

    @Test fun `충전 후 금액이 최대 보유 가능 금액보다 큰 경우, 실패한다` () {
        //given
        sut.point = (UserPoint.MIN_AMOUNT_PER_CHARGE..UserPoint.MAX_AMOUNT_PER_CHARGE).random()
        
        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(UserPoint.MAX_TOTAL_AMOUNT - sut.point + 1)
        }
    }

    @Test fun `사용하려는 금액이 최소 사용 금액보다 작은 경우, 실패한다`() {
        //given
        val amount = UserPoint.MIN_AMOUNT_PER_USE - 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(amount)
        }
    }

    @Test fun `사용하려는 금액이 최대 사용 금액보다 큰 경우, 실패한다`() {
        //given
        val amount = UserPoint.MAX_AMOUNT_PER_USE + 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(amount)
        }
    }

    @Test fun `가진 포인트보다 더 큰 포인트를 사용하려는 경우, 실패한다`() {
        //given
        val amount = (sut.point + 1..UserPoint.MAX_AMOUNT_PER_USE).random()

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(amount)
        }
    }
}