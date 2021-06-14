import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.getConnection;
public class Function {
    private static Connection conn;
    private static SparkHandler sparkHandler;
    private static String userId;
    public static void connect(String user,String password) throws SQLException {

        userId = user;
        String url = "jdbc:hive2://bigdata115.depts.bingosoft.net:22115/"+user+"_db";
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn = getConnection(url,user,password);

    }

    public static void connect(String hostText,String portText,String databaseText,String usernameText,String passwordText) throws SQLException{

        sparkHandler = new SparkHandler(hostText,portText,databaseText,usernameText,passwordText);


    }


    public static void execute(String sql, TableView output) throws SQLException {


        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);

    }

    public static void execute(String sql) throws SQLException {
        String[][] res2 =sparkHandler.getRes(sql);
        new Table(res2);

    }
    public static void execute(String sql, TextArea output) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        while (res.next()) {
            String row = res.getString(1);
            //输出所有表名
            output.appendText(row+"\n");
        }
    }


    public static List<String> bulidTree() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show tables");
        List<String> temp = new ArrayList<String>();
        while(res.next()){
            temp.add(res.getString(1));
        }
        return temp;
    }

    public static List<String> buildSubTree(String s) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show columns FROM " + s);
        List<String> list = new ArrayList<>();
        while(res.next()) {
            list.add(res.getString(1));
        }
        return list;
    }


}
