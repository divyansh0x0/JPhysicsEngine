package material.window;

import com.sun.jna.platform.win32.User32;

public interface User32Ex extends User32 {
    int GWLP_WNDPROC = -4;

    LONG_PTR SetWindowLongPtr(HWND hwnd, int nIndex, WindowProc wndProc);

    LONG_PTR SetWindowLongPtr(HWND hwnd, int nIndex, LONG_PTR wndProc);

    LRESULT CallWindowProc(LONG_PTR proc, HWND hwnd, int uMsg, WPARAM uParam, LPARAM lparam);

}
