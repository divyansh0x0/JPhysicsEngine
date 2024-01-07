package material.window;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;
import material.utils.Log;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.sun.jna.platform.win32.WinUser.*;

public class BorderlessWindowProc implements WindowProc {
    private static final HWND HWND_TOP = new HWND(new Pointer(0));
    public final static long WS_EX_CLIENTEDGE = 0x00000200L;
    public final static long WS_EX_WINDOWEDGE = 0x00000100L;
    public final static int HTTOPLEFT = 13, HTTOP = 12, HTCAPTION = 2, HTTOPRIGHT = 14, HTLEFT = 10, HTNOWHERE = 0,
            HTRIGHT = 11, HTBOTTOMLEFT = 16, HTBOTTOM = 15, HTBOTTOMRIGHT = 17, HTSYSMENU = 3,HTMAXBUTTON = 9;
    public final static long WS_EX_DLGMODALFRAME = 0x00000001L;
    public final static long WS_EX_STATICEDGE = 0x00020000L;
    public final static int SW_MINIMIZE = 6;
    public final static int CS_DROPSHADOW = 0x00020000;
    public final static int WM_NCCALCSIZE = 0x0083;
    public final static int WM_NCHITTEST = 0x0084;
    public final static int WM_NCLBUTTONDOWN = 0x00A1;
    public final static int WM_NCLBUTTONUP = 0x00A2;
    public final static int WM_MOUSEHOVER= 0x02A1;
    public final static int SC_CLOSE = 0xF060;
    public final static int SC_RESTORE = 0xF120;
    private boolean isResizable = true;
    private boolean isVisible = false;
    private int TITLE_BAR_HEIGHT = 27;

    MaterialWindow window;
    HWND hwnd = new HWND();
    BaseTSD.LONG_PTR defWndProc;

    final User32Ex INSTANCEx;
    boolean isInitialized = false;
    private final int normalWindowUFlags = SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_FRAMECHANGED;
    private int normalWindowStyle;
    private RECT normalWindowBounds = new RECT();
    private boolean isFullscreen;
    public BorderlessWindowProc() {
        INSTANCEx = Native.load("user32", User32Ex.class, W32APIOptions.UNICODE_OPTIONS);
    }

