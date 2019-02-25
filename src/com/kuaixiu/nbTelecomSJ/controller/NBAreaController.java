package com.kuaixiu.nbTelecomSJ.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;
import com.kuaixiu.nbTelecomSJ.service.NBAreaService;
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
 * NBArea Controller
 *
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
@Controller
public class NBAreaController extends BaseController {
    private static final Logger log= Logger.getLogger(NBAreaController.class);

    @Autowired
    private NBAreaService nBAreaService;

    /**
     * 获取支局包区
     */
    @RequestMapping(value = "NBTelecomSJ/getOfficeAndArea")
    @ResponseBody
    public ResultData getOfficeAndArea(HttpServletRequest request, HttpServletResponse response) {
        ResultData result = new ResultData();
        try {
            JSONObject params = getPrarms(request);
            String countyId = params.getString("countyId");

            if (StringUtils.isBlank(countyId)) {
                return getResult(result, null, false, "2", "参数为空");
            }

            List<NBArea> nbAreas=nBAreaService.getDao().queryByCountyId(countyId);

            List<Map<String, Object>> maps = new ArrayList<>();
            for (NBArea nbArea : nbAreas) {
                Map<String, Object> map = new HashedMap();
                map.put("officeId", nbArea.getOfficeId());
                map.put("branchOffice", nbArea.getBranchOffice());
                List<NBArea> nbAreas1=nBAreaService.getDao().queryByBranchOffice(nbArea.getBranchOffice());
                List<Map<String, Object>> maps1 = new ArrayList<>();
                for(NBArea nbArea1:nbAreas1){
                    Map<String, Object> map1 = new HashedMap();
                    map1.put("areaId", nbArea1.getAreaId());
                    map1.put("areaPerson", nbArea1.getAreaName());
                    maps1.add(map1);
                }
                map.put("area",maps1);
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
