package NatSelection;

public class Nothing {
    private static final Nothing instance = new Nothing();

    private Nothing() {}

    public static Nothing getInstance() {
        return instance;
    }
}
