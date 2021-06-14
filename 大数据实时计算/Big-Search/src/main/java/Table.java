import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
public class Table {
    public Table(String[][] data) {

        int rows = data.length;
        int cols = data[0].length;
        String[] name=new String[cols];
        for(int i=0;i<cols;i++){
            name[i]=data[0][i];
        }

        JFrame frame=new JFrame("查询结果");
        frame.setBounds(200, 100, 900, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container contentPane=frame.getContentPane();
        Object[][] tableDate=new Object[rows-1][cols];
        for(int i=0;i<rows-1;i++)
        {
            for(int j=0;j<cols;j++)
            {
                tableDate[i][j]=data[i+1][j];
            }
        }

        JTable table=new JTable(tableDate,name);
        contentPane.add(new JScrollPane(table));
        frame.setVisible(true);
    }
}