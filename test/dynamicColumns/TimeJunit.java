package dynamicColumns;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderPayLogService;
import com.kuaixiu.order.service.OrderPayService;
import com.system.constant.SystemConstant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/30/030.
 */
public class TimeJunit extends JunitTest {

    //回收订单状态变化时调用，更改状态  参数签名
    private static final String autograph = "HZYNKJ@SUPER2017";
    private static final String cipherdata = SystemConstant.RECYCLE_REQUEST;

    @Test
    public void aaa() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long totalTime = 180l;

        Long time = sdf.parse("2018-11-27 18:15:48").getTime() - sdf.parse("2019-01-22 18:15:48").getTime();
        Long aa = totalTime + time / (1000 * 3600 * 24);
    }

    /**
     * 返回结果解析
     *
     * @param originalString
     * @return
     */
    public static JSONObject getResult(String originalString) {
        JSONObject result = (JSONObject) JSONObject.parse(originalString);
        if (result.getString("result") != null && !result.getString("result").equals("RESPONSESUCCESS")) {
            throw new SystemException(result.getString("msg"));
        }
        return result;
    }

}
