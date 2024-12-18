package cn.kutori.config;

import com.sun.jna.platform.win32.WinDef;
import org.opencv.core.Core;
import org.opencv.core.Mat;
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
        Mat mainImage = Imgcodecs.imread(main);
        Mat subImages = Imgcodecs.imread(sub);
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
    public  int[] getImageSize(String imagePath) throws Exception {
        // 读取图片
        Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_UNCHANGED);
        if (image.empty()) {
            throw new Exception("无法加载图片：" + imagePath);
        }
        // 获取宽高
        int width = image.cols();  // 图片的宽度
        int height = image.rows(); // 图片的高度
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
    public Map<String,Integer> multipleImages(WinDef.HWND hwnd, String [] main, String sub) throws Exception {
        for(String m : main){
            if(getXOrY(m,sub) != null){
                return getXOrY(m,sub);
                break;
            }else {
                swipeLeft(hwnd, 800, 500, 300, 500); // 模拟从右向左滑动
            }
        }

    }
}
