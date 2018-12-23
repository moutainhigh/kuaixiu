package com.kuaixiu.recycle.controller;

import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.paginate.Page;
import com.google.common.collect.Maps;
import com.kuaixiu.recycle.entity.PrizeRecord;
import com.kuaixiu.recycle.entity.RecyclePrize;
import com.kuaixiu.recycle.service.PrizeRecordService;
import com.kuaixiu.recycle.service.RecyclePrizeService;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 * Description:奖品控制
 * 
 * @author anson
 * 
 * @date 2018年6月13日
 */
@Controller
public class RecyclePrizeController extends BaseController {

	@Autowired
	private RecyclePrizeService recyclePrizeService;
    @Autowired
	private PrizeRecordService prizeRecordService;


	/**
	 * 
	 * 
	 * Description: 奖品列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "recycle/prize")
	public ModelAndView RecycleSystem(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String returnView = "prize/prizeList";
		return new ModelAndView(returnView);
	}

	/**
	 * 
	 * 
	 * Description: 刷新奖品列表数据
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "recycle/prize/queryListForPage")
	public void systemForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		// 获取查询条件
		String name = request.getParameter("query_name");
		String batch = request.getParameter("query_batch");
		String grade = request.getParameter("query_grade");
		RecyclePrize r = new RecyclePrize();
		Page page = getPageByRequest(request);
		r.setPrizeName(name);
		r.setBatch(batch);
		if(StringUtils.isNotBlank(grade)){
			r.setGrade(Integer.parseInt(grade));
		}
		r.setPage(page);
		List<RecyclePrize> list = recyclePrizeService.queryListForPage(r);
		page.setData(list);
		this.renderJson(response, page);
	}

	/**
	 * 
	 * 
	 * Description: 编辑奖品页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "recycle/prize/edit")
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		String prizeId=request.getParameter("prizeId");
		if(StringUtils.isBlank(prizeId)){
			throw new SystemException("请求参数不完整");
		}
		RecyclePrize prize = recyclePrizeService.queryById(prizeId);
	    request.setAttribute("prize", prize);
		String returnView = "prize/editPrize";
		return new ModelAndView(returnView);
	}

	/**
	 * 
	 * 
	 * Description: 保存奖品
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "recycle/prize/save")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
		Map<String, Object> resultMap = Maps.newHashMap();

		RecyclePrize r = new RecyclePrize();
		// 获取奖品名称 中奖率 总数量 奖品等级 奖品详情 奖品批次
		String prizeName = request.getParameter("prize_name");
		String prizeProbability = request.getParameter("prize_probability");
		String totalSum = request.getParameter("total_sum");
		String grade = request.getParameter("grade");
		String details = request.getParameter("details");
		String sort = request.getParameter("sort");
		String batch = request.getParameter("batch");
		if(StringUtils.isBlank(prizeName)||StringUtils.isBlank(prizeProbability)||StringUtils.isBlank(totalSum)||StringUtils.isBlank(grade)){
            throw new SystemException("参数不完整");
		}
		//目前设置最多设置到六等奖  且每个批次每个奖项只能存在一种
        if(Integer.parseInt(grade)>Integer.parseInt(SystemConstant.MAX_PRIZE)){
			throw new SystemException("目前奖项最多可设置到六等奖");
		}
		//中奖概率不能低于配置中定义的参数(目前配置为100000)  因为抽奖随机数采用的int类型比较 不能存在小数区间

        //查询该批次的改类别奖项是否已存在
		RecyclePrize rp=new RecyclePrize();
		rp.setGrade(Integer.parseInt(grade));
		rp.setBatch(batch);
		List<RecyclePrize> recyclePrizeList = recyclePrizeService.queryList(rp);
        if(!recyclePrizeList.isEmpty()){
        	throw new SystemException("该批次该等级奖项已经存在，不可重复添加");
		}

		if(StringUtils.isBlank(sort)){
			//排序为空时 默认填充99
			sort="99";
		}
        r.setPrizeName(prizeName);
        r.setPrizeProbability(prizeProbability);
        r.setTotalSum(Integer.parseInt(totalSum));
        r.setGrade(Integer.parseInt(grade));
        r.setDetails(details);
        //批出使用系统参数文件里的参数
        r.setBatch(batch);
        r.setSort(Integer.parseInt(sort));
        r.setCreateUser(su.getUserId());
        recyclePrizeService.add(r);
		resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
		resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
		renderJson(response, resultMap);
	}
	
	
	/**
	 * 
	
	 * Description: 修改奖品
	
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/recycle/updatePrize")
    public void updatePrize(HttpServletRequest request,
                              HttpServletResponse response) throws SystemException, IOException {
		SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM) {
			throw new SystemException("对不起，您没有操作权限!");
		}
        Map<String, Object> resultMap = Maps.newHashMap();
        String id = request.getParameter("prizeId");
        RecyclePrize prize = recyclePrizeService.queryById(id);
        if(prize==null){
        	throw new SystemException("奖品不存在!");
        }
        String prizeName = request.getParameter("prizeName");
        String totalSum = request.getParameter("totalSum");
        String prizeProbability = request.getParameter("prizeProbability");
        String grade = request.getParameter("grade");
        String sort = request.getParameter("sort");
        String details = request.getParameter("details");
        prize.setPrizeName(prizeName);
        prize.setPrizeProbability(prizeProbability);
        prize.setDetails(details);
        if(!StringUtils.isBlank(totalSum)){
			//设置的奖品总数不能低于已使用的数量
			if(Integer.parseInt(totalSum)<prize.getUseSum()){
				throw  new SystemException("奖品总数不能小于已使用的数量");
			}
        	prize.setTotalSum(Integer.parseInt(totalSum));
        }
        if(!StringUtils.isBlank(sort)){
        	prize.setSort(Integer.parseInt(sort));
        }
		if(!StringUtils.isBlank(grade)){
        	if(Integer.parseInt(grade)>Integer.parseInt(SystemConstant.MAX_PRIZE)){
				throw new SystemException("目前奖项最多可设置到六等奖");
			}
			//保存每一个批次的每一个奖项只存在一条记录
			RecyclePrize rp=new RecyclePrize();
			rp.setGrade(Integer.parseInt(grade));
			rp.setBatch(SystemConstant.NOW_PRIZE_BATCH);
			List<RecyclePrize> recyclePrizeList = recyclePrizeService.queryList(rp);
			if(recyclePrizeList.size()>1){
				throw new SystemException("该批次该等级奖项已经存在，不可重复添加");
			}
			prize.setGrade(Integer.parseInt(grade));
		}
        recyclePrizeService.saveUpdate(prize);
        resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
        resultMap.put(RESULTMAP_KEY_MSG, "保存成功");
        renderJson(response, resultMap);
    }



    /**
     * 抽奖记录
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/getPrizerRecordList")
    public ModelAndView getPrizerRecordList(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String returnView = "prize/getPrizerRecordList";
        return new ModelAndView(returnView);
    }


    /**
     * 抽奖记录数据源
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "recycle/prizeRecord/queryListForPage")
    public void prizeRecordForPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionUser su = getCurrentUser(request);
		if (su.getType() != SystemConstant.USER_TYPE_SYSTEM&&su.getType() != SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
			throw new SystemException("对不起，您没有操作权限!");
		}
        // 获取查询条件
		String mobile=request.getParameter("query_mobile");
        String isGet = request.getParameter("query_isGet");
        String grade = request.getParameter("query_grade");
        String batch = request.getParameter("query_batch");
        PrizeRecord record = new PrizeRecord();
        if(!StringUtils.isBlank(isGet)){
            record.setIsGet(Integer.parseInt(isGet));
        }
        if(!StringUtils.isBlank(grade)){
            record.setGrade(Integer.parseInt(grade));
        }
        if(StringUtils.isNotBlank(batch)){
        	record.setBatch(batch);
		}
        record.setMobile(mobile);
        Page page = getPageByRequest(request);
        record.setPage(page);
        List<PrizeRecord> list = prizeRecordService.queryListForPage(record);
        page.setData(list);
        this.renderJson(response, page);
    }

}
