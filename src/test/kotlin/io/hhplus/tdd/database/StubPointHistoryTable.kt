package io.hhplus.tdd.database

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType

class StubPointHistoryTable : PointHistoryTable() {

    override fun insert(id: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory {
        return PointHistory(
            id = 1L,
            userId = id,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis,
        )
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return listOf(PointHistory(
            id = 1L,
            userId = userId,
            amount = 1L,
            type = TransactionType.CHARGE,
            timeMillis = System.currentTimeMillis()
        ), PointHistory(
            id = 2L,
            userId = userId,
            amount = 1L,
            type = TransactionType.USE,
            timeMillis = System.currentTimeMillis()
        ))
    }
}