package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.kuaixiu.nbTelecomSJ.dao.NBBusinessMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBBusiness;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * NBBusiness Service
 *
 * @CreateDate: 2019-02-23 上午11:53:31
 * @version: V 1.0
 */
@Service("nBBusinessService")
public class NBBusinessService extends BaseService<NBBusiness> {
    private static final Logger log = Logger.getLogger(NBBusinessService.class);

    @Autowired
    private NBBusinessMapper<NBBusiness> mapper;


    public NBBusinessMapper<NBBusiness> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public NBBusiness queryByOpenId(String openId) {
        NBBusiness nbBusiness = new NBBusiness();
        List<NBBusiness> nbBusinesses = getDao().queryByOpenId(openId);
        if (CollectionUtils.isEmpty(nbBusinesses)) {
            return null;
        }
        return nbBusinesses.get(0);
    }


    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        HttpServletResponse response = (HttpServletResponse) params.get("response");

        //获取查询条件
        String queryStartTime = MapUtils.getString(params, "queryStartTime");
        String queryEndTime = MapUtils.getString(params, "queryEndTime");
        String countyId = MapUtils.getString(params, "countyId");
        String officeId = MapUtils.getString(params, "officeId");
        String areaId = MapUtils.getString(params, "areaId");
        String companyName = MapUtils.getString(params, "companyName");
        String landline = MapUtils.getString(params, "landline");
        String broadband = MapUtils.getString(params, "broadband");
        String addressType = MapUtils.getString(params, "addressType");
        String demand = MapUtils.getString(params, "demand");
        String idStr = MapUtils.getString(params, "ids");

