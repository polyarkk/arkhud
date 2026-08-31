package cc.arkko.mc.arkhud.forge.utils;

public class SealedValue<T> {
    private T v;

    public SealedValue(T defaultValue) {
        v = defaultValue;
    }

    public T get() {
        return v;
    }

    public void set(T v) {
        this.v = v;
    }
}
