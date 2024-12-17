package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointServiceImpl (
    val userPointTable: UserPointTable,
    val pointHistoryTable: PointHistoryTable
) : PointService {

    companion object CONSTANTS {
        private const val ONE_MILLION = 1_000_000L // 1백만

        const val MAX_TOTAL_AMOUNT = ONE_MILLION

        const val MIN_AMOUNT_PER_CHARGE = 1L
        const val MAX_AMOUNT_PER_CHARGE = ONE_MILLION

        const val MIN_AMOUNT_PER_USE = 1L
        const val MAX_AMOUNT_PER_USE = ONE_MILLION
    }

    override fun findUserPointById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun findPointHistoriesByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }


    override fun charge(id: Long, amount: Long): UserPoint {
        if (amount < MIN_AMOUNT_PER_CHARGE || amount > MAX_AMOUNT_PER_CHARGE) {
            throw Exception("최소 1 포인트에서 최대 백만 포인트까지 충전 가능합니다.")
        }

        val userPoint = userPointTable.selectById(id)

        val pointAfterCharge = userPoint.point + amount

        if (pointAfterCharge > MAX_TOTAL_AMOUNT) {
            throw Exception("최대로 충전 가능한 포인트를 초과했습니다.")
        }

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis())
        val userPointAfterCharge = userPointTable.insertOrUpdate(id, pointAfterCharge)

        return userPointAfterCharge
    }

    override fun use(id: Long, amount: Long): UserPoint {
        if (amount < MIN_AMOUNT_PER_USE || amount > MAX_AMOUNT_PER_USE) {
            throw Exception("최소 1 포인트에서 최대 백만 포인트까지 사용 가능합니다.")
        }

        val userPoint = userPointTable.selectById(id)

        if (userPoint.point < amount) {
            throw Exception("보유한 포인트를 초과하여 사용할 수 없습니다.")
        }

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis())
        val userPointAfterUse = userPointTable.insertOrUpdate(id, userPoint.point - amount)

        return userPointAfterUse
    }
}