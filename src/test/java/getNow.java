
import java.awt.*;
//(535.0, 403.0)
public class getNow {
    public static void main(String[] args) throws AWTException {
        Robot robot = new Robot();
        Point point = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(535,403);
        System.out.println("当前鼠标位置：" + point.x + ", " + point.y);
    }
}
