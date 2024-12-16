package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PointServiceImplTest
{
    val pointService: PointServiceImpl = PointServiceImpl(UserPointTable())

    @Test fun `처음 조회하는 경우, amount가 0이고 해당 id를 갖는 UserPoint를 반환한다`() {
        //given
        val userId = 5L

        //when
        val userPoint = pointService.findUserPointById(userId)

        //then
        assertEquals(userId, userPoint.id)
        assertEquals(0, userPoint.point)
    }

    @Test fun `여러 번 조회하는 경우, 최초와 동일한 UserPoint를 반환한다`() {
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
}