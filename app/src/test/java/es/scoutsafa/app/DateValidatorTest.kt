package es.scoutsafa.app

import es.scoutsafa.app.util.DateValidator
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DateValidatorTest {

    @Test
    fun validDate_returnsNull() {
        assertNull(DateValidator.validate("15/03/2026"))
    }

    @Test
    fun leapDay_onlyValidInLeapYears() {
        assertNull(DateValidator.validate("29/02/2028"))
        assertNotNull(DateValidator.validate("29/02/2027"))
    }

    @Test
    fun wrongFormat_isRejected() {
        assertNotNull(DateValidator.validate("2026-03-15"))
        assertNotNull(DateValidator.validate("1/3/2026"))
    }

    @Test
    fun impossibleDay_isRejected() {
        assertNotNull(DateValidator.validate("31/04/2026"))
    }

    @Test
    fun validTime_returnsNull() {
        assertNull(DateValidator.validateTime("00:00"))
        assertNull(DateValidator.validateTime("23:59"))
    }

    @Test
    fun invalidTime_isRejected() {
        assertNotNull(DateValidator.validateTime("24:00"))
        assertNotNull(DateValidator.validateTime("12:60"))
        assertNotNull(DateValidator.validateTime("9:30"))
    }
}
