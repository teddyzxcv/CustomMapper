package implementation.testclass;

import ru.hse.homework4.DateFormat;
import ru.hse.homework4.Exported;
import ru.hse.homework4.PropertyName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Exported
public class TimeTest {
    public LocalDateTime getMyLocalDateTime() {
        return myLocalDateTime;
    }

    public void setMyLocalDateTime(LocalDateTime myLocalDateTime) {
        this.myLocalDateTime = myLocalDateTime;
    }

    public LocalTime getMyLocalTime() {
        return myLocalTime;
    }

    public void setMyLocalTime(LocalTime myLocalTime) {
        this.myLocalTime = myLocalTime;
    }

    public LocalDate getMyLocalDate() {
        return myLocalDate;
    }

    public void setMyLocalDate(LocalDate myLocalDate) {
        this.myLocalDate = myLocalDate;
    }

    @PropertyName("Supa Time")
    @DateFormat("uuuu-dd-MMMM HH:mm:ss")
    private LocalDateTime myLocalDateTime = LocalDateTime.of(2022, 3, 13, 1, 2);
    @DateFormat("HH:ss:mm")
    private LocalTime myLocalTime = LocalTime.of(12, 21);
    @DateFormat("dd MMM uuuu")
    private LocalDate myLocalDate = LocalDate.of(2022, 3, 13);
}
