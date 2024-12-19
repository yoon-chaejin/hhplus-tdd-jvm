package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    var point: Long,
    val updateMillis: Long,
) {
    companion object CONSTANTS {
        private const val ONE_MILLION = 1_000_000L // 1백만

        const val MAX_TOTAL_AMOUNT = ONE_MILLION

        const val MIN_AMOUNT_PER_CHARGE = 1L
        const val MAX_AMOUNT_PER_CHARGE = ONE_MILLION

        const val MIN_AMOUNT_PER_USE = 1L
        const val MAX_AMOUNT_PER_USE = ONE_MILLION
    }

    fun charge(amount: Long) {
        if (amount < MIN_AMOUNT_PER_CHARGE || amount > MAX_AMOUNT_PER_CHARGE) {
            throw Exception("최소 1 포인트에서 최대 백만 포인트까지 충전 가능합니다.")
        }

        if (point + amount > MAX_TOTAL_AMOUNT) {
            throw Exception("최대로 충전 가능한 포인트를 초과했습니다.")
        }
        point += amount
    }

    fun use(amount: Long) {
        if (amount < MIN_AMOUNT_PER_USE || amount > MAX_AMOUNT_PER_USE) {
            throw Exception("최소 1 포인트에서 최대 백만 포인트까지 사용 가능합니다.")
        }

        if (point < amount) {
            throw Exception("보유한 포인트를 초과하여 사용할 수 없습니다.")
        }

        point -= amount

    }
}
