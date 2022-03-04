package implementation.testclass;

import ru.hse.homework4.Exported;

@Exported
public class Preference {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
