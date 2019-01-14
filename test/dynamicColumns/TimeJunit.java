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
        JSONObject requestNews = new JSONObject();
        //通过转换过的机型 使用回收搜索接口得到对应机型id
        JSONObject code = new JSONObject();
        code.put("brandcode", "HUAWEI");
        code.put("modelcode", "HUAWEI NXT-AL10");
        String realCode = AES.Encrypt(code.toString());  //加密
        requestNews.put(cipherdata, realCode);
        //发起获取对应productid请求
        Date a=new Date();
        String getResult = AES.post(SystemConstant.RECYCLE_NEW_URL + "getmodelbycode", requestNews);
        Date b=new Date();
        Long c=a.getTime()-b.getTime();
        System.out.print("时间差是"+c);
        //对得到结果进行解密
        code = getResult(AES.Decrypt(getResult));
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
