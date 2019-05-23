package com.kuaixiu.sjUser.service;


import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.util.SmsSendUtil;
import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.sjBusiness.entity.*;
import com.kuaixiu.sjBusiness.service.*;
import com.kuaixiu.sjUser.dao.CustomerDetailMapper;
import com.kuaixiu.sjUser.entity.CustomerDetail;

import com.kuaixiu.sjUser.entity.SjUser;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * CustomerDetail Service
 *
 * @CreateDate: 2019-05-06 上午10:48:12
 * @version: V 1.0
 */
@Service("customerDetailService")
public class CustomerDetailService extends BaseService<CustomerDetail> {
    private static final Logger log = Logger.getLogger(CustomerDetailService.class);

    @Autowired
    private CustomerDetailMapper<CustomerDetail> mapper;
    @Autowired
    private AreaCityCompanyService cityCompanyService;
    @Autowired
    private AreaManagementUnitService managementUnitService;
    @Autowired
    private AreaBranchOfficeService branchOfficeService;
    @Autowired
    private AreaContractBodyService contractBodyService;
    @Autowired
    private SjCodeService codeService;


    public CustomerDetailMapper<CustomerDetail> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    public JSONObject getUserToJsonObject(SjUser sjUser, CustomerDetail customerDetail) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", sjUser.getName());
        jsonObject.put("phone", sjUser.getPhone());
        StringBuilder ascription = new StringBuilder();
        AreaCityCompany areaCityCompany = cityCompanyService.queryById(customerDetail.getCityCompanyId());
        String cityCompany = areaCityCompany.getCityCompany();
        jsonObject.put("cityCompanyId", areaCityCompany.getId());
        jsonObject.put("cityCompany", cityCompany);
        ascription.append(cityCompany);
        if (customerDetail.getManagementUnitId() != null) {
            AreaManagementUnit managementUnit1 = managementUnitService.queryById(customerDetail.getManagementUnitId());
            if (managementUnit1 != null) {
                String managementUnit = managementUnit1.getManagementUnit();
                ascription.append("/" + managementUnit);
                jsonObject.put("managementUnitId", managementUnit1.getId());
                jsonObject.put("managementUnit", managementUnit);
            }
        }
        if (customerDetail.getBranchOfficeId() != null) {
            AreaBranchOffice branchOffice1 = branchOfficeService.queryById(customerDetail.getBranchOfficeId());
            if (branchOffice1 != null) {
                String branchOffice = branchOffice1.getBranchOffice();
                ascription.append("/" + branchOffice);
                jsonObject.put("branchOfficeId", branchOffice1.getId());
                jsonObject.put("branchOffice", branchOffice);
            }
        }
        if (customerDetail.getContractBodyId() != null) {
            AreaContractBody contractBody1 = contractBodyService.queryById(customerDetail.getContractBodyId());
            if (contractBody1 != null) {
                String contractBody = contractBody1.getContractBody();
                ascription.append("/" + contractBody);
                jsonObject.put("contractBodyId", contractBody1.getId());
                jsonObject.put("contractBody", contractBody);
            }
        }
        jsonObject.put("ascription", ascription.toString());
        jsonObject.put("marketingNo", customerDetail.getMarketingNo());
        return jsonObject;
    }

    /**
     * 获取验证码并保存到session
     *
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    public String getRandomCode(HttpServletRequest request, String key) {
        String randomCode = SmsSendUtil.randomCode();
        request.getSession().setAttribute(SystemConstant.SESSION_RANDOM_CODE + key, randomCode);
        //保存验证码到数据库
        SjCode code = codeService.queryById(key);
        if (code == null) {
            code = new SjCode();
            code.setCode(randomCode);
            code.setPhone(key);
            codeService.add(code);
        } else {
            code.setCode(randomCode);
            code.setUpdateTime(new Date());
            codeService.saveUpdate(code);
        }
        return randomCode;
    }

    @Autowired
    private SjOrderService orderService;

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        HttpServletResponse response = (HttpServletResponse) params.get("response");

        //获取查询条件
        // 获取iccid  是否已寄出  批次号   是否已分配   号卡类型   号卡名称   号卡地市   导入时间区间
        // 分配时间区间   推送电渠状态  转转推送给超人的时间区间  超人推送电渠的时间区间
        String namePhone = MapUtils.getString(params, "namePhone");
        String ascription = MapUtils.getString(params, "ascription");//归属
        String queryStartTime = MapUtils.getString(params, "query_startTime");
        String queryEndTime = MapUtils.getString(params, "query_endTime");
        String marketingNo = MapUtils.getString(params, "marketingNo");
        CustomerDetail customerDetail = new CustomerDetail();
        if (StringUtils.isNotBlank(namePhone)) {
            if (orderService.isNumeric(namePhone)) {
                customerDetail.setPhone(namePhone);
            } else {
                customerDetail.setName(namePhone);
            }
        }
        customerDetail.setQueryStartTime(queryStartTime);
        customerDetail.setQueryEndTime(queryEndTime);
        customerDetail.setMarketingNo(marketingNo);
        if (StringUtils.isNotBlank(ascription)) {
            switch (ascription) {
                case "1":
                    customerDetail.setCityCompanyId(1);
                    break;
                case "2":
                    customerDetail.setManagementUnitId(1);
                    break;
                case "3":
                    customerDetail.setBranchOfficeId(1);
                    break;
                case "4":
                    customerDetail.setContractBodyId(1);
                    break;
            }
        }
        //login_id,name,phone,city_company_id,management_unit_id,branch_office_id,
        // contract_body_id,marketing_no,create_time,is_cancel
        List<Map<String, Object>> maps = this.getDao().queryCustomerList(customerDetail);

        List<List<Map<String, Object>>> lists = new ArrayList<>();
        int size = maps.size() / 10000;
        List<String> listAgent = new ArrayList<>();
        if (maps.size() < 10000) {
            lists.add(maps);
            listAgent.add("集合");
        } else {
            for (int i = 0; i < size; i++) {
                List<Map<String, Object>> list = maps.subList(i * 10000, (i + 1) * 10000);
                lists.add(list);
                listAgent.add("集合" + i);
            }
            //余数
            int lastSize = maps.size() - size * 10000;
            List<Map<String, Object>> list = maps.subList(size * 10000, size * 10000 + lastSize);
            lists.add(list);
            listAgent.add("集合" + size);
        }

        //excel标题
        String[] header = new String[]{"登录号", "姓名", "手机号", "所属", "营销工号", "注册时间", "是否注销"};

// 导出到多个sheet中--------------------------------------------------------------------------------开始
        // 创建一个EXCEL
        HSSFWorkbook wb = new HSSFWorkbook();
        // 循环经销商，每个经销商一个sheet
        for (int i = 0; i < listAgent.size(); i++) {
            // 第 i 个sheet,以经销商命名
            HSSFSheet sheet = wb.createSheet(listAgent.get(i).toString());
            // 第i个sheet第二行为列名
            HSSFRow rowFirst = sheet.createRow(0);
            // 写标题
            for (int j = 0; j < header.length; j++) {
                // 获取第一行的每一个单元格
                HSSFCell cell = rowFirst.createCell(j);
                // 往单元格里面写入值
                cell.setCellValue(header[j]);
            }
            for (int j = 0; j < lists.get(i).size(); j++) {
                Map<?, ?> map = (Map<?, ?>) lists.get(i).get(j);
                // 创建数据行，从第三行开始
                HSSFRow row = sheet.createRow(j + 1);
                // 设置对应单元格的值
                getRow(row, map);
            }
        }
        // 写出文件（path为文件路径含文件名）
        OutputStream os = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filedisplay = "客户经理导出.xls";
            //防止文件名含有中文乱码
            filedisplay = new String(filedisplay.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

            os = response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            System.out.println("导出文件出错了.....\n" + e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                System.out.println("导出文件出错了.....\n" + e.getMessage());
            }
        }
        // 导出到多个sheet中---------------------------------------------------------------------------------结束

    }

    private HSSFRow getRow(HSSFRow row, Map<?, ?> map) {
        // 设置对应单元格的值
        if (map.get("login_id") == null) {
            row.createCell(0).setCellValue("");
        } else {
            row.createCell(0).setCellValue(map.get("login_id").toString());
        }
        if (map.get("name") == null) {
            row.createCell(1).setCellValue("");
        } else {
            row.createCell(1).setCellValue(map.get("name").toString());
        }
        if (map.get("phone") == null) {
            row.createCell(2).setCellValue("");
        } else {
            row.createCell(2).setCellValue(map.get("phone").toString());
        }
        if (map.get("type_name") == null) {
            row.createCell(3).setCellValue("");
        } else {
            row.createCell(3).setCellValue(map.get("type_name").toString());
        }
        if (map.get("marketing_no") == null) {
            row.createCell(4).setCellValue("");
        } else {
            row.createCell(4).setCellValue(map.get("marketing_no").toString());
        }
        if (map.get("create_time") == null) {
            row.createCell(5).setCellValue("");
        } else {
            row.createCell(5).setCellValue(map.get("create_time").toString());
        }
        if (map.get("is_cancel") == null) {
            row.createCell(6).setCellValue("");
        } else {
            if ("1".equals(map.get("is_cancel").toString())) {
                row.createCell(6).setCellValue("是");
            } else {
                row.createCell(6).setCellValue("否");
            }
        }

        return row;
    }
}