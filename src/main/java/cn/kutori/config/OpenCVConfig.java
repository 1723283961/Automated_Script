package cn.kutori.config;

import com.sun.jna.platform.win32.WinDef;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static cn.kutori.config.SelectRunFileConfig.swipeLeft;
import static cn.kutori.enumPojo.coordinate.COORDINATEX;
import static cn.kutori.enumPojo.coordinate.COORDINATEY;


public class OpenCVConfig {

    private String ImagePath;

    /**
     * 加载OpenCV库
     */
    public void init() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = OpenCVConfig.class.getClassLoader().getResourceAsStream("config.properties")){
                if (input == null) {
                    throw new Exception("Sorry, unable to find config.properties");
                }
                properties.load(input);
                String myConfig = properties.getProperty("config.opencv");
                System.out.println("Config value: " + myConfig);
                System.load(myConfig);
                ImagePath = properties.getProperty("config.ImagePath");
        } catch (IOException ex) {
            throw new Exception(ex);
        }
    }

    /**
     * 获取路径（相对）xy坐标
     * @param main 主图片
     * @param sub 子图片
     * @return map
     */
    public Map<String,Integer> getXOrY(String main, String sub) throws Exception {
        // 加载截图和模板图像
        Mat mainImage = Imgcodecs.imread(ImagePath + main);
        Mat subImages = Imgcodecs.imread(ImagePath + sub);
        if (mainImage.empty() || subImages.empty()) {
            throw new Exception("无法加载图片，请检查文件路径!");
        }
        // 模板匹配
        Mat result = new Mat();
        Imgproc.matchTemplate(mainImage, subImages, result, Imgproc.TM_CCOEFF_NORMED);
        // 获取匹配结果最大值和最小值
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        double matchThreshold = 0.8; // 匹配阈值（可调整）
        if (mmr.maxVal >= matchThreshold) {
            // 获取匹配位置
            System.out.println("找到目标元素，位置：(" + mmr.maxLoc.x + ", " + mmr.maxLoc.y + ")");
            return Map.of(COORDINATEX.value, (int)  mmr.maxLoc.x , COORDINATEY.value,  (int)mmr.maxLoc.y);
        }
        return null;
    }
    /**
     * 获取图片的宽度和高度
     * @param imagePath 图片路径
     * @return 图片的宽度和高度
     */
    public  int[] getImageSize(String imagePath) throws Exception {        // 读取图片
        Mat image = Imgcodecs.imread(ImagePath + imagePath, Imgcodecs.IMREAD_UNCHANGED);
        if (image.empty()) {
            throw new Exception("无法加载图片：" + imagePath);
        }
        // 获取宽高
        int width = image.cols();  // 图片的宽度
        int height = image.rows(); // 图片的高度
        return new int[]{width, height};
    }

    /**
     * 获取图片的宽度和高度
     * @param imagePath 图片路径
     * @return 图片的宽度和高度
     */
    public  int[] getMatSize(Mat imagePath) throws Exception {        // 读取图片
        if (imagePath.empty()) {
            throw new Exception("无法加载图片：" + imagePath);
        }
        // 获取宽高
        int width = imagePath.cols();  // 图片的宽度
        int height = imagePath.rows(); // 图片的高度
        return new int[]{width, height};
    }

    /**
     * 如果是从多重匹配
     * @param hwnd 句柄
     * @param main 主界面
     * @param sub 组件
     * @return 坐标
     * @throws Exception 抛出
     */
    public Map<String, Integer> multipleImages(WinDef.HWND hwnd, String[] main, String sub) throws Exception {
        for (String m : main) {
            Map<String, Integer> result = getXOrY(m, sub); // 调用一次 getXOrY
            if (result != null) {
                return result; // 找到结果，立即返回
            } else {
                swipeLeft(hwnd, 800, 500, 300, 500); // 模拟从右向左滑动
            }
        }
        throw new Exception("未找到符合条件的图片");
    }

    /**
     * 获取路径（相对）xy坐标
     * @param sub 子图片
     * @return map
     */
    public Map<String,Integer> getXY(Mat mat,String sub) throws Exception {
        // 加载截图和模板图像
        Mat subImages = Imgcodecs.imread(ImagePath + sub);
        if ( subImages.empty()) {
            throw new Exception("无法加载图片，请检查文件路径!");
        }
        // 模板匹配
        Mat result = new Mat();
        Imgproc.matchTemplate(mat, subImages, result, Imgproc.TM_CCOEFF_NORMED);
        // 获取匹配结果最大值和最小值
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        double matchThreshold = 0.7; // 匹配阈值（可调整）
        if (mmr.maxVal >= matchThreshold) {
            // 获取匹配位置
            return Map.of(COORDINATEX.value, (int)  mmr.maxLoc.x , COORDINATEY.value,  (int)mmr.maxLoc.y);
        }
        return null;
    }

    /**
     * 获取路径（相对）xy坐标
     * @param sub 子图片
     * @return map
     */
    public Integer getSum(Mat mat, String sub) throws Exception {
        int sum = 0;
        Mat subImages = Imgcodecs.imread(ImagePath + sub);
        if (subImages.empty()) {
            throw new Exception("无法加载图片，请检查文件路径!");
        }

        Mat result = new Mat();
        Imgproc.matchTemplate(mat, subImages, result, Imgproc.TM_CCOEFF_NORMED);

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        double matchThreshold = 0.8;

        while (mmr.maxVal >= matchThreshold) {
            // 获取当前最匹配的位置
            Point matchLoc = mmr.maxLoc;
            // 增加匹配数量
            sum++;
            // 设定一个稍大的矩形区域以避免重复匹配
            int padding = 10;  // 控制区域大小以避免重复
            Rect rect = new Rect((int)(matchLoc.x - padding), (int)(matchLoc.y - padding),
                    subImages.cols() + 2 * padding, subImages.rows() + 2 * padding);
            rect.x = Math.max(0, rect.x); // 防止越界
            rect.y = Math.max(0, rect.y);
            rect.width = Math.min(result.cols() - rect.x, rect.width);
            rect.height = Math.min(result.rows() - rect.y, rect.height);
            // 将该区域覆盖为0，防止重复匹配
            Mat roi = result.submat(rect);
            roi.setTo(new Scalar(0));

            // 重新计算最小最大值
            mmr = Core.minMaxLoc(result);
        }
        return sum;
    }


}
