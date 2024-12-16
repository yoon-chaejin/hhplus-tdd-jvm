package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PointServiceImplTest
{
    val pointService: PointServiceImpl = PointServiceImpl(UserPointTable(), PointHistoryTable())

    @Test fun `처음 UserPoint를 조회하는 경우, amount가 0이고 해당 id를 갖는 UserPoint를 반환한다`() {
        //given
        val userId = 5L

        //when
        val userPoint = pointService.findUserPointById(userId)

        //then
        assertEquals(userId, userPoint.id)
        assertEquals(0, userPoint.point)
    }

    @Test fun `여러 번 UserPoint를 조회하는 경우, 최초와 동일한 UserPoint를 반환한다`() {
        //given
        val userId = 5L
        val userPoint = pointService.findUserPointById(userId)

        //when
        val userPointAfterFirstSelect = pointService.findUserPointById(userId)

        //then
        assertEquals(userPoint.id, userPointAfterFirstSelect.id)
        assertEquals(userPoint.point, userPointAfterFirstSelect.point)
        assertEquals(userPoint.updateMillis, userPointAfterFirstSelect.updateMillis)
    }

    @Test fun `내역이 없는 사용자를 조회하는 경우, 빈 리스트를 반환하다`() {
        //given
        val userId = 1L

        //when
        val pointHistories = pointService.findPointHistories(userId)

        //then
        assertEquals(0, pointHistories.size)
    }

    @Test fun `충전하려는 금액이 0인 경우, 실패한다`() {
        //given
        val userId = 1L
        val amount = 0L

        //when

        //then
        assertThrows(Exception::class.java) {
            pointService.charge(userId, amount)
        }
    }

    @Test fun `충전하려는 금액이 백만보다 큰 경우, 실패한다`() {
        //given
        val userId = 1L
        val amount = 1_000_001L

        //when

        //then
        assertThrows(Exception::class.java) {
            pointService.charge(userId, amount)
        }
    }

    @Test fun `충전하려는 금액이 백만인 경우, 성공한다`() {
        //given
        val userId = 1L
        val amount = 1_000_000L

        //when
        val userPoint = pointService.charge(userId, amount)

        //then
        assertEquals(amount, userPoint.point)
    }

    @Test fun `특정 유저가 충전 후 총 포인트가 백만보다 큰 경우, 실패한다`() {
        //given
        val userPointAfterFirstCharge = pointService.charge(1L, 1_000_000L)
        assertEquals(1_000_000L, userPointAfterFirstCharge.point)
        //when

        //then
        assertThrows(Exception::class.java) {
            pointService.charge(1L, 1L)
        }
    }
}