package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Utils;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void testFormatDate() {
        // Arrange
        Date date = new Date();
        String pattern = "yyyy-MM-dd";

        // Act
        String formatted = DateUtils.formatDate(date, pattern);

        // Assert
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        assertEquals(sdf.format(date), formatted);
    }

    @Test
    void testParseDate() {
        // Arrange
        String dateString = "2024-01-15";
        String pattern = "yyyy-MM-dd";

        // Act
        Date date = DateUtils.parseDate(dateString, pattern);

        // Assert
        assertNotNull(date);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        assertEquals(dateString, sdf.format(date));
    }

    @Test
    void testParseDate_InvalidFormat() {
        // Arrange
        String invalidDateString = "invalid-date";
        String pattern = "yyyy-MM-dd";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> DateUtils.parseDate(invalidDateString, pattern));
    }

    @Test
    void testFormatLocalDateTime() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        String pattern = "yyyy-MM-dd HH:mm:ss";

        // Act
        String formatted = DateUtils.formatLocalDateTime(dateTime, pattern);

        // Assert
        assertEquals("2024-01-15 10:30:00", formatted);
    }

    @Test
    void testParseLocalDateTime() {
        // Arrange
        String dateString = "2024-01-15 10:30:00";
        String pattern = "yyyy-MM-dd HH:mm:ss";

        // Act
        LocalDateTime dateTime = DateUtils.parseLocalDateTime(dateString, pattern);

        // Assert
        assertEquals(2024, dateTime.getYear());
        assertEquals(1, dateTime.getMonthValue());
        assertEquals(15, dateTime.getDayOfMonth());
        assertEquals(10, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
    }

    @Test
    void testToIsoString() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        // Act
        String isoString = DateUtils.toIsoString(dateTime);

        // Assert
        assertEquals("2024-01-15 10:30:00", isoString);
    }

    @Test
    void testFromIsoString() {
        // Arrange
        String isoString = "2024-01-15 10:30:00";

        // Act
        LocalDateTime dateTime = DateUtils.fromIsoString(isoString);

        // Assert
        assertEquals(2024, dateTime.getYear());
        assertEquals(1, dateTime.getMonthValue());
        assertEquals(15, dateTime.getDayOfMonth());
    }

    @Test
    void testCurrentDateTimeAsString() {
        // Arrange
        String pattern = "yyyy-MM-dd";

        // Act
        String currentDate = DateUtils.currentDateTimeAsString(pattern);

        // Assert
        assertNotNull(currentDate);
        assertEquals(10, currentDate.length()); // yyyy-MM-dd is 10 chars
    }
}
