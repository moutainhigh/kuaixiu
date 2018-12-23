package dynamicColumns;

/**
 * @Auther: anson
 * @Date: 2018/7/16
 * @Description:批量插入
 */

import com.mysql.jdbc.Connection;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class MysqlBatchUtil {
    private String sql="INSERT INTO hk_express (id,sort) VALUES (?,?)";
    private String charset="utf-8";
    private String connectStr="jdbc:mysql://localhost:3306/kuaixiu";
    private String username="root";
    private String password="root";
    private void doStore() throws ClassNotFoundException, SQLException, IOException {
        System.out.println("开始操作");
        Class.forName("com.mysql.jdbc.Driver");
        connectStr += "?useServerPrepStmts=false&rewriteBatchedStatements=true";//此处是测试高效批次插入，去掉之后执行时普通批次插入
        Connection conn = (Connection) DriverManager.getConnection(connectStr, username,password);
        conn.setAutoCommit(false); // 设置手动提交
        int count = 0;
        PreparedStatement psts = conn.prepareStatement(sql);
        String line = null;
        Date begin=new Date();
        for(int i=0;i<=20;i++){
            psts.setString(1, UUID.randomUUID().toString());
            psts.setInt(2,1);
           // psts.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            psts.addBatch();   // 加入批量处理
            count++;
        }
        psts.executeBatch(); // 执行批量处理
        conn.commit(); // 提交
        Date end=new Date();
        System.out.println("数量="+count);
        System.out.println("运行时间="+(end.getTime()-begin.getTime()));
        conn.close();
    }
    public static void main(String[] args) {
        try {
            new MysqlBatchUtil().doStore();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
