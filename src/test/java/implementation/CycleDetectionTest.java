package implementation;

import implementation.testclass.Bar;
import implementation.testclass.Foo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;

public class CycleDetectionTest {
    @Test
    void testIsCycle() {
        Mapper mapper = new CustomMapper();
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        Bar bar1 = new Bar();
        Bar bar2 = new Bar();
        foo1.setBar0(bar1);
        foo1.setBar1(bar1);
        bar1.setFoo(foo2);
        foo2.setBar0(bar2);
        foo2.setBar1(bar2);
        bar2.setFoo(foo1);
        try {
            mapper.writeToString(foo1);
        } catch (RuntimeException ignored) {
            Assertions.assertNotEquals("", ignored.getMessage());
        }

    }
}
