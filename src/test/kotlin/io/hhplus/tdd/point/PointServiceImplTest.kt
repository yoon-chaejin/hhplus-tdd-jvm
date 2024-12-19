package io.hhplus.tdd.point

import io.hhplus.tdd.database.StubPointHistoryTable
import io.hhplus.tdd.database.StubUserPointTable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PointServiceImplTest
{
    private val pointHistoryTable = StubPointHistoryTable()

    private val userId = 1L

    private val userPointWithZeroPoint = UserPoint(
        id = userId,
        point = 0,
        updateMillis = System.currentTimeMillis(),
    )

    private val userPointWithNonZeroPoint = UserPoint(
        id = userId,
        point = (PointServiceImpl.MIN_AMOUNT_PER_CHARGE..PointServiceImpl.MAX_AMOUNT_PER_CHARGE).random(),
        updateMillis = System.currentTimeMillis(),
    )

    @Test fun `id로 UserPoint를 조회하는 경우, 해당 id를 갖는 UserPoint를 반환한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithNonZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)

        //when
        val userPoint = sut.findUserPointById(userId)

        //then
        assertInstanceOf(UserPoint::class.java, userPoint)
        assertEquals(userId, userPoint.id)
    }

    @Test fun `userId로 PointHistory를 조회하는 경우, 해당 userId를 갖는 PointHistory List를 반환한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithNonZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)

        //when
        val pointHistories = sut.findPointHistoriesByUserId(userId)

        //then
        assertInstanceOf(List::class.java, pointHistories)
        for (pointHistory in pointHistories) {
            assertInstanceOf(PointHistory::class.java, pointHistory)
            assertEquals(userId, pointHistory.userId)
        }
    }

    @Test fun `충전하려는 금액이 최소 충전 금액보다 작은 경우, 실패한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val amount = PointServiceImpl.MIN_AMOUNT_PER_CHARGE - 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(userId, amount)
        }
    }

    @Test fun `충전하려는 금액이 최대 충전 금액보다 큰 경우, 실패한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val amount = PointServiceImpl.MAX_AMOUNT_PER_CHARGE + 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(userId, amount)
        }
    }

    @Test fun `충전하려는 금액이 최소 충전 금액과 최대 충전 금액 사이에 있는 경우, 성공한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val amount = (PointServiceImpl.MIN_AMOUNT_PER_CHARGE..PointServiceImpl.MAX_AMOUNT_PER_CHARGE).random()

        //when
        val userPoint = sut.charge(userId, amount)

        //then
        assertEquals(amount, userPoint.point)
    }

    @Test fun `충전 후 금액이 최대 보유 가능 금액보다 큰 경우, 실패한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithNonZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val userPoint = sut.findUserPointById(userId)

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.charge(userId, PointServiceImpl.MAX_TOTAL_AMOUNT - userPoint.point + 1)
        }
    }

    @Test fun `사용하려는 금액이 최소 사용 금액보다 작은 경우, 실패한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithNonZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val amount = PointServiceImpl.MIN_AMOUNT_PER_USE - 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(userId, amount)
        }
    }

    @Test fun `사용하려는 금액이 최대 사용 금액보다 큰 경우, 실패한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithNonZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val amount = PointServiceImpl.MAX_AMOUNT_PER_USE + 1

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(userId, amount)
        }
    }

    @Test fun `가진 포인트보다 더 큰 포인트를 사용하려는 경우, 실패한다`() {
        //given
        val userPointTable = StubUserPointTable(userPointWithNonZeroPoint)
        val sut = PointServiceImpl(userPointTable, pointHistoryTable)
        val userPoint = sut.findUserPointById(userId)
        val amount = (userPoint.point + 1..PointServiceImpl.MAX_AMOUNT_PER_USE).random()

        //when

        //then
        assertThrows(Exception::class.java) {
            sut.use(userId, amount)
        }
    }
}