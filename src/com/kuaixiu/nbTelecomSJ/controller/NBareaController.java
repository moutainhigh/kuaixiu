package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBarea;
import com.kuaixiu.nbTelecomSJ.service.NBareaService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * NBarea Controller
 *
 * @CreateDate: 2019-02-22 下午06:31:04
 * @version: V 1.0
 */
@Controller
public class NBareaController extends BaseController {

    @Autowired
    private NBareaService nBareaService;


    /**
     * 获取支局包区
     */
    @RequestMapping(value = "NBTelecomSJ/getOfficeAndArea")
    @ResponseBody
    public ResultData getOfficeAndArea(HttpServletRequest request, HttpServletResponse response){
        ResultData result=new ResultData();
        try {
            JSONObject params=getPrarms(request);
            String countyId=params.getString("countyId");

            if(StringUtils.isBlank(countyId)){
                return getResult(result,null,false,"2","参数为空");
            }

            NBarea nBarea=new NBarea();
            nBarea.setCountyId(Integer.valueOf(countyId));
            List<NBarea> nBareas=nBareaService.getDao().queryList(nBarea);

            getResult(result,nBareas,true,"0","成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
