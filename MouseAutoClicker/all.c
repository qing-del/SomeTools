#include <stdio.h>
#include <Windows.h>
const long TIMESET = 1000;

//获取鼠标点击的位置
int x, y;
void ChoosePosition()
{
    // 存储当前和上一次的鼠标状态
    SHORT prevLeftState = 0;
    POINT cursorPos;

    while(1)
    {
    	// 获取当前鼠标左键状态
    	SHORT currentLeftState = GetAsyncKeyState(VK_LBUTTON);
        // 检查鼠标左键是否刚刚按下（从弹起变为按下）
        if ((currentLeftState & 0x8000) && !(prevLeftState & 0x8000)) {
            // 获取鼠标位置
            GetCursorPos(&cursorPos);

            // 打印坐标信息
            printf("鼠标点击位置: X=%d, Y=%d\n", cursorPos.x, cursorPos.y);
            x = cursorPos.x, y = cursorPos.y;

            // 获取窗口句柄（可选）
            HWND window = WindowFromPoint(cursorPos);
            if (window) {
                char title[256];
                GetWindowTextA(window, title, sizeof(title));
                printf("所在窗口: %s\n", title);
            }
            
            int comfirm;
            printf("确定是这个位置吗？(1 确定， 0 取消)\n");
            int confirm;
            scanf("%d", &comfirm);
            if(comfirm) break;
        }
        
        // 保存当前状态用于下一次比较
    	prevLeftState = currentLeftState;

        Sleep(60);
    }

    
}

//传入 x y 表示点击屏幕的坐标  time为等待的时间（点击的间隔）
void simulateClick(int waiting) {
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
    printf("请点击你要鼠标循环点击的位置\n");
    ChoosePosition();



    //t 表示要循环的次数
    printf("请输入你要循环的次数：\n");
    int t; scanf("%d", &t);

    int second, minute, hour;
    printf("请输入你每次间隔的小时：\n");
    scanf("%d", &hour);

    printf("请输入你每次间隔的分钟：\n");
    scanf("%d", &minute);

    printf("请输入你每次间隔的秒数：\n");
    scanf("%d", &second);
    //waitTime 表示等待的时间
    long waitTime = timeCalculation(second, minute, hour);

    //开始重复执行点击操作
    printf("你可以按下ESC来退出程序\n");
    int k = 0;
    while (t--) {
        // 检测ESC键退出
        if (GetAsyncKeyState(VK_ESCAPE)) {
            printf("程序已退出\n");
            break;
        }

        printf("已完成第%d次点击\n", ++k);
        simulateClick(waitTime);
    }
    return 0;
}