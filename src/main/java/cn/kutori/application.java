package cn.kutori;

import cn.kutori.common.ResolvingPowerCommon;
import cn.kutori.config.OpenCVConfig;
import cn.kutori.config.SelectRunFileConfig;
import com.sun.jna.platform.win32.WinDef;
import java.util.Map;

import static cn.kutori.enumPojo.coordinate.COORDINATEX;
import static cn.kutori.enumPojo.coordinate.COORDINATEY;

public class application {

    public static void main(String[] args) throws Exception {
        // 加载OpenCV库
        OpenCVConfig openCVConfig = new OpenCVConfig();
        openCVConfig.init();

        //图片路径
        String fullImagePath = "E:\\selfUse\\test1.png";
        String templateImagePath = "E:\\selfUse\\test2.png";

        // 获取窗口句柄
        SelectRunFileConfig selectRunFileConfig = new SelectRunFileConfig();
        WinDef.HWND hwnd= selectRunFileConfig.selectRunFile();

        // 延迟等待窗口置顶生效
        Thread.sleep(1000);

        // 获取窗口坐标
        Map<String,Integer> map =openCVConfig.getXOrY(fullImagePath,templateImagePath);

        ResolvingPowerCommon resolvingPowerCommon = new ResolvingPowerCommon();
        //获取图片大小
        int [] ans = openCVConfig.getImageSize(fullImagePath);
        int [] xy = resolvingPowerCommon.clickMatch(hwnd,map.get(COORDINATEX.value),map.get(COORDINATEY.value),ans[0],ans[1]);
        selectRunFileConfig.sendMouseClick(hwnd,xy[0],xy[1]);
    }

}
