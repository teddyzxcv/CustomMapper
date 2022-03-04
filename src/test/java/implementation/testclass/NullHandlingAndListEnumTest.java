package implementation.testclass;

import ru.hse.homework4.Exported;
import ru.hse.homework4.NullHandling;

import java.util.ArrayList;
import java.util.List;

@Exported(nullHandling = NullHandling.EXCLUDE)
public class NullHandlingAndListEnumTest {
    private final Integer integer = null;

    public List<EnumTest> getEnumTests() {
        return enumTests;
    }

    public void setEnumTests(List<EnumTest> enumTests) {
        this.enumTests = enumTests;
    }

    private List<EnumTest> enumTests = new ArrayList<>();
}

