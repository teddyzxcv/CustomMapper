package implementation;

import implementation.CustomMapper;
import implementation.testclass.Bar;
import implementation.testclass.Foo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.homework4.Mapper;

public class RetainIdentityTest {
    @Test
    void retainIdTest() {
        Foo foo = new Foo();
        Bar bar = new Bar();
        foo.setBar0(bar);
        foo.setBar1(bar);
        boolean retainIdentity = true;
        Mapper mapper = new CustomMapper(retainIdentity);
        bar.setOkay("okay1");
        String json = mapper.writeToString(foo);
        Foo restored = mapper.readFromString(Foo.class, json);
        Assertions.assertEquals(restored.getBar0(), restored.getBar1());
    }


}
