package implementation;

import ru.hse.homework4.Exported;

import java.util.Set;

@Exported
public class Guest {
    private String name;
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
