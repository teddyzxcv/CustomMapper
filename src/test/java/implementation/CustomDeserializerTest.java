package implementation;

import implementation.testclass.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomDeserializerTest {
    Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomMapper();
    }

    /**
     * Read from string datetime.
     */
    @Test
    void testReadFromStringDateTime() {
        TimeTest timeTest = new TimeTest();
        String json = mapper.writeToString(timeTest);
        TimeTest timeTest1 = mapper.readFromString(TimeTest.class, json);
        Assertions.assertEquals("2022-03-13T01:02", timeTest1.getMyLocalDateTime().toString());
        Assertions.assertEquals("2022-03-13", timeTest1.getMyLocalDate().toString());
        Assertions.assertEquals("12:21", timeTest1.getMyLocalTime().toString());
    }

    /**
     * Test read enum.
     */
    @Test
    void testReadFromStringEnum() {
        EnumTest enumTest = EnumTest.THIRD;
        String json = mapper.writeToString(enumTest);
        EnumTest enumTest2 = mapper.readFromString(EnumTest.class, json);
        Assertions.assertEquals(enumTest, enumTest2);
    }

    /**
     * Test read booking forms.
     */
    @Test
    void testReadBookingForms() {
        BookingForm bookingForm = new BookingForm();
        List<Guest> guests = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            Set<Preference> setPref = new HashSet<>();
            Guest guest = new Guest();
            guest.setName("teddy");
            Preference pref = new Preference();
            pref.setDescription("Sth" + j);
            setPref.add(pref);
            guest.setPreferences(setPref);
            guests.add(guest);
        }
        bookingForm.setGuests(guests);
        String json = mapper.writeToString(bookingForm);
        BookingForm bf = mapper.readFromString(BookingForm.class, json);
        for (int i = 0; i < 3; i++) {
            Assertions.assertEquals("teddy", bf.getGuests().get(i).getName());
            Assertions.assertEquals("Sth" + i, bf.getGuests().get(i).getPreferences().stream().toList().get(0).getDescription());
        }

    }

    /**
     * Test read guests.
     */
    @Test
    void testReadGuests() {
        Set<Preference> setPref = new HashSet<>();
        Guest guest = new Guest();
        guest.setName("teddy");

        Preference pref = new Preference();
        pref.setDescription("Number:" + 0);
        setPref.add(pref);
        guest.setPreferences(setPref);
        String serialized = mapper.writeToString(guest);
        Guest guestRead = mapper.readFromString(Guest.class, serialized);
        Assertions.assertEquals("Number:0", guestRead.getPreferences().stream().toList().get(0).getDescription());
        Assertions.assertEquals("teddy", guestRead.getName());

    }

    /**
     * Test read preference.
     */
    @Test
    void testReadPreference() {
        Preference preference = mapper.readFromString(Preference.class, "{\"description\":\"^&*UHJNBGYUJNBG\"}");
        Assertions.assertEquals(preference.getDescription(), "^&*UHJNBGYUJNBG");
    }

    @Test
    void testReadRW() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Mapper mapper = new CustomMapper();
        ReviewComment reviewComment = mapper.readFromString(ReviewComment.class, "{\"comment\":\"Одни \\\"static'и в работе\",\"resolved\":\"true\"}");
        Assertions.assertEquals("Одни \\\"static'и в работе", reviewComment.getComment());
        Assertions.assertEquals(true, reviewComment.isResolved());
    }

    /**
     * Test read from string record.
     */
    @Test
    void testReadFromStringRecord() {
        Song song = new Song("Ted", "SupaSolja");
        String json = mapper.writeToString(song);
        Song song1 = mapper.readFromString(Song.class, json);
        Assertions.assertEquals(json, mapper.writeToString(song1));
    }
}