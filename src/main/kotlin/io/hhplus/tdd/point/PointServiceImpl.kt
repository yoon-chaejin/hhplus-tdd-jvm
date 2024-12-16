package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointServiceImpl (
    val userPointTable: UserPointTable,
) : PointService {

    override fun findUserPointById(id: Long): UserPoint {
        return userPointTable.selectById(id);
    }
}