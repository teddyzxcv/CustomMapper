package implementation.testclass;

import ru.hse.homework4.Exported;
import ru.hse.homework4.PropertyName;

@Exported
public record Song(
        String artist,
        @PropertyName("myTitle") String title
        ){

}
