package io.hhplus.tdd.point

interface PointService {
    fun findUserPointById(id: Long): UserPoint

    fun findPointHistoriesByUserId(userId: Long): List<PointHistory>

    fun charge(id: Long, amount: Long): UserPoint

    fun use(id: Long, amount: Long): UserPoint
}