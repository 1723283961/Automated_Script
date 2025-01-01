package cn.kutori;

import cn.kutori.config.OpenCVConfig;
import cn.kutori.config.SelectRunFileConfig;
import com.sun.jna.platform.win32.WinDef;
import org.opencv.core.Mat;

import java.util.Map;

import static cn.kutori.enumPojo.coordinate.COORDINATEX;
import static cn.kutori.enumPojo.coordinate.COORDINATEY;
import static cn.kutori.utils.ScreenCaptureUtils.getScreenShot;
import static java.lang.Thread.sleep;

public class application {

    public static void main(String[] args) throws Exception {
        // 加载OpenCV库1386, 21
        OpenCVConfig openCVConfig = new OpenCVConfig();
        openCVConfig.init();
        //图片路径
        String templateImagePath = "again.png";

        String templateImagePath1 = "yes2.png";

        String templateImagePath2 = "skip4.png";

        String templateImagePath3 = "have4.png";

        // 获取窗口句柄
        SelectRunFileConfig selectRunFileConfig = new SelectRunFileConfig();
        WinDef.HWND hwnd= selectRunFileConfig.selectRunFile();

        while (true){
            sleep(500);
            Mat screen = getScreenShot();
            if(openCVConfig.getXY(screen,templateImagePath) != null){
                Map<String,Integer> map =openCVConfig.getXY(screen,templateImagePath);
                selectRunFileConfig.sendMouseClick(hwnd,map.get(COORDINATEX.value),map.get(COORDINATEY.value));
                sleep(1000);

                Mat screen1 = getScreenShot();
                if(openCVConfig.getXY(screen1,templateImagePath1) != null){
                    Map<String,Integer> map1 =openCVConfig.getXY(screen1,templateImagePath1);
                    selectRunFileConfig.sendMouseClick(hwnd,map1.get(COORDINATEX.value),map1.get(COORDINATEY.value));
                }

            }else if(openCVConfig.getXY(screen,templateImagePath1) != null){
                Map<String,Integer> map =openCVConfig.getXY(screen,templateImagePath1);
                selectRunFileConfig.sendMouseClick(hwnd,map.get(COORDINATEX.value),map.get(COORDINATEY.value));
            }else if(openCVConfig.getXY(screen,templateImagePath2) != null){
                Map<String,Integer> map =openCVConfig.getXY(screen,templateImagePath2);
                selectRunFileConfig.sendMouseClick(hwnd,map.get(COORDINATEX.value),map.get(COORDINATEY.value));
            }
            sleep(1000);
            Mat screen3 = getScreenShot();
            System.out.println(openCVConfig.getSum(screen3,templateImagePath3));
            if(openCVConfig.getSum(screen3,templateImagePath3) >= 4){
                break;
            }
        }
    }

}
