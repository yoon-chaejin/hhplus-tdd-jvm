package io.hhplus.tdd.point

import io.hhplus.tdd.database.StubPointHistoryTable
import io.hhplus.tdd.database.StubUserPointTable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointServiceImplTest
{
    private val pointHistoryTable = StubPointHistoryTable()
    private val userPointTable = StubUserPointTable()
    private val sut = PointServiceImpl(userPointTable, pointHistoryTable)

    private val userId = 1L

    private var userPoint = UserPoint(
        id = userId,
        point = (0..UserPoint.MAX_TOTAL_AMOUNT - 1).random(),
        updateMillis = System.currentTimeMillis(),
    )

    @BeforeEach internal fun setUp() {
        userPointTable.insertOrUpdate(userPoint.id, userPoint.point)
    }

    @Test fun `id로 UserPoint를 조회하는 경우, 해당 id를 갖는 UserPoint를 반환한다`() {
        //given

        //when
        val userPoint = sut.findUserPointById(userId)

        //then
        assertInstanceOf(UserPoint::class.java, userPoint)
        assertEquals(userId, userPoint.id)
    }

    @Test fun `userId로 PointHistory를 조회하는 경우, 해당 userId를 갖는 PointHistory List를 반환한다`() {
        //given

        //when
        val pointHistories = sut.findPointHistoriesByUserId(userId)

        //then
        assertInstanceOf(List::class.java, pointHistories)
        for (pointHistory in pointHistories) {
            assertInstanceOf(PointHistory::class.java, pointHistory)
            assertEquals(userId, pointHistory.userId)
        }
    }

    /* 코드 변경 중 금액이 맞지 않는데 테스트를 통과하는 경우가 발생하여 추가했습니다. */
    @Test fun `충전 금액이 최소 충전 금액보다 크고, 최대 충전 금액보다 작으며, 충전 후 금액이 최대 보유 가능 금액보다 큰 경우, 충전 금액만큼 포인트가 증가한다`() {
        //given
        val amount = (UserPoint.MIN_AMOUNT_PER_CHARGE..UserPoint.MAX_AMOUNT_PER_CHARGE - userPoint.point).random()

        //when
        val result = sut.charge(userId, amount)

        //then
        assertEquals(userPoint.point + amount, result.point)
    }

    /* 코드 변경 중 금액이 맞지 않는데 테스트를 통과하는 경우가 발생하여 추가했습니다. */
    @Test fun `사용 금액이 최소 사용 금액보다 크고, 최대 사용 금액보다 작으며, 가진 포인트를 넘지 않으면, 사용 금액만큼 포인트가 감소한다`() {
        //given
        val amount = (UserPoint.MIN_AMOUNT_PER_USE..Math.min(UserPoint.MAX_AMOUNT_PER_USE, userPoint.point)).random()

        //when
        val result = sut.use(userId, amount)

        //then
        assertEquals(userPoint.point - amount, result.point)
    }
}