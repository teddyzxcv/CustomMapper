package implementation.testclass;

import ru.hse.homework4.Exported;
import ru.hse.homework4.UnknownPropertiesPolicy;

import java.util.List;

@Exported(unknownPropertiesPolicy = UnknownPropertiesPolicy.FAIL)
public class UnknownPolicyTest {
    private List<Integer> listOfInteger;
    private List<String> listOfString;
    private Preference preference;

    public List<Integer> getListOfInteger() {
        return listOfInteger;
    }

    public void setListOfInteger(List<Integer> listOfInteger) {
        this.listOfInteger = listOfInteger;
    }

    public List<String> getListOfString() {
        return listOfString;
    }

    public void setListOfString(List<String> listOfString) {
        this.listOfString = listOfString;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }
}
