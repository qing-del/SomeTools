import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Robot;
import java.awt.MouseInfo;
import java.awt.Point;

public class MouseClicker extends JFrame {
    private int x = -1, y = -1;
    private int clickCount = 0;
    private boolean isRunning = false;
    private JLabel positionLabel = new JLabel("未选择位置");
    private JTextField countField = new JTextField("1", 10);
    private JTextField intervalField = new JTextField("1000", 10);
    private JLabel statusLabel = new JLabel("准备就绪");
    private JButton startButton = new JButton("开始 (T)");
    private JButton stopButton = new JButton("停止 (ESC)");
    private JButton positionButton = new JButton("选择位置");
    private JButton calcButton = new JButton("时间计算器");
    private Robot robot;

    public MouseClicker() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(this, "无法创建Robot对象: " + e.getMessage());
            System.exit(1);
        }

        setTitle("鼠标自动点击器");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 位置显示区域
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(positionLabel, gbc);

        // 选择位置按钮
        gbc.gridy = 1; gbc.gridwidth = 1;
        positionButton.addActionListener(e -> selectPosition());
        add(positionButton, gbc);

        // 时间计算器按钮
        gbc.gridx = 1;
        calcButton.addActionListener(e -> showTimeCalculator());
        add(calcButton, gbc);

        // 循环次数设置
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("循环次数:"), gbc);
        gbc.gridx = 1;
        add(countField, gbc);

        // 间隔时间设置
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("间隔时间(毫秒):"), gbc);
        gbc.gridx = 1;
        add(intervalField, gbc);

        // 状态标签
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel, gbc);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton.addActionListener(e -> startClicking());
        stopButton.addActionListener(e -> stopClicking());
        stopButton.setEnabled(false);
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        gbc.gridy = 5;
        add(buttonPanel, gbc);

        // 键盘快捷键
        setupKeyBindings();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectPosition() {
        JDialog positionDialog = new JDialog(this, "选择点击位置", true);
        positionDialog.setSize(300, 150);
        positionDialog.setLayout(new BorderLayout());

        JLabel instruction = new JLabel(
                "<html><center>请将鼠标移动到目标位置<br>然后按空格键确认</center></html>",
                SwingConstants.CENTER
        );
        positionDialog.add(instruction, BorderLayout.CENTER);

        positionDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                positionLabel.setText("位置: (" + mouseLoc.x + ", " + mouseLoc.y + ")");
            }
        });

        // 添加键盘监听
        positionDialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("SPACE"), "confirm");
        positionDialog.getRootPane().getActionMap().put("confirm", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                x = mouseLoc.x;
                y = mouseLoc.y;
                positionLabel.setText("位置: (" + x + ", " + y + ")");
                positionDialog.dispose();
            }
        });

        positionDialog.setLocationRelativeTo(this);
        positionDialog.setVisible(true);
    }

    private void startClicking() {
        if (x == -1 || y == -1) {
            JOptionPane.showMessageDialog(this, "请先选择点击位置");
            return;
        }

        try {
            int count = Integer.parseInt(countField.getText());
            long interval = Long.parseLong(intervalField.getText());

            if (count < 1 || interval < 1) {
                JOptionPane.showMessageDialog(this, "请输入有效的数值");
                return;
            }

            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            positionButton.setEnabled(false);

            new Thread(() -> {
                for (int i = 0; i < count && isRunning; i++) {
                    clickCount = i + 1;
                    SwingUtilities.invokeLater(() ->
                            statusLabel.setText("已完成第 " + clickCount + " 次点击")
                    );

                    robot.mouseMove(x, y);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.delay(50);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                    // 等待间隔时间，但检查是否被中断
                    long waitTime = interval;
                    while (waitTime > 0 && isRunning) {
                        long sleepTime = Math.min(waitTime, 100);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException ex) {
                            break;
                        }
                        waitTime -= sleepTime;
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    if (isRunning) {
                        statusLabel.setText("已完成所有点击");
                    } else {
                        statusLabel.setText("已停止点击");
                    }
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    positionButton.setEnabled(true);
                    isRunning = false;
                });
            }).start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字");
        }
    }

    private void stopClicking() {
        isRunning = false;
    }

    private void showTimeCalculator() {
        JDialog calculator = new JDialog(this, "时间计算器", true);
        calculator.setSize(300, 250);
        calculator.setLayout(new GridLayout(6, 2, 5, 5));

        JTextField msField = new JTextField("0");
        JTextField secField = new JTextField("0");
        JTextField minField = new JTextField("0");
        JTextField hourField = new JTextField("0");

        calculator.add(new JLabel("毫秒:"));
        calculator.add(msField);
        calculator.add(new JLabel("秒:"));
        calculator.add(secField);
        calculator.add(new JLabel("分钟:"));
        calculator.add(minField);
        calculator.add(new JLabel("小时:"));
        calculator.add(hourField);

        JButton calculateButton = new JButton("计算");
        JButton applyButton = new JButton("应用到间隔时间");

        JLabel resultLabel = new JLabel("总毫秒数: 0");

        calculateButton.addActionListener(e -> {
            try {
                long ms = Long.parseLong(msField.getText());
                long sec = Long.parseLong(secField.getText());
                long min = Long.parseLong(minField.getText());
                long hour = Long.parseLong(hourField.getText());

                long total = ms + (sec * 1000) + (min * 60000) + (hour * 3600000);
                resultLabel.setText("总毫秒数: " + total);
            } catch (NumberFormatException ex) {
                resultLabel.setText("请输入有效的数字");
            }
        });

        applyButton.addActionListener(e -> {
            String text = resultLabel.getText().replace("总毫秒数: ", "");
            if (!text.startsWith("请输入")) {
                intervalField.setText(text);
                calculator.dispose();
            }
        });

        calculator.add(calculateButton);
        calculator.add(applyButton);
        calculator.add(resultLabel);
        calculator.add(new JLabel()); // 占位符

        calculator.setLocationRelativeTo(this);
        calculator.setVisible(true);
    }

    private void setupKeyBindings() {
        // T键开始
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("T"), "start");
        getRootPane().getActionMap().put("start", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startButton.isEnabled()) {
                    startClicking();
                }
            }
        });

        // ESC键停止
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "stop");
        getRootPane().getActionMap().put("stop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopButton.isEnabled()) {
                    stopClicking();
                }
            }
        });

        // 空格键在位置选择时确认
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("SPACE"), "none"); // 防止冲突
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MouseClicker());
    }
}