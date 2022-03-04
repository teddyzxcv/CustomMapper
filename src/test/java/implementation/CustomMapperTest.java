package implementation;

import implementation.testclass.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

class CustomMapperTest {
    Mapper mapper;

    /**
     * Set Up
     */
    @BeforeEach
    void setUp() {
        mapper = new CustomMapper();
    }

    /**
     * Test read file.
     * @throws IOException
     */
    @Test
    void testReadFile() throws IOException {
        File file = new File("output.txt");
        Guest guest = mapper.read(Guest.class, file);
        Assertions.assertEquals("Number:0", guest.getPreferences().stream().toList().get(0).getDescription());
        Assertions.assertEquals("teddy", guest.getName());
    }

    /**
     * Test write file.
     * @throws IOException
     */
    @Test
    void testWriteFile() throws IOException {
        Set<Preference> setPref = new HashSet<>();
        Guest guest = new Guest();
        guest.setName("teddy");
        Preference pref = new Preference();
        pref.setDescription("Number:" + 0);
        setPref.add(pref);
        guest.setPreferences(setPref);
        File file = new File("output.txt");
        mapper.write(guest, file);
    }

}