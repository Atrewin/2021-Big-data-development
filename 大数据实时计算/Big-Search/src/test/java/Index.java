//package main.java;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//import main.scala.*;
//import TestScala;

/**
 * called another JFrame close this JFrame write by Jimmy.li time:2016/4/6 22:55
 */

public class Index {
    //    前后端合并的结构，表明这个对象就是现实中的一个界面对象，包含了相关的GUI和后端是相应逻辑
    JMenuBar menuBar; //菜单条
    JMenu menu;//菜单

    public Index(String hostText, String portText, String databaseText, String usernameText, String passwordText) {
        // 普通按钮控件
        final JFrame jf = new JFrame("简易数据查询器");
        jf.setBounds(300, 150, 800, 400);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.setVisible(true);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        jf.setContentPane(contentPane);

        SparkHolder con = new SparkHolder(hostText,portText,databaseText,usernameText,passwordText);


        String[] tables = con.getTables();
        menuBar = new JMenuBar();//创建一个菜单条
        menu = new JMenu("数据库");//创立一个菜单选项
        JMenu subMenu = new JMenu(databaseText);
        menu.add(subMenu);//把subMenu菜单做为menu的一个菜单项

        JLabel queryLabel = new JLabel("请输入查询语句:");
        queryLabel.setBounds(10,10,100,25);
        contentPane.add(queryLabel);

        JTextArea input = new JTextArea(10,20);
        input.setBounds(120, 10, 650, 250);
        contentPane.add(input);

        JButton ok = new JButton("查询");
        ok.setBounds(650, 280, 100, 40);
        contentPane.add(ok);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query=input.getText();
                String[][] res=con.getRes(query);
                // 展示的结果
                new Table(res);
            }
        });

        for (String t:tables){
            JMenu subsubMenu = new JMenu(t);
            String[] columns = con.getColumn(t);
            for (String c:columns){
                subsubMenu.add(new JMenuItem(c));
            }
            subMenu.add(subsubMenu);
        }
        menuBar.add(menu);
        jf.setJMenuBar(menuBar);
        contentPane.revalidate();
    }
}