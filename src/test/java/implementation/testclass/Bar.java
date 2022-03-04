package implementation.testclass;

import ru.hse.homework4.Exported;

/**
 * Class for test cycle and retainIdentity.
 */
@Exported
public class Bar {
    public String getOkay() {
        return okay;
    }

    public void setOkay(String okay) {
        this.okay = okay;
    }

    private String okay;

    private Foo foo;

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }
}
