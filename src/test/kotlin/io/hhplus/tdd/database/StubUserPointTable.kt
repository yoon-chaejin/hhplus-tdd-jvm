package io.hhplus.tdd.database

import io.hhplus.tdd.point.UserPoint

class StubUserPointTable: UserPointTable() {

    private var userPoint: UserPoint = UserPoint(
        id = 1L,
        point = 500,
        updateMillis = System.currentTimeMillis(),
    )

    override fun selectById(id: Long): UserPoint {
        return userPoint
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        userPoint = UserPoint(id = id, point = amount, updateMillis = System.currentTimeMillis())
        return userPoint
    }

}