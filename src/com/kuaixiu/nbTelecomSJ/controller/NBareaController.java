package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.service.NBAreaService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * NBArea Controller
 *
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
@Controller
public class NBAreaController extends BaseController {

    @Autowired
    private NBAreaService nBAreaService;

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

            NBArea nBarea=new NBArea();
            nBarea.setCountyId(Integer.valueOf(countyId));
            List<NBArea> nBareas=nBAreaService.getDao().queryList(nBarea);

            getResult(result,nBareas,true,"0","成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
