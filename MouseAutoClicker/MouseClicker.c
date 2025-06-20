#include <stdio.h>
#include <Windows.h>
const long TIMESET = 1000;

//传入 x y 表示点击屏幕的坐标  time为等待的时间（点击的间隔）
void simulateClick(int x, int y, int waiting) {
    // 设置鼠标位置
    SetCursorPos(x, y);

    // 模拟鼠标按下
    mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
    Sleep(10);  // 短暂延迟

    // 模拟鼠标释放
    mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);

    //开始等待下一次操作
    Sleep(waiting);
}

//第一个参数时秒， 第二个是分钟， 第三个是小时
long timeCalculation(int second, int minute, int hour) {
    return TIMESET * second + TIMESET * 60 * minute + TIMESET * 3600 * hour;
}

int main() {
    //t 表示要循环的次数
    int t = 1000;
    //waitTime 表示等待的时间
    long waitTime = timeCalculation(10, 5, 0);

    //开始重复执行点击操作
    int k = 0;
    while (t--) {
        printf("已完成第%d次点击", ++k);
        simulateClick(1197, 987, waitTime);
    }
    return 0;
}