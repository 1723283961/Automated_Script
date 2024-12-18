package cn.kutori.config;

import cn.kutori.utils.FindWindowByProcess;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SelectRunFileConfig {

    /**
     * 选择运行文件
     * @return 窗口句柄
     * @throws Exception
     */
    public WinDef.HWND selectRunFile() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = OpenCVConfig.class.getClassLoader().getResourceAsStream("config.properties")){
            if (input == null) {
                throw new Exception("Sorry, unable to find config.properties");
            }
            properties.load(input);
            // 替换为窗口引用的标题
            String windowApplication = properties.getProperty("config.SelectRunFileConfig");
            System.out.println("windowApplication value: " + windowApplication);
            // 获取窗口句柄
            WinDef.HWND hwnd = FindWindowByProcess.getWindowByProcessName(windowApplication);
            if (hwnd == null) {
                throw new Exception("未找到窗口：" + windowApplication);
            }
            return hwnd;
        } catch (IOException ex) {
            throw new Exception(ex);
        }
    }

    /**
     * 模拟鼠标点击
     * @param hwnd 窗口句柄
     * @param x    x坐标
     * @param y    y坐标
     */
    public void sendMouseClick(WinDef.HWND hwnd, int x, int y) {
        final int WM_LBUTTONDOWN = 0x0201; // 鼠标左键按下
        final int WM_LBUTTONUP = 0x0202;   // 鼠标左键抬起
        int lParamValue = (y << 16) | x;
        WinDef.LPARAM lParam = new WinDef.LPARAM(lParamValue);
        User32.INSTANCE.PostMessage(hwnd, WM_LBUTTONDOWN, new WinDef.WPARAM(0), lParam);
        User32.INSTANCE.PostMessage(hwnd, WM_LBUTTONUP, new WinDef.WPARAM(0), lParam);
        System.out.println("点击："+ x + " " + y);
    }

    /**
     * 模拟页面向左滑动
     *
     * @param hwnd 窗口句柄
     * @param startX 起始 X 坐标
     * @param startY 起始 Y 坐标
     * @param endX   结束 X 坐标
     * @param endY   结束 Y 坐标
     */
    public static void swipeLeft(WinDef.HWND hwnd, int startX, int startY, int endX, int endY) {
        final int WM_LBUTTONDOWN = 0x0201; // 鼠标左键按下
        final int WM_MOUSEMOVE = 0x0200;   // 鼠标移动
        final int WM_LBUTTONUP = 0x0202;   // 鼠标左键抬起

        // 模拟鼠标按下
        User32.INSTANCE.PostMessage(hwnd, WM_LBUTTONDOWN, new WinDef.WPARAM(0), new WinDef.LPARAM(((long) startY << 16) | startX));

        // 模拟鼠标拖动
        int stepCount = 10; // 滑动步数
        for (int i = 1; i <= stepCount; i++) {
            int currentX = startX + (endX - startX) * i / stepCount;
            int currentY = startY + (endY - startY) * i / stepCount;
            User32.INSTANCE.PostMessage(hwnd, WM_MOUSEMOVE, new WinDef.WPARAM(0), new WinDef.LPARAM(((long) currentY << 16) | currentX));
            try {
                Thread.sleep(50); // 每步延迟 50ms
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 模拟鼠标抬起
        User32.INSTANCE.PostMessage(hwnd, WM_LBUTTONUP, new WinDef.WPARAM(0), new WinDef.LPARAM(((long) endY << 16) | endX));
        System.out.println("页面向左滑动完成！");
    }
}
