package implementation;

import implementation.testclass.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;

import java.util.*;


class CustomSerializerTest {

    Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomMapper();
    }

    /**
     * Test write to stinf ReviewComment
     */
    @Test
    void testWriteToStringRW() {
        CustomMapper mapper = new CustomMapper();
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setComment("Одни \\\"static'и в работе");
        reviewComment.setResolved(false);
        reviewComment.setAuthor("Проверяющий #1");
        String json = mapper.writeToString(reviewComment);
        Assertions.assertEquals(json, "{\"comment\":\"Одни \\\"static'и в работе\",\"resolved\":\"false\"}");
    }

    @Test
    void testWriteToStringPrefenrence() {
        Preference preference = new Preference();
        preference.setDescription("^&*UHJNBGYUJNBG");
        String json = mapper.writeToString(preference);
        Assertions.assertEquals(json, "{\"description\":\"^&*UHJNBGYUJNBG\"}");
    }

    @Test
    void testWriteToStringGuest() {
        Set<Preference> setPref = new HashSet<>();
        Guest guest = new Guest();
        guest.setName("teddy");

        Preference pref = new Preference();
        pref.setDescription("Number:" + 0);
        setPref.add(pref);
        guest.setPreferences(setPref);
        String json = mapper.writeToString(guest);
        Assertions.assertEquals("{\"Good name\":\"teddy\",\"Good Preference\":[{\"description\":\"Number:0\"}]}", json);
    }

    @Test
    void testWriteToStringBookingForm() {
        BookingForm bookingForm = new BookingForm();
        List<Guest> guests = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            Set<Preference> setPref = new HashSet<>();
            Guest guest = new Guest();
            guest.setName("teddy");
            Preference pref = new Preference();
            pref.setDescription("Number:" + j + ":" + 1);
            setPref.add(pref);
            guest.setPreferences(setPref);
            guests.add(guest);
        }
        bookingForm.setGuests(guests);
        Assertions.assertEquals("{\"guests\":[{\"Good name\":\"teddy\",\"Good Preference\":[{\"description\":\"Number:0:1\"}]},{\"Good name\":\"teddy\",\"Good Preference\":[{\"description\":\"Number:1:1\"}]},{\"Good name\":\"teddy\",\"Good Preference\":[{\"description\":\"Number:2:1\"}]}]}", mapper.writeToString(bookingForm));
    }

    @Test
    void testWriteToStringNullHandlingAndListEnum() {
        NullHandlingAndListEnumTest nullHandlingTest = new NullHandlingAndListEnumTest();
        List<EnumTest> list = new ArrayList<>();
        list.add(EnumTest.FIRST);
        list.add(EnumTest.SECOND);
        list.add(EnumTest.THIRD);
        nullHandlingTest.setEnumTests(list);
        String string = mapper.writeToString(nullHandlingTest);
        Assertions.assertEquals(string, "{\"enumTests\":[{FIRST},{SECOND},{THIRD}]}");
    }

    @Test
    void testWriteToStringUnknownPolicy() {
        UnknownPolicyTest unknownPolicyTest = new UnknownPolicyTest();
        unknownPolicyTest.setListOfInteger(Arrays.asList(1, 2, 3, 4, 5, 65, 3, 3));
        unknownPolicyTest.setListOfString(Arrays.asList("asf", "asdsdff", "asd", "12sdfas"));
        Preference preference = new Preference();
        preference.setDescription("asdfasdfasdf");
        unknownPolicyTest.setPreference(preference);
        String string = mapper.writeToString(unknownPolicyTest);
        string = string.substring(0, string.length() - 1);
        string += ",\"supa\":\"asdf\"}";
        try {
            mapper.readFromString(UnknownPolicyTest.class, string);
            Assertions.fail();
        } catch (RuntimeException runtimeException) {
            Assertions.assertNotEquals("", runtimeException.getMessage());
        }
    }

    @Test
    void testWirteToStringEnum() {
        EnumTest enumTest = EnumTest.SECOND;
        String json = mapper.writeToString(enumTest);
        Assertions.assertEquals("{SECOND}", json);
    }

    @Test
    void testWriteToStringDateTime() {
        TimeTest timeTest = new TimeTest();
        String json = mapper.writeToString(timeTest);
        Assertions.assertEquals("{\"Supa Time\":\"2022-13-March 01:02:00\",\"myLocalTime\":\"12:00:21\",\"myLocalDate\":\"13 Mar 2022\"}",
                json);
    }

    @Test
    void testWriteToStringRecord() {
        Song song = new Song("Ted", "SupaSolja");
        String json = mapper.writeToString(song);
        Assertions.assertEquals("{\"artist\":\"Ted\",\"myTitle\":\"SupaSolja\"}", json);
    }
}