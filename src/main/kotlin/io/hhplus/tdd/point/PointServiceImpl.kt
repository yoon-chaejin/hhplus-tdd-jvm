package io.hhplus.tdd.point

import io.hhplus.tdd.common.ReentrantLockManager
import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointServiceImpl (
    val lockManager: ReentrantLockManager,
    val userPointTable: UserPointTable,
    val pointHistoryTable: PointHistoryTable
) : PointService {

    override fun findUserPointById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun findPointHistoriesByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }


    override fun charge(id: Long, amount: Long): UserPoint {
        return lockManager.execute(id) {
            val userPoint = userPointTable.selectById(id)

            userPoint.charge(amount)
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis())

            userPointTable.insertOrUpdate(id, userPoint.point)
        }
    }

    override fun use(id: Long, amount: Long): UserPoint {
        return lockManager.execute(id) {
            val userPoint = userPointTable.selectById(id)

            userPoint.use(amount)
            pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis())
            userPointTable.insertOrUpdate(id, userPoint.point)
        }
    }
}