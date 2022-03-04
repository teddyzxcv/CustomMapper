package implementation.testclass;

import ru.hse.homework4.Exported;

import java.util.List;

@Exported
public class BookingForm {
    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    private List<Guest> guests;
}

