package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointServiceImpl (
    val userPointTable: UserPointTable,
    val pointHistoryTable: PointHistoryTable
) : PointService {

    override fun findUserPointById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun findPointHistories(id: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(id)
    }


    override fun charge(id: Long, amount: Long): UserPoint {
        if (amount <= 0 || amount > 1_000_000) {
            throw Exception("최소 1 포인트에서 최대 백만 포인트까지 충전 가능합니다.")
        }

        val userPoint = userPointTable.selectById(id)

        val pointAfterCharge = userPoint.point + amount

        if (pointAfterCharge > 1_000_000) {
            throw Exception("최대로 충전 가능한 포인트를 초과했습니다.")
        }

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis())
        val userPointAfterCharge = userPointTable.insertOrUpdate(id, pointAfterCharge)

        return userPointAfterCharge
    }
}