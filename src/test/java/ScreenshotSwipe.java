import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ScreenshotSwipe {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 加载 OpenCV 本地库
    }

    /**
     * 模板匹配，查找指定元素位置
     *
     * @param screenshotPath 截屏路径
     * @param templatePath   模板路径
     * @return 是否找到模板
     */
    public static boolean findElement(String screenshotPath, String templatePath) {
        // 读取截屏和模板图片
        Mat screenshot = Imgcodecs.imread(screenshotPath);
        Mat template = Imgcodecs.imread(templatePath);

        if (screenshot.empty() || template.empty()) {
            System.out.println("无法读取截图或模板图片！");
            return false;
        }

        // 创建结果矩阵
        int resultCols = screenshot.cols() - template.cols() + 1;
        int resultRows = screenshot.rows() - template.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

        // 模板匹配
        Imgproc.matchTemplate(screenshot, template, result, Imgproc.TM_CCOEFF_NORMED);

        // 获取匹配结果最大值和最小值
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        double matchThreshold = 0.8; // 匹配阈值（可调整）

        if (mmr.maxVal >= matchThreshold) {
            System.out.println("找到目标元素，位置：(" + mmr.maxLoc.x + ", " + mmr.maxLoc.y + ")");
            return true;
        } else {
            System.out.println("未找到目标元素！");
            return false;
        }
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
        User32.INSTANCE.PostMessage(hwnd, WM_LBUTTONDOWN, new WinDef.WPARAM(0), new WinDef.LPARAM((startY << 16) | startX));

        // 模拟鼠标拖动
        int stepCount = 10; // 滑动步数
        for (int i = 1; i <= stepCount; i++) {
            int currentX = startX + (endX - startX) * i / stepCount;
            int currentY = startY + (endY - startY) * i / stepCount;
            User32.INSTANCE.PostMessage(hwnd, WM_MOUSEMOVE, new WinDef.WPARAM(0), new WinDef.LPARAM((currentY << 16) | currentX));
            try {
                Thread.sleep(50); // 每步延迟 50ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 模拟鼠标抬起
        User32.INSTANCE.PostMessage(hwnd, WM_LBUTTONUP, new WinDef.WPARAM(0), new WinDef.LPARAM((endY << 16) | endX));
        System.out.println("页面向左滑动完成！");
    }

    public static void main(String[] args) {
        // 假设你有多个模板图片需要轮流匹配
        List<String> templates = new ArrayList<>();
        templates.add("template1.png");
        templates.add("template2.png");
        templates.add("template3.png");

        // 截图路径（假设已截取当前窗口的截图）
        String screenshotPath = "screenshot.png";

        // 获取窗口句柄（替换为目标应用窗口标题）
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "目标窗口标题");

        if (hwnd == null) {
            System.out.println("未找到目标窗口！");
            return;
        }

        // 遍历模板图片并查找
        for (String templatePath : templates) {
            boolean found = findElement(screenshotPath, templatePath);
            if (found) {
                System.out.println("找到匹配模板：" + templatePath);
                break; // 找到则退出循环
            } else {
                System.out.println("切换下一张模板图片！");
                swipeLeft(hwnd, 800, 500, 300, 500); // 模拟从右向左滑动
            }
        }
    }
}
