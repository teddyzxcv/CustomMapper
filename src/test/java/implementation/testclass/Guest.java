package implementation.testclass;

import ru.hse.homework4.Exported;
import ru.hse.homework4.PropertyName;

import java.util.Set;

@Exported
public class Guest {
    @PropertyName("Good name")
    private String name;
    @PropertyName("Good Preference")
    private Set<Preference> preferences;

    public String getName() {
        return name;
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<Preference> preferences) {
        this.preferences = preferences;
    }

    public void setName(String name) {
        this.name = name;
    }
}
