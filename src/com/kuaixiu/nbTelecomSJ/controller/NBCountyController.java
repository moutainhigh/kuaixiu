package com.kuaixiu.nbTelecomSJ.controller;

import com.common.base.controller.BaseController;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;
import com.kuaixiu.nbTelecomSJ.service.NBCountyService;
import com.system.api.entity.ResultData;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * NBCounty Controller
 *
 * @CreateDate: 2019-02-22 下午06:31:52
 * @version: V 1.0
 */
@Controller
public class NBCountyController extends BaseController {
    private static final Logger log= Logger.getLogger(NBCountyController.class);

    @Autowired
    private NBCountyService nBCountyService;


    /**
     * 获取经营单元
     */
    @RequestMapping(value = "NBTelecomSJ/getCounty")
    @ResponseBody
    public ResultData getCounty(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            List<NBCounty> nbCounty = nBCountyService.getDao().queryList(null);
            List<Map<String,Object>> maps = new ArrayList<>();
            for (NBCounty nbCounty1 : nbCounty) {
                Map<String,Object> map = new HashedMap();
                map.put("countyId", nbCounty1.getCountyId());
                map.put("county", nbCounty1.getCounty());
                maps.add(map);
            }
            getResult(result, maps, true, "0", "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return result;
    }

}
