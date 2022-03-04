package implementation.testclass;

import ru.hse.homework4.Exported;

@Exported
public class Foo {
    private Bar bar0;
    private Bar bar1;

    public Bar getBar0() {
        return bar0;
    }

    public void setBar0(Bar bar0) {
        this.bar0 = bar0;
    }

    public Bar getBar1() {
        return bar1;
    }

    public void setBar1(Bar bar1) {
        this.bar1 = bar1;
    }
}
