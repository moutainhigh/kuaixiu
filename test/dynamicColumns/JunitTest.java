package dynamicColumns;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;

import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.card.service.TelecomCardService;
import com.kuaixiu.station.entity.Station;
import com.kuaixiu.station.service.StationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.common.util.BaiduMapUtil;
import com.kuaixiu.recycle.controller.TestZhimaDataBatchFeedback;
import com.kuaixiu.recycle.controller.TestZhimaMerchantCreditlifeFundPay;
import com.kuaixiu.recycle.entity.RecycleCustomer;
import com.kuaixiu.recycle.entity.RecycleOrder;
import com.kuaixiu.recycle.service.AlipayService;
import com.kuaixiu.recycle.service.RecycleCustomerService;
import com.kuaixiu.recycle.service.RecycleOrderService;
import com.kuaixiu.screen.dao.ScreenBrandMapper;
import com.kuaixiu.screen.entity.ScreenBrand;
import com.kuaixiu.screen.service.ScreenBrandService;
import com.kuaixiu.screen.service.ScreenCustomerService;

/**
* @author: anson
* @CreateDate: 2017年10月25日 上午11:21:28
* @version: V 1.0
* 
*/

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"../applicationContext.xml"}) //加载配置文件
public class JunitTest extends BaseJunitTest{

	@Autowired
	private ScreenBrandService screenBrandService;
	@Autowired
	private RecycleOrderService recycleOrderService;
	@Autowired
	private RecycleCustomerService recycleCustomerService; 
	@Autowired
	private AlipayService alipayService;
	@Autowired
	private ScreenCustomerService screenCustomerService;
	@Autowired
	private ScreenBrandMapper<ScreenBrand> mapper;
	@Autowired
	private StationService stationService;
	@Autowired
	private TelecomCardService telecomCardService;
	
	@Test
	public void add(){
		ScreenBrand brand=new ScreenBrand();
		brand.setName("大大米");
        brand.setSort(12);
        brand.setCreateUserId("admins");
        mapper.add(brand);
		System.out.println("增加品牌完毕");
        
	}
	
	
	@Test
	public void addS(){
		ClassPathXmlApplicationContext ct=new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		ct.start();
		ScreenBrandService bean = ct.getBean(ScreenBrandService.class);
		ScreenBrand brand=new ScreenBrand();
		brand.setName("小待米");
        brand.setSort(12);
        brand.setCreateUserId("admins");
        bean.add(brand);
        
	}
	
	@Test
	public void addT(){
		List<ScreenBrand> list=screenBrandService.queryList(null);
        for(ScreenBrand b:list){
        	System.out.println(b.getName()+b.getCreateUserId());
        }
	}
	
	/**
	 * 支付宝代扣测试
	 * @throws Exception
	 */
	@Test
	public void fund() throws Exception{
		RecycleOrder order=recycleOrderService.queryByOrderNo("20180312154022473");
		HttpServletRequest request=null;
		if(order!=null){
			RecycleCustomer cust=recycleCustomerService.queryById(order.getCustomerId());
			order.setPreparePrice(new BigDecimal("0.1"));
			order.setPrice(new BigDecimal("0.2"));
			//发起扣款
			TestZhimaMerchantCreditlifeFundPay f=new TestZhimaMerchantCreditlifeFundPay();
			f.testZhimaMerchantCreditlifeFundPay();
			System.out.println("代扣完毕");
			//发起数据反馈
			TestZhimaDataBatchFeedback.testZhimaDataBatchFeedback(order,cust,request,7);
		}
	}
	
	
	/**
	 * 支付宝转账测试
	 * @throws Exception
	 */
	@Test
	public void transfer() throws Exception{
		RecycleOrder order=recycleOrderService.queryByOrderNo("20180312154022473");
		HttpServletRequest request=null;
		if(order!=null){
			RecycleCustomer cust=recycleCustomerService.queryById(order.getCustomerId());
			order.setPreparePrice(new BigDecimal("0.1"));
			order.setPrice(new BigDecimal("0.2"));
			//发起代扣
			alipayService.transfer(order, "1", "3");
			System.out.println("转账完毕");
			//发起数据反馈
			TestZhimaDataBatchFeedback.testZhimaDataBatchFeedback(order,cust,request,7);
		}
	}
	
	
	/**
	 * 地图转化坐标测试
	 */
	public static void Map(String path){
		JSONObject latAndLngByAddr = BaiduMapUtil.getLatAndLngByAddr(path);
		System.out.println(latAndLngByAddr);
	}
	
	/**
	 * 高德地图转化
	 */
	public static void gaode(String path){
		JSONObject latAndLngByAddr = BaiduMapUtil.gaode(path);
		System.out.println(latAndLngByAddr);
	}
	
	/**
	 * 查询品牌
	 */
	@Test
	public void selectBrand(){
		List<String> list=screenCustomerService.queryAllBrand();
		System.out.println(list.size());
	}


	/**
	 * 修改号卡库存
	 */
	@Test
	public void updateCard(){
		//先查询站点信息
		Station s=new Station();
		List<Station> stations = stationService.queryList(s);

		//更新同步站点信息
		for(Station station:stations) {
			TelecomCard t = new TelecomCard();
			t.setIsUse(1);
            t.setStationId(station.getId());
			List<TelecomCard> telecomCards = telecomCardService.queryList(t);
			System.out.println("station  "+station.getId()+"  站点使用数量"+telecomCards.size());
            if(!telecomCards.isEmpty()){
            	station.setRepertory(station.getDistributionSum()-telecomCards.size());
            	stationService.saveUpdate(station);
			}
		}
		System.out.println("更新完毕");


	}


	
	
	public static void main(String[] args) {
		//Map("浙江 杭州市 江干区 dajksdjad");
		//gaode("浙江 杭州市 余杭区 塘栖镇 交警中队南面杭区中医院行政楼810室");
		gaode("浙江 杭州市 江干区 dajksdjad");
	}
}
