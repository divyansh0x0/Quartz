package material.window.win32procedures;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.W32APIOptions;
import material.window.Dwmapi;
import material.window.User32Ex;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.sun.jna.platform.win32.WinUser.*;
import static com.sun.jna.platform.win32.WinUser.WM_DESTROY;


public class PopupWindowProc implements WinUser.WindowProc {
    private boolean isVisible = false;
    private final static int WM_ERASEBKGND = 0x0014;
    private final static int WM_NCCALCSIZE = 0x0083;

    private WinDef.HWND hwnd = new WinDef.HWND();
    private BaseTSD.LONG_PTR defWndProc;

    final User32Ex INSTANCEx;
    boolean isInitialized = false;
    public PopupWindowProc() {
        INSTANCEx = Native.load("user32", User32Ex.class, W32APIOptions.UNICODE_OPTIONS);
    }
    public void init(@NotNull Window window) {
        if(window.isDisplayable() && !isInitialized) {
            this.hwnd = getHwnd(window);
            defWndProc = INSTANCEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, this);

            int style = INSTANCEx.GetWindowLong(hwnd, GWL_STYLE) | WS_POPUP;
            style &= ~(WS_SYSMENU);

            INSTANCEx.SetWindowLongPtr(hwnd, GWL_STYLE, Pointer.createConstant(style));
            INSTANCEx.SetWindowPos(hwnd, hwnd, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_NOZORDER | SWP_FRAMECHANGED);
            Dwmapi.enableMMCSS(true);
            Dwmapi.addShadowEffect(hwnd);
            isInitialized = true;
        }
    }

    private @NotNull HWND getHwnd(Window jFrame) {

        HWND hwnd = new HWND();
        hwnd.setPointer(Native.getComponentPointer(jFrame));
        return hwnd;
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

    @Override
    public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
        switch (uMsg) {
            case WM_NCCALCSIZE:
            case WM_PAINT:
                if (wParam.intValue() == 1)
                    return new LRESULT(0);
                return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);

            case WM_DESTROY:
                INSTANCEx.SetWindowLongPtr(hwnd, User32Ex.GWLP_WNDPROC, defWndProc);
                return new LRESULT(0);
            default:
                return INSTANCEx.CallWindowProc(defWndProc, hwnd, uMsg, wParam, lParam);
        }
    }

}

