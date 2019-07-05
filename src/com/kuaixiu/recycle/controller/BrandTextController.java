package com.kuaixiu.recycle.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.entity.BrandText;
import com.kuaixiu.recycle.service.BrandTextService;
import com.system.api.entity.ResultData;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * BrandText Controller
 *
 * @CreateDate: 2018-12-04 下午05:02:18
 * @version: V 1.0
 */
@Controller
public class BrandTextController extends BaseController {

    @Autowired
    private BrandTextService brandTextService;

    /**
     * 品牌机型查询版本号
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "recycle/checkVoLTE")
    @ResponseBody
    public ResultData brandText(HttpServletRequest request, HttpServletResponse response) {
        ResultData resultData=new ResultData();
        try {
            JSONObject jsonObject=new JSONObject();
            JSONObject params = getPrarms(request);
            String brandName = params.getString("brandName");
            String modelNo = params.getString("modelNo");
            if(StringUtils.isBlank(brandName)||StringUtils.isBlank(modelNo)){
                return getResult(resultData,jsonObject,false,"2","参数为空");
            }
            BrandText brandText=new BrandText();
            brandText.setBrand(brandName);
            brandText.setModelNo(modelNo);
            List<BrandText> brandTexts=brandTextService.queryList(brandText);
            if(CollectionUtils.isEmpty(brandTexts)){
                jsonObject.put("isTrue","0");
            }else {
                jsonObject.put("isTrue","1");
            }
            getResult(resultData,jsonObject,true,"0","成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultData;
    }
}