        NBBusiness nbBusiness = new NBBusiness();
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            nbBusiness.setQueryIds(Arrays.asList(ids));
        }
        nbBusiness.setQueryStartTime(queryStartTime);
        nbBusiness.setQueryEndTime(queryEndTime);
        if (StringUtils.isNotBlank(countyId)) {
            nbBusiness.setCountyId(Integer.valueOf(countyId));
        }
        if (StringUtils.isNotBlank(officeId)) {
            nbBusiness.setOfficeId(Integer.valueOf(officeId));
        }
        if (StringUtils.isNotBlank(areaId)) {
            nbBusiness.setAreaId(Integer.valueOf(areaId));
        }
        nbBusiness.setCompanyName(companyName);
        nbBusiness.setLandline(landline);
        nbBusiness.setBroadband(broadband);
        if (StringUtils.isNotBlank(addressType)) {
            nbBusiness.setAddressType(Integer.valueOf(addressType));
        }
        if (StringUtils.isNotBlank(demand)) {
            nbBusiness.setDemand(Integer.valueOf(demand));
        }

        List<Map<String, Object>> maps = getDao().queryListMap(nbBusiness);
        for (Map<String, Object> map : maps) {
            getMap(map);
        }
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
        String[] header = new String[]{"创建时间", "县分", "支局", "包区", "单位名称",
                "固定电话", "宽带", "地址属性", "通信需求", "备注",
                "联系人/手机号", "走访人/手机号", "包区人/手机号"};

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
            String filedisplay = "号卡导出.xls";
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
        if (map.get("createTime") == null) {
            row.createCell(0).setCellValue("");
        } else {
            row.createCell(0).setCellValue(map.get("createTime").toString());
        }
        if (map.get("county") == null) {
            row.createCell(1).setCellValue("");
        } else {
            row.createCell(1).setCellValue(map.get("county").toString());
        }
        if (map.get("branch_office") == null) {
            row.createCell(2).setCellValue("");
        } else {
            row.createCell(2).setCellValue(map.get("branch_office").toString());
        }
        if (map.get("area_name") == null) {
            row.createCell(3).setCellValue("");
        } else {
            row.createCell(3).setCellValue(map.get("area_name").toString());
        }
        if (map.get("company_name") == null) {
            row.createCell(4).setCellValue("");
        } else {
            row.createCell(4).setCellValue(map.get("company_name").toString());
        }
        if (map.get("landline") == null) {
            row.createCell(5).setCellValue("");
        } else {
            row.createCell(5).setCellValue(map.get("landline").toString());
        }
        if (map.get("broadband") == null) {
            row.createCell(6).setCellValue("");
        } else {
            row.createCell(6).setCellValue(map.get("broadband").toString());
        }
        if (map.get("address_type") == null) {
            row.createCell(7).setCellValue("");
        } else {
            row.createCell(7).setCellValue(map.get("address_type").toString());
        }
        if (map.get("address") == null) {
            row.createCell(8).setCellValue("");
        } else {
            row.createCell(8).setCellValue(map.get("address").toString());
        }
        if (map.get("demand") == null) {
            row.createCell(9).setCellValue("");
        } else {
            row.createCell(9).setCellValue(map.get("demand").toString());
        }
        if (map.get("remarks") == null) {
            row.createCell(10).setCellValue("");
        } else {
            row.createCell(10).setCellValue(map.get("remarks").toString());
        }
        if (map.get("coutomer_name") == null) {
            row.createCell(11).setCellValue("");
        } else {
            row.createCell(11).setCellValue(map.get("coutomer_name").toString());
        }
        if (map.get("manager_name") == null) {
            row.createCell(12).setCellValue("");
        } else {
            row.createCell(12).setCellValue(map.get("manager_name").toString());
        }
        if (map.get("area_person") == null) {
            row.createCell(13).setCellValue("");
        } else {
            row.createCell(13).setCellValue(map.get("area_person").toString());
        }
        return row;
    }

    private void getMap(Map<String, Object> map) {
        String coutomer_name = "";
        String telephone = "";
        if (map.get("coutomer_name") != null) {
            coutomer_name = map.get("coutomer_name").toString();
        }
        if (map.get("telephone") != null) {
            telephone = map.get("telephone").toString();
        }
        if (StringUtils.isBlank(coutomer_name) && StringUtils.isBlank(telephone)) {
            map.put("coutomer_name", "");
        } else {
            map.put("coutomer_name", coutomer_name + "/" + telephone);
        }
        String manager_name = "";
        String manager_tel = "";
        if (map.get("manager_name") != null) {
            manager_name = map.get("manager_name").toString();
        }
        if (map.get("manager_tel") != null) {
            manager_tel = map.get("manager_tel").toString();
        }
        if (StringUtils.isBlank(manager_name) && StringUtils.isBlank(manager_tel)) {
            map.put("manager_name", "");
        } else {
            map.put("manager_name", manager_name + "/" + manager_tel);
        }
        String area_person = "";
        String person_tel = "";
        if (map.get("area_person") != null) {
            area_person = map.get("area_person").toString();
        }
        if (map.get("person_tel") != null) {
            person_tel = map.get("person_tel").toString();
        }
        if (StringUtils.isBlank(area_person) && StringUtils.isBlank(person_tel)) {
            map.put("area_person", "");
        } else {
            map.put("area_person", area_person + "/" + person_tel);
        }

        String address_type = getAddressType(map.get("address_type").toString());
        map.put("address_type", address_type);
        String getDemand = getDemand(map.get("demand").toString());
        map.put("demand", getDemand);
        String landline1 = map.get("landline").toString();
        if (landline1.contains(",")) {
            String[] landlines = landline1.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < landlines.length; i++) {
                sb.append(getlandlines(landlines[i]) + ",");
            }
            map.put("landline", sb.toString());
        } else {
            map.put("landline", getlandlines(landline1));
        }
        String broadband1 = map.get("broadband").toString();
        if (broadband1.contains(",")) {
            String[] broadbands = broadband1.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < broadbands.length; i++) {
                sb.append(getlandlines(broadbands[i]) + ",");
            }
            map.put("broadband", sb.toString());
        } else {
            map.put("broadband", getlandlines(broadband1));
        }
    }

    private String getDemand(String demand) {
        String state = "";
        switch (demand) {
            case "1":
                state = "无需求";
                break;
            case "2":
                state = "宽带体验";
                break;
            case "3":
                state = "专线体验";
                break;
            case "4":
                state = "战狼办理";
                break;
            case "5":
                state = "其他需求";
                break;
            default:
                state = "";
        }
        return state;
    }

    private String getAddressType(String address_type) {
        String state = "";
        switch (address_type) {
            case "1":
                state = "楼宇";
                break;
            case "2":
                state = "园区";
                break;
            case "3":
                state = "市场";
                break;
            case "4":
                state = "沿街";
                break;
            case "5":
                state = "工厂";
                break;
            default:
                state = "";
        }
        return state;
    }

    private String getlandlines(String landline) {
        String landline1 = "";
        switch (landline) {
            case "1":
                landline1 = "联通";
                break;
            case "2":
                landline1 = "电信";
                break;
            case "3":
                landline1 = "移动";
                break;
            case "4":
                landline1 = "无";
                break;
            default:
                landline1 = "";
        }
        return landline1;
    }
}