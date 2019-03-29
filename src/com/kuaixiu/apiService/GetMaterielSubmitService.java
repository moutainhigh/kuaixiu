package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.common.util.NOUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.materiel.entity.MaterielType;
import com.kuaixiu.materiel.entity.Process;
import com.kuaixiu.materiel.entity.ProcessMateriel;
import com.kuaixiu.materiel.service.MaterielTypeService;
import com.kuaixiu.materiel.service.ProcessMaterielService;
import com.kuaixiu.materiel.service.ProcessService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author: najy
 * 领取物料提交信息
 * Created by Administrator on 2019/3/27/027.
 */
@Service("getMaterielSubmitService")
public class GetMaterielSubmitService implements ApiServiceInf{
    private static final Logger log = Logger.getLogger(GetMaterielSubmitService.class);

    @Autowired
    private EngineerService engineerService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private ProcessMaterielService processMaterielService;

    @Override
    public Object process(Map<String, String> params) {
        Object json = new Object();
        try {
            //获取工程师工号和密码
            String number = MapUtils.getString(params, "pmClientId");
            //解析请求参数
            String paramJson = MapUtils.getString(params, "params");
            JSONObject pmJson = JSONObject.parseObject(paramJson);
            //验证请求参数
            if (pmJson == null
                    || !pmJson.containsKey("materiel")
                    || !pmJson.containsKey("time")) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            Engineer engineer=engineerService.queryByEngineerNumber(number);
            //获取订单号
            Date time = pmJson.getDate("time");
            //创建流程申请
            String processNo=createProcess(time,engineer);

            JSONArray materiels = pmJson.getJSONArray("materiel");
            for(int i=0;i<materiels.size();i++){
                JSONObject materiel=(JSONObject)materiels.get(i);
                Integer materielId=materiel.getInteger("materielId");
                Integer materielNum=materiel.getInteger("materielNum");
                ProcessMateriel processMateriel=new ProcessMateriel();
                processMateriel.setProcessNo(processNo);
                processMateriel.setMaterielId(materielId);
                processMateriel.setAppleNum(materielNum);
                processMaterielService.add(processMateriel);
            }
            json = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return json;
    }

    private String createProcess(Date time,Engineer engineer){
        Process process=new Process();
        String processNo=NOUtil.getNo("lc-")+NOUtil.getRandomInteger(5);
        process.setProcessNo(processNo);
        process.setProcessType(1);
        process.setProcessState(1);
        process.setApplyNo(engineer.getNumber());
        process.setApplyPerson(engineer.getName());
        process.setDealNo("admin");
        process.setDealPerson("系统管理员");
        process.setRecevieTime(time);
        processService.add(process);
        return processNo;
    }
}
