//package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Login<onButtonOk> {

    private JFrame jFrame = new JFrame("连接");
    private Container c = jFrame.getContentPane();
    private JLabel a1 = new JLabel("主    机：");
    private JTextField host = new JTextField();
    private JLabel a2 = new JLabel("端    口：");
    private JTextField port = new JTextField();
    private JLabel a3 = new JLabel("数据库：");
    private JTextField database = new JTextField();
    private JLabel a4 = new JLabel("用户名：");
    private JTextField username = new JTextField();
    private JLabel a5 = new JLabel("密    码：");
    private JPasswordField password = new JPasswordField();
    private JButton okbtn = new JButton("确定");
    private JButton cancelbtn = new JButton("取消");



    // 对外参数
    public String hostText = "";
    public String portText = "";
    public String databaseText = "";
    public String usernameText = "";
    public String passwordText = "";


    public Boolean end = false;

    public Login() {
        //设置窗体的位置及大小
        jFrame.setBounds(600, 200, 340, 540);
        //设置一层相当于桌布的东西
        c.setLayout(new BorderLayout());//布局管理器
        //设置按下右上角X号后关闭
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //初始化--往窗体里放其他控件
        init();
        //设置窗体可见
        jFrame.setVisible(true);
    }
    public void init() {
        /*标题部分--North*/
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(new JLabel("连接数据库"));
        c.add(titlePanel, "North");

        /*输入部分--Center*/
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(null);
        a1.setBounds(50, 20, 60, 20);
        a2.setBounds(50, 60, 60, 20);
        a3.setBounds(50, 100, 60, 20);
        a4.setBounds(50, 140, 60, 20);
        a5.setBounds(50, 180, 60, 20);
        fieldPanel.add(a1);
        fieldPanel.add(a2);
        fieldPanel.add(a3);
        fieldPanel.add(a4);
        fieldPanel.add(a5);
        host.setBounds(110, 20, 200, 20);
        port.setBounds(110, 60, 80, 20);
        database.setBounds(110, 100, 200, 20);
        username.setBounds(110, 140, 120, 20);
        password.setBounds(110, 180, 120, 20);
        fieldPanel.add(host);
        fieldPanel.add(port);
        fieldPanel.add(database);
        fieldPanel.add(username);
        fieldPanel.add(password);
        c.add(fieldPanel, "Center");

        /*按钮部分--South*/
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okbtn);
        buttonPanel.add(cancelbtn);
        c.add(buttonPanel, "South");

        okbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostText = host.getText();
                portText = port.getText();
                databaseText = database.getText();
                usernameText = username.getText();
                passwordText = password.getText();
                jFrame.dispose();
                // TODO
//                new Index(hostText,portText,databaseText,usernameText,passwordText); //没有办法在这个事件上wait

                end = true;
            }
        });

        cancelbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

}