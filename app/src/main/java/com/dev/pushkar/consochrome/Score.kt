package com.dev.pushkar.consochrome

import com.dev.pushkar.consochrome.Data.STEPS_PER_LEVEL

class Score : Comparable<Score> {

    private var level: Int = 0
    private var step: Int = 0
    private var time: Long = 0

    init {
        level = 1
        step = 1
        time = 0
    }

    fun level(): Int {
        return level
    }

    fun step(): Int {
        return step
    }

    fun time(): Long {
        return time
    }

    fun increment(timetaken: Long) {
        if (step < INSTANCE.getSTEPS_PER_LEVEL()) {
            step++
        } else {
            level++
            step = 1
        }
        time += timetaken
    }

    override fun compareTo(another: Score): Int {
        if (level > another.level())
            return 1
        else if (level < another.level)
            return -1

        if (step > another.step)
            return 1
        else if (step < another.step)
            return -1

        if (time < another.time)
            return 1
        else if (time > another.time)
            return -1

        return 0
    }

    override fun toString(): String {
        return "[$level.$step $time]"
    }

}
