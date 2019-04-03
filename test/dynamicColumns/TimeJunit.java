package dynamicColumns;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.common.util.AES;
import com.kuaixiu.apiService.OrderApiService;
import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderPayLogService;
import com.kuaixiu.order.service.OrderPayService;
import com.system.constant.SystemConstant;
import org.apache.commons.collections.map.HashedMap;
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

    @Autowired
    private OrderApiService apiService;

    @Test
    public void aaa() throws Exception {
        Map<String,String> map=new HashedMap();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("pageIndex",1);
        jsonObject.put("pageSize",10);
        jsonObject.put("status",50);
        jsonObject.put("newStatus",1);
        map.put("params",jsonObject.toJSONString());
        map.put("pmClientId","hbwhe002");apiService.process(map);

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
