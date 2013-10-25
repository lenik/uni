package net.bodz.swt.c.os;

/**
 * This class is just needed to force the compile be done without error.
 */
public class OS_Win32 {

    public static final int VK_CONTROL = 0x11;

    public static final int SW_HIDE = 0;
    public static final int SW_SHOW = 5;

    public static int GetKeyState(int c) {
        // OS.g_signal_connect(null, "key_press_event", key_press_event, 0);
        return 0;
    }

}
