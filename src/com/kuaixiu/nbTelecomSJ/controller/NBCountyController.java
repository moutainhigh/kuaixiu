package com.kuaixiu.nbTelecomSJ.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.kuaixiu.nbTelecomSJ.service.NBCountyService;
import com.system.api.entity.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * NBCounty Controller
 *
 * @CreateDate: 2019-02-22 下午06:31:52
 * @version: V 1.0
 */
@Controller
public class NBCountyController extends BaseController {

    @Autowired
    private NBCountyService nBCountyService;


    /**
     * 获取经营单元
     */
    @RequestMapping(value = "NBTelecomSJ/getCounty")
    @ResponseBody
    public ResultData getCounty(HttpServletRequest request, HttpServletResponse response){
        ResultData result=new ResultData();
        try {
            List<NBCounty> nbCounty=nBCountyService.getDao().queryList(null);
            getResult(result,nbCounty,true,"0","成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
