package io.hhplus.tdd.common

class StubReentrantLockManager : ReentrantLockManager() {
    override fun <T> execute(key: Long, action: () -> T): T {
        return action()
    }
}