    public void init(MaterialWindow window) {
        if(window.isDisplayable() && !isInitialized) {
            this.window = window;
            this.hwnd = getHwnd(window);
            defWndProc = INSTANCEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, this);

            normalWindowStyle = INSTANCEx.GetWindowLong(hwnd, GWL_STYLE);
            normalWindowStyle &= ~(WS_SYSMENU);
            INSTANCEx.SetWindowPos(hwnd, hwnd, 0, 0, 0, 0, normalWindowUFlags);
            setNormalWindowState();
//            Dwmapi.enableMMCSS(true);
            Dwmapi.addShadowEffect(hwnd);
            Dwmapi.useImmersiveDarkMode(hwnd,true);
            isInitialized =true;
        }
    }

    private void setNormalWindowState(){
        INSTANCEx.SetWindowLongPtr(hwnd, GWL_STYLE, Pointer.createConstant(normalWindowStyle));
        isFullscreen = false;
    }

    private void setFullscreenWindowState(){
        if(!isFullscreen) {

            //This boolean will disable hit testing
            isFullscreen = true;
            //change style of window, both styles are important otherwise it will either crash or will not show fullscreen properly
            INSTANCEx.SetWindowLong(hwnd, GWL_STYLE, (WS_VISIBLE | WS_POPUP));
            //Resize window to fullscreen
            DisplayMode displayMode = window.getGraphicsConfiguration().getDevice().getDisplayMode();
            INSTANCEx.SetWindowPos(hwnd, HWND_TOP, 0, 0, displayMode.getWidth(), displayMode.getHeight(), SWP_FRAMECHANGED);
        }
    }


    public void ds() {
        INSTANCEx.ShowWindow(hwnd, SW_MINIMIZE);
    }

    private boolean isMaximized = false;
    private final WINDOWPLACEMENT windowplacement = new WINDOWPLACEMENT();
    public void toggleMaximize(){
        INSTANCEx.GetWindowPlacement(hwnd,windowplacement);
        if (windowplacement.showCmd != SW_MAXIMIZE){
            INSTANCEx.ShowWindow(hwnd, SW_MAXIMIZE);
        }
        else {
            INSTANCEx.ShowWindow(hwnd, SW_RESTORE);
        }

    }

    public boolean isMaximized(){
        return windowplacement.showCmd == SW_MAXIMIZE;
    }
    public void minimizeWindow() {
        INSTANCEx.ShowWindow(hwnd, SW_MINIMIZE);
        Log.info("MINIMIZING WINDOW");
    }

    public void restoreWindow() {
        INSTANCEx.ShowWindow(hwnd, SW_RESTORE);
    }
    public void closeWindow(){
        Log.warn("Destroying window : " + hwnd);
//        defWndProc = INSTANCEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, new BaseTSD.LONG_PTR(0));
//        INSTANCEx.PostMessage(hwnd, WM_CLOSE, new WPARAM(0), new LPARAM(0));
//        defWndProc.
    }

    @Override
    public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
        LRESULT lresult;
        switch (uMsg) {
            case WM_NCCALCSIZE:
                if (wParam.intValue() == 1 && !isFullscreen) {
                    return new LRESULT(0);
                }
                return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
            case WM_NCHITTEST:
                if(isFullscreen)
                    return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
                lresult = this.HitTest(hwnd); // Check for hit

                if (lresult.intValue() == new LRESULT(0).intValue()) {
                    return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
                }
                return lresult;
            case  WM_NCLBUTTONDOWN:
                switch (wParam.intValue()) {
                    case HTMAXBUTTON:
                        return new LRESULT(0);
                    default:
                        return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
                }
            case  WM_NCLBUTTONUP:
                switch (wParam.intValue()) {
                    case HTMAXBUTTON:
                        toggleMaximize();
                        return new LRESULT(0);
                    default:
                        return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
                }
            case WM_DESTROY:
                INSTANCEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, defWndProc);
                return new LRESULT(0);
            case WM_SYSCOMMAND:
                switch (wParam.intValue()) {
                    case SC_MINIMIZE:
                        INSTANCEx.ShowWindow(hwnd, SW_MINIMIZE);
                        return new LRESULT(0);
                    default:
                        return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
                }
            default:
                return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
        }
    }

    public void setGripSize(int size) {
        TITLE_BAR_HEIGHT = size;
    }

    //TODO add snap menu in maximize button
    private POINT ptMouse = new POINT();
    private RECT rcWindow = new RECT();
    private @NotNull LRESULT HitTest(HWND hwnd) {
        int borderOffset = DecorationParameters.getResizeBorderThickness();
        int borderThickness = DecorationParameters.getResizeBorderThickness();

        User32.INSTANCE.GetCursorPos(ptMouse);
        User32.INSTANCE.GetWindowRect(hwnd, rcWindow);
        int captionBtnWidth = DecorationParameters.getTitleBarHeight();//caption buttons are square
        int uRow = 1, uCol = 1;
        boolean fOnResizeBorder = false, fOnFrameDrag, fOnIcon = false,fOnMaximize = false;




        fOnFrameDrag = window.isOnDragArea();
        if (isResizable) {
            //Max button
            if (ptMouse.y >= rcWindow.top && ptMouse.y < rcWindow.top + TITLE_BAR_HEIGHT + borderOffset) {
                //CORRECTION VALUE WAS FOUND THROUGH HIT N TRIAL
//                int maxStateBorderCorrection =20;
//                if(isMaximized)
//                    maxStateBorderCorrection += borderThickness*2;
//                fOnMaximize = ptMouse.x < rcWindow.right - captionBtnWidth - maxStateBorderCorrection
//                        && ptMouse.x > rcWindow.right - captionBtnWidth * 2 - borderOffset - maxStateBorderCorrection
//                        && (ptMouse.y <= rcWindow.top + TITLE_BAR_HEIGHT + borderOffset);
                fOnResizeBorder = (ptMouse.y < (rcWindow.top + borderThickness)); //Top resizing
//                    fOnIcon = (ptMouse.y <= rcWindow.top + TITLE_BAR_HEIGHT)
//                              && (ptMouse.x > rcWindow.left)
//                              && (ptMouse.x < (rcWindow.left + DecorationParameters.getIconWidth() + borderOffset));
                uRow = 0; // Top Resizing or Caption Moving
            } else if (ptMouse.y < rcWindow.bottom && ptMouse.y >= rcWindow.bottom - borderThickness)
                uRow = 2; // Bottom resizing

            //Left and right resizing
            if (ptMouse.x >= rcWindow.left && ptMouse.x < rcWindow.left + borderThickness)
                uCol = 0; // Left resizing
            else if (ptMouse.x < rcWindow.right && ptMouse.x >= rcWindow.right - borderThickness)
                uCol = 2; // Right resizing

            int[][] hitTests = {
                    {HTTOPLEFT, fOnResizeBorder ? HTTOP : fOnIcon ? HTSYSMENU : fOnFrameDrag ? HTCAPTION :  fOnMaximize ? HTMAXBUTTON : HTNOWHERE, HTTOPRIGHT},
                    {HTLEFT, HTNOWHERE, HTRIGHT},
                    {HTBOTTOMLEFT, HTBOTTOM, HTBOTTOMRIGHT}

            };
            return new LRESULT(hitTests[uRow][uCol]);
        } else
            return new LRESULT(HTCAPTION);
    }

    private @NotNull HWND getHwnd(Window jFrame) {

        HWND hwnd = new HWND();
        hwnd.setPointer(Native.getComponentPointer(jFrame));
        return hwnd;
    }


    public void setResizable(boolean resizable) {
        isResizable = resizable;
    }

    public void setVisible(boolean visible){
        if(isVisible != visible) {
            if(visible)
                INSTANCEx.ShowWindow(hwnd, SW_SHOW);
            else
                INSTANCEx.ShowWindow(hwnd, SW_HIDE);
            isVisible = visible;
        }
    }

    public void setFullscreen(boolean fullscreen) {
        if(fullscreen && isInitialized)
            setFullscreenWindowState();
        else
            setNormalWindowState();
    }


    void CreateThumbBarButtons(HWND hwnd)
    {
//        THUMBBUTTON rgtb[1];
//        rgtb[0].iId = IDC_INCREMENT;
//        rgtb[0].hIcon = g_hicoAlert;
//        rgtb[0].dwFlags = THBF_ENABLED;
//        rgtb[0].dwMask = THB_ICON | THB_TOOLTIP | THB_FLAGS;
//        wcscpy_s(rgtb[0].szTip, L"Increment the value");
//        ITaskbarList3Ptr sptb3;
//        sptb3.CreateInstance(CLSID_TaskbarList);
//        sptb3->ThumbBarAddButtons(hwnd, 1, rgtb);
    }
}
