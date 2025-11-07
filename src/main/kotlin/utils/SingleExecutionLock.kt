package org.ivcode.kcomfy.utils

import java.util.concurrent.locks.ReentrantLock

/**
 * A lock that ensures a given action is only executed once, even if multiple threads
 * attempt to execute it concurrently. Subsequent callers will wait for the first
 * execution to complete and then return without re-executing the action.
 */
class SingleExecutionLock {
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private var running = false

    /**
     * If this is the first caller, runs [action] and returns its result.
     * Other concurrent callers will wait while the first runs, then return `null`.
     */
    fun runOnce(action: () -> Unit) {
        // Quick guard and mark as running if this caller will execute
        lock.lock()
        try {
            if (running) {
                while (running) {
                    condition.await()
                }
                // Woken up -> someone already executed
                return
            }
            running = true
        } finally {
            lock.unlock()
        }

        // Perform the action outside the lock so waiters actually block on the condition
        try {
            action()
        } finally {
            // Mark finished and wake waiters
            lock.lock()
            try {
                running = false
                condition.signalAll()
            } finally {
                lock.unlock()
            }
        }
    }
}
