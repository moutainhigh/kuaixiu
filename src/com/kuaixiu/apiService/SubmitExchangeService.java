package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.ApiServiceException;
import com.common.util.NOUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.materiel.entity.Process;
import com.kuaixiu.materiel.entity.ProcessMateriel;
import com.kuaixiu.materiel.service.ProcessMaterielService;
import com.kuaixiu.materiel.service.ProcessService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author: najy
 * 提交兑换/压屏/报废/退物料
 * Created by Administrator on 2019/3/27/027.
 */
@Service("submitExchangeService")
public class SubmitExchangeService  extends BaseController implements ApiServiceInf{

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
                    || !pmJson.containsKey("type")) {
                throw new ApiServiceException(ApiResultConstant.resultCode_1001, ApiResultConstant.resultCode_str_1001);
            }
            Engineer engineer=engineerService.queryByEngineerNumber(number);
            //获取订单号
            Integer type = pmJson.getInteger("type");
            //创建流程申请
            String processNo=createProcess(type,engineer);
            if(type==2||type ==3){
                JSONArray images=pmJson.getJSONArray("images");
//            //获取图片，保存图片到webapp同级inages/activityCompany目录
//            String savePath = serverPath(request.getServletContext().getRealPath("")) + System.getProperty("file.separator") + SystemConstant.IMAGE_PATH + System.getProperty("file.separator") + "activityCompany" + System.getProperty("file.separator") + "hd_images";
//            String logoPath = getPath(request, "file", savePath);             //图片路径
//            String imageUrl = getProjectUrl(request) + "/images/activityCompany/hd_images" + logoPath.substring(logoPath.lastIndexOf("/") + 1);
//            System.out.println("图片路径：" + savePath);
            }

            JSONArray materiels = pmJson.getJSONArray("materiel");
            for(int i=0;i<materiels.size();i++){
                JSONObject materiel=(JSONObject)materiels.get(i);
                Integer materielId=materiel.getInteger("materielId");
                Integer materielNum=materiel.getInteger("materielNum");
                ProcessMateriel processMateriel=new ProcessMateriel();
                if(type==2||type ==3){
                    BigDecimal price=materiel.getBigDecimal("price");
                    processMateriel.setPrice(price);
                }
                processMateriel.setProcessNo(processNo);
                processMateriel.setMaterielId(materielId);
                processMateriel.setAppleNum(materielNum);
                processMaterielService.add(processMateriel);
            }
            json = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            json = "NO";
        }
        return json;
    }

    private String createProcess(Integer type,Engineer engineer){
        Process process=new Process();
        String processNo= NOUtil.getNo("lc-")+NOUtil.getRandomInteger(5);
        switch (type){
            case 2:
                process.setProcessType(2);
                process.setProcessState(3);
                break;
            case 3:
                process.setProcessType(3);
                process.setProcessState(4);
                break;
            case 4:
                process.setProcessType(4);
                process.setProcessState(6);
                break;
            case 6:
                process.setProcessType(6);
                process.setProcessState(5);
                break;
        }
        process.setProcessNo(processNo);
        process.setApplyNo(engineer.getNumber());
        process.setApplyPerson(engineer.getName());
        process.setDealNo("admin");
        process.setDealPerson("系统管理员");
        processService.add(process);
        return processNo;
    }
}
