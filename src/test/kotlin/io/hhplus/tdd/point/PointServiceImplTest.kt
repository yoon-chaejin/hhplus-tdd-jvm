package io.hhplus.tdd.point

import io.hhplus.tdd.database.FakePointHistoryTable
import io.hhplus.tdd.database.FakeUserPointTable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class PointServiceImplTest
{
    private val userPointTable = FakeUserPointTable()
    private val pointHistoryTable = FakePointHistoryTable()
    private val sut: PointServiceImpl = PointServiceImpl(userPointTable, pointHistoryTable)

    private val userId = 1L

    @AfterEach
    fun tearDown() {
        userPointTable.clearTable()
        pointHistoryTable.clearTable()
    }

    @Test fun `처음 UserPoint를 조회하는 경우, amount가 0이고 해당 id를 갖는 UserPoint를 반환한다`() {
        //given

        //when
        val userPoint = sut.findUserPointById(userId)

        //then
        assertEquals(userId, userPoint.id)
        assertEquals(0, userPoint.point)
    }

    @Test fun `여러 번 UserPoint를 조회하는 경우, 최초와 동일한 UserPoint를 반환한다`() {
        //given
        val userPoint = sut.findUserPointById(userId)

        //when
        val userPointAfterFirstSelect = sut.findUserPointById(userId)

        //then
        assertEquals(userPoint.id, userPointAfterFirstSelect.id)
        assertEquals(userPoint.point, userPointAfterFirstSelect.point)
        assertEquals(userPoint.updateMillis, userPointAfterFirstSelect.updateMillis)
    }

    @Test fun `내역이 없는 사용자를 조회하는 경우, 빈 리스트를 반환하다`() {
        //given

        //when
        val pointHistories = sut.findPointHistoriesByUserId(userId)

        //then
        assertEquals(0, pointHistories.size)
    }

    @Test fun `충전하려는 금액이 최소 충전 금액보다 작은 경우, 실패한다`() {
        //given
        val amount = PointServiceImpl.MIN_AMOUNT_PER_CHARGE - 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(userId, amount)
        }
    }

    @Test fun `충전하려는 금액이 최대 충전 금액보다 큰 경우, 실패한다`() {
        //given
        val amount = PointServiceImpl.MAX_AMOUNT_PER_CHARGE + 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(userId, amount)
        }
    }

    @Test fun `충전하려는 금액이 최소 충전 금액과 최대 충전 금액 사이에 있는 경우, 성공한다`() {
        //given
        val amount = (PointServiceImpl.MIN_AMOUNT_PER_CHARGE..PointServiceImpl.MAX_AMOUNT_PER_CHARGE).random()

        //when
        val userPoint = sut.charge(userId, amount)

        //then
        assertEquals(amount, userPoint.point)
    }

    @Test fun `충전 후 총 포인트가 백만보다 큰 경우, 실패한다`() {
        //given
        val userPointAfterFirstCharge = sut.charge(1L, 1_000_000L)
        assertEquals(1_000_000L, userPointAfterFirstCharge.point)
        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(1L, 1L)
        }
    }

    @Test fun `사용하려는 금액이 최소 사용 금액보다 작은 경우, 실패한다`() {
        //given
        val amount = PointServiceImpl.MIN_AMOUNT_PER_USE - 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(userId, amount)
        }
    }

    @Test fun `사용하려는 금액이 최대 사용 금액보다 큰 경우, 실패한다`() {
        //given
        val amount = PointServiceImpl.MAX_AMOUNT_PER_USE + 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(userId, amount)
        }
    }

    @Test fun `가진 포인트보다 더 큰 포인트를 사용하려는 경우, 실패한다`() {
        //given
        val amount = (PointServiceImpl.MIN_AMOUNT_PER_USE..PointServiceImpl.MAX_AMOUNT_PER_USE).random()
        sut.findUserPointById(userId)

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(userId, amount)
        }
    }
}