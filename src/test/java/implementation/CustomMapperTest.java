package implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CustomMapperTest {
    Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomMapper();
    }

    @Test
    void readFromString() {
    }

    @Test
    void testReadBookingForms() {

    }

    @Test
    void testReadGuests() {

    }

    @Test
    void testReadPreference() {
        Preference preference = mapper.readFromString(Preference.class, "{\"description\":\"^&*UHJNBGYUJNBG\"}");
        Assertions.assertEquals(preference.getDescription(), "^&*UHJNBGYUJNBG");
    }

    @Test
    void testRead() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Mapper mapper = new CustomMapper();
        ReviewComment reviewComment = mapper.readFromString(ReviewComment.class,
                "{\"comment\":\"Одни static'и в работе\",\"resolved\":\"true\"}");
        System.out.println(reviewComment.getComment() + "\n");
        System.out.println(reviewComment.isResolved() + "\n");
    }

    @Test
    void testWriteToStringRW() throws IllegalAccessException {
        CustomMapper mapper = new CustomMapper();
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setComment("Одни static'и в работе");
        reviewComment.setResolved(false);
        reviewComment.setAuthor("Проверяющий #1");
        ReviewComment reviewComment1 = new ReviewComment();
        String string = mapper.writeToString(reviewComment);
        Assertions.assertEquals(string, "{\"comment\":\"Одни static'и в работе\",\"resolved\":\"false\"}");
    }

    @Test
    void testWriteToStringPrefenrence() {
        Preference preference = new Preference();
        preference.setDescription("^&*UHJNBGYUJNBG");
        Assertions.assertEquals(mapper.writeToString(preference), "{\"description\":\"^&*UHJNBGYUJNBG\"}");
    }

    @Test
    void testWriteToStringGuest() {
        Set<Preference> setPref = new HashSet<>();
        Preference preference = new Preference();
        preference.setDescription("^&*UHJNBGYUJNBG");
        Guest guest = new Guest();
        guest.setName("teddy");

        Preference pref = new Preference();
        pref.setDescription("Number:" + 0);
        setPref.add(pref);
        guest.setPreferences(setPref);
        Assertions.assertEquals(mapper.writeToString(guest),
                "{\"name\":\"teddy\",\"preferences\":[{\"description\":\"Number:0\"}]}");
    }

    @Test
    void testWriteToStringBookingForm() {
        BookingForm bookingForm = new BookingForm();
        List<Guest> guests = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            Set<Preference> setPref = new HashSet<>();
            Preference preference = new Preference();
            preference.setDescription("^&*UHJNBGYUJNBG");
            Guest guest = new Guest();
            guest.setName("teddy");
            for (int i = 0; i < 3; i++) {
                Preference pref = new Preference();
                pref.setDescription("Number:" + j + ":" + i);
                setPref.add(pref);
            }
            guest.setPreferences(setPref);
            guests.add(guest);
        }
        bookingForm.setGuests(guests);
        System.out.println(mapper.writeToString(bookingForm));
    }

    @Test
    void testWrite() {
    }
}