package io.hhplus.tdd.point

interface PointService {
    fun findUserPointById(id: Long): UserPoint

    fun findPointHistories(id: Long): List<PointHistory>

}