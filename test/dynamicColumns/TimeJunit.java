package dynamicColumns;

import com.kuaixiu.order.entity.OrderPayLog;
import com.kuaixiu.order.service.OrderPayLogService;
import com.kuaixiu.order.service.OrderPayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/30/030.
 */
public class TimeJunit extends JunitTest {

    @Autowired
    private OrderPayService orderPayService;
@Autowired
private OrderPayLogService payLogService;

    @Test
    public void aaa() throws Exception {
        OrderPayLog payLog=payLogService.queryByPayOrderNo("PN-20181215150524089");
        orderPayService.aliPayOrder(payLog);//支付宝
    }


}
