import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
//import javafx.scene.control.T
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Scene;

import javax.swing.*;

import static java.sql.DriverManager.getConnection;

public class Index2 extends Application {

    private static Function sparkhandler = new Function();
    private static String HostText = "";
    private static String PortText = "";
    private static String DatabaseText = "";
    private static String UsernameText = "";
    private static String PasswordText = "";

    public static void main(String[] args) throws SQLException {
        Login();

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        //设置树状结构
        TreeItem<String> rootItem = new TreeItem("Tables");
        rootItem.setExpanded(true);


        //设置文本框
        //设置输入文本框
        VBox vb = new VBox();
        Label label = new Label("  输入SQL语句");
        label.setStyle("-fx-font-size:18;");
        label.setStyle("-fx-padding: 12;");
        TextArea input = new TextArea();
        input.setEditable(true);
        input.setStyle("-fx-font-size:18;");


        //设置输出文本框
        Label label1 = new Label("执行结果");
        label1.setStyle("-fx-font-size:18;");
        label1.setStyle("-fx-padding: 12;");

        TextArea output = new TextArea();
        output.setStyle("-fx-font-size:18;");
        output.setEditable(false);
//        vb.getChildren().addAll(label,input,label1,output);

        // 提交查询
        Button queryButton = new Button("查询");
//        vb.getChildren().addAll(ok);

        // 输出查询
        TableView tableView = new TableView();
        tableView.setEditable(false);


        vb.getChildren().addAll(label,input,queryButton,tableView, output);
        //表格文本框
//        JTable output2=new JTable();

//        vb.getChildren().addAll(label,input,label1,output);

        //创建菜单栏
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-padding: 12;");
        menuBar.setStyle("-fx-font-size:20;");

        Menu menu1 = new Menu("菜单");
        Menu menu2 = new Menu("帮助");

        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();



        MenuItem menuItem3 = new MenuItem("退出");


        menu1.getItems().addAll(menuItem3);
        menuBar.getMenus().addAll(menu1,menu2);
        TreeView treeView = new TreeView(rootItem);
        BorderPane bl = new BorderPane();
        bl.setTop(menuBar);
        bl.setCenter(vb);
        bl.setLeft(treeView);

        initLogin(rootItem);

        Scene mainScene = new Scene(bl,300,400);
        primaryStage.setTitle("sparkhandler");
        primaryStage.setScene(mainScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        queryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String sql = input.getText();

                try {
//                    sparkhandler.execute(sql,tableView);
                    sparkhandler.execute(sql);

                } catch (SQLException throwables) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("查询语句错误！");
                    alert.show();
                    throwables.printStackTrace();
                }
            }
        });



        menuItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

    };


    public static void Login(){
        Login loginger = new Login();


        while(true){

            if (!loginger.end) {
                try { Thread.sleep ( 500 ) ;
                } catch (InterruptedException ie){}
            }else {
                HostText = loginger.hostText;
                PortText = loginger.portText;
                DatabaseText = loginger.databaseText;
                UsernameText = loginger.usernameText;
                PasswordText = loginger.passwordText;

                //debug




                break;
            }
        }


    }

    private static void initLogin(TreeItem<String> rootItem){
        //连接

        try {
            sparkhandler.connect(HostText,PortText, DatabaseText, UsernameText, PasswordText);
            sparkhandler.connect(UsernameText, PasswordText);

        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("连接数据库失败！");
            alert.show();
            throwables.printStackTrace();
        }

        List<String> list = new ArrayList<String>();
        List<String> subList = new ArrayList<>();
        try {
            list = sparkhandler.bulidTree();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        rootItem.getChildren().clear();
        for(String s : list){
            TreeItem<String> item = new TreeItem<>(s);
            try {
                subList = sparkhandler.buildSubTree(s);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            for(String ss : subList){
                item.getChildren().add(new TreeItem<>(ss));
            }
            //item.getChildren().add(new TreeItem<>())
            rootItem.getChildren().add(item);
        }
    }

}
