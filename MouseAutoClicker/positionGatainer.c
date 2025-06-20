#include<stdio.h>
#include<Windows.h>

int main() {
    printf("鼠标坐标获取程序已启动\n");
    printf("点击鼠标左键获取坐标（按ESC键退出）\n\n");

    // 存储当前和上一次的鼠标状态
    SHORT prevLeftState = 0;
    POINT cursorPos;

    while (1) {
        // 检测ESC键退出
        if (GetAsyncKeyState(VK_ESCAPE)) {
            printf("程序已退出\n");
            break;
        }

        // 获取当前鼠标左键状态
        SHORT currentLeftState = GetAsyncKeyState(VK_LBUTTON);

            // 检查鼠标左键是否刚刚按下（从弹起变为按下）
            if ((currentLeftState & 0x8000) && !(prevLeftState & 0x8000)) {
                // 获取鼠标位置
                GetCursorPos(&cursorPos);

                // 打印坐标信息
                printf("鼠标点击位置: X=%d, Y=%d\n", cursorPos.x, cursorPos.y);

                // 获取窗口句柄（可选）
                HWND window = WindowFromPoint(cursorPos);
                if (window) {
                    char title[256];
                    GetWindowTextA(window, title, sizeof(title));
                    printf("所在窗口: %s\n", title);
                }
            }

        // 保存当前状态用于下一次比较
        prevLeftState = currentLeftState;

        // 减少CPU占用
        Sleep(60);
    }

    return 0;
}