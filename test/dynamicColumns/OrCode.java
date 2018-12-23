package dynamicColumns;

import com.common.util.NOUtil;
import com.common.util.SmsSendUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.shop.entity.Shop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2018/8/30/030.
 */
public class OrCode extends JunitTest {

    @Resource
    EngineerService engineerService;

    @Test
    public void aaa(){
        Engineer eng=engineerService.queryById("008bedaa-ffaa-11e6-be36-00163e04c890");
        //更改工程师状态
        eng.setIsDispatch(1);
        Integer a=engineerService.saveUpdate(eng);
        Order o = new Order();

        //生成订单号
        String orderNo = NOUtil.getNo("PO-");
        o.setOrderNo(orderNo);
        o.setEngineerName(eng.getName());
        o.setEngineerMobile(eng.getMobile());
        o.setIsDispatch(1);
        //给工程师发送短信提示
        SmsSendUtil.sendSmsToEngineer(eng, new Shop(), o);
    }
}
