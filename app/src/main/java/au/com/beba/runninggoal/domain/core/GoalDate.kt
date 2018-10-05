package au.com.beba.runninggoal.domain.core

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class GoalDate {

    private var internalUtc: ZonedDateTime? = null

    private val dayMonthYearFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    private val dayMonthFormat = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)

    companion object {
        const val EARLIEST = 0
        const val LATEST = 1
    }

    /**
     * Create GoalDate of "now" (with UTC)
     */
    constructor() {
        this.internalUtc = ZonedDateTime.now(ZoneOffset.UTC)
    }

    constructor(zoned: ZonedDateTime) {
        this.internalUtc = zoned
    }

    constructor(epochSecond: Long) {
        val startInstant = Instant.ofEpochSecond(epochSecond)
        this.internalUtc = ZonedDateTime.ofInstant(startInstant, ZoneOffset.UTC)
    }

    /**
     * Constructs GoalDate using date value as String and
     * optional time MIN (00:00:00) or MAX (23:59:59)
     */
    constructor(dateAsString: String, time: Int = LATEST) {
        this.internalUtc = stringToUtc(dateAsString, time)
    }

    constructor(year: Int, month: Int, dayOfMonth: Int) {
        // FORMAT 1/9/2018 => 01/09/2018 AND PARSE IT TO UTC DATE
        this.internalUtc = stringToUtc("%02d/%02d/%04d".format(dayOfMonth, month, year), EARLIEST)
    }

    val yearLocal: Int
        get() {
            return asLocal.year
        }

    val monthLocal: Int
        get() {
            return asLocal.monthValue
        }

    val dayOfMonthLocal: Int
        get() {
            return asLocal.dayOfMonth
        }

    fun asDisplayLocalLong(): String {
        return dayMonthYearFormat.format(asLocal)
    }

    fun asDisplayLocalShort(): String {
        return dayMonthFormat.format(asLocal)
    }

    fun asEpochUtc(): Long {
        return internalUtc!!.toEpochSecond()
    }

    fun isBefore(from: GoalDate): Boolean {
        return internalUtc!!.isBefore(from.internalUtc)
    }

    fun isAfter(from: GoalDate): Boolean {
        return internalUtc!!.isAfter(from.internalUtc)
    }

    fun daysBetween(to: GoalDate): Int {
        var daysBetween = 0
        // CALCULATE WHEN from==to OR from IS BEFORE to
        if (!isAfter(to)) {
            daysBetween = Duration.between(asLocal, to.asLocal).toDays().toInt()
            daysBetween++
        }
        return daysBetween
    }

    // EVALUATE ONCE (WHEN FIRST) REQUESTED
    private val asLocal: LocalDateTime get() = internalUtc!!.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

    private fun stringToUtc(dateAsString: String, time: Int = LATEST): ZonedDateTime {
        val localDate = LocalDate.parse(dateAsString, dayMonthYearFormat)
        // CONVERT FROM *ANY*  ZONE TO UTC ZONE
        return ZonedDateTime
                .of(
                        localDate,
                        when (time) {
                            EARLIEST -> LocalTime.MIN
                            else -> LocalTime.MAX
                        },
                        ZoneOffset.systemDefault()
                )
                .toOffsetDateTime()
                .atZoneSameInstant(ZoneOffset.UTC)

    }
}
