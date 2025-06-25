package net.gamma.qualityoflife.util;

public class WidgetUtils {
    public static boolean isNearLeftEdge(double mouseX, int widgetX, int resizeMargin)
    {
        return mouseX >= widgetX && mouseX <= widgetX + resizeMargin;
    }
    public static boolean isNearRightEdge(double mouseX, int widgetX, int widgetWidth, int resizeMargin)
    {
        return mouseX <= widgetX + widgetWidth && mouseX >= widgetX + widgetWidth - resizeMargin;
    }
    public static boolean isNearTopEdge(double mouseY, int widgetY, int resizeMargin)
    {
        return mouseY >= widgetY && mouseY <= widgetY + resizeMargin;
    }
    public static boolean isNearBottomEdge(double mouseY, int widgetY, int widgetHeight, int resizeMargin)
    {
        return mouseY <= widgetY + widgetHeight && mouseY >= widgetY + widgetHeight - resizeMargin;
    }
    public static boolean isNearLeftTopEdge(double mouseX, int widgetX, double mouseY, int widgetY,int resizeMargin)
    {
        return isNearLeftEdge(mouseX, widgetX, resizeMargin) && isNearTopEdge(mouseY, widgetY, resizeMargin);
    }
    public static boolean isNearLeftBottomEdge(double mouseX, int widgetX, double mouseY, int widgetY, int widgetHeight, int resizeMargin)
    {
        return isNearLeftEdge(mouseX, widgetX, resizeMargin) && isNearBottomEdge(mouseY, widgetY, widgetHeight, resizeMargin);
    }
    public static boolean isNearRightTopEdge(double mouseX, int widgetX, int widgetWidth, double mouseY, int widgetY, int resizeMargin)
    {
        return isNearRightEdge(mouseX, widgetX, widgetWidth, resizeMargin) && isNearTopEdge(mouseY, widgetY, resizeMargin);
    }
    public static boolean isNearRightBottomEdge(double mouseX, int widgetX, int widgetWidth, double mouseY, int widgetY, int widgetHeight, int resizeMargin)
    {
        return isNearRightEdge(mouseX, widgetX, widgetWidth, resizeMargin) && isNearBottomEdge(mouseY, widgetY, widgetHeight, resizeMargin);
    }

    public static double getNormalized(int input, int directionTotal)
    {
        return (double)input / directionTotal;
    }
    public static int getReal(double normalized, int directionTotal)
    {
        return (int)(normalized *  directionTotal);
    }
}
