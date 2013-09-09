package org.eclipse.swt.internal.win32;

/**
 * This class is just needed to force the compile be done without error.
 */
@Deprecated
public class OS {

    public static final int VK_CONTROL = 0;

    public static final int SW_SHOW = 0;
    public static final int SW_HIDE = 0;

    public static int GetKeyState(int c) {
        return 0;
    }

}
