package material.window;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;

public interface Dwmapi extends Library {
    public final static Dwmapi INSTANCE = Native.load("dwmapi", Dwmapi.class);

    int DwmpActivateLivePreview(int s, Pointer hWnd, Pointer topmost, int d);

    int DwmSetWindowAttribute(HWND hwnd, int dwAttribute, Pointer pvAttribute, int cbAttribute);

    WinNT.HRESULT DwmEnableMMCSS(WinDef.BOOL fEnableMMCSS);

    WinNT.HRESULT DwmExtendFrameIntoClientArea(HWND hWnd, int[] margins);

    static void addShadowEffect(HWND hwnd) {
        INSTANCE.DwmExtendFrameIntoClientArea(hwnd, new int[]{0, 0, 0, 1});
    }

    static void enableMMCSS(boolean b) {
        INSTANCE.DwmEnableMMCSS(new WinDef.BOOL(b));
    }

    static void useImmersiveDarkMode(HWND hwnd, boolean b){
        INSTANCE.DwmSetWindowAttribute(hwnd,20, Pointer.createConstant(b ? 1 : 0),1);
    }
}

