package com.kuaixiu.model.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.kuaixiu.brand.entity.Brand;
import com.kuaixiu.brand.service.BrandService;
import com.kuaixiu.model.dao.ModelMapper;
import com.kuaixiu.model.entity.Model;
import com.kuaixiu.model.entity.RepairCost;
import com.kuaixiu.project.entity.Project;
import com.kuaixiu.project.service.ProjectService;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * Model Service
 *
 * @CreateDate: 2016-09-03 上午12:26:35
 * @version: V 1.0
 */
@Service("modelService")
public class ModelService extends BaseService<Model> {
    private static final Logger log = Logger.getLogger(ModelService.class);

    @Autowired
    private ModelMapper<Model> mapper;
    @Autowired
    private RepairCostService repairCostService;
    @Autowired
    private ProjectService projectService;


    public ModelMapper<Model> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据品牌查询机型
     *
     * @param brandId
     * @return
     * @CreateDate: 2016-9-29 下午8:21:48
     */
    public List<Model> queryByBrandId(String brandId) {
        Model m = new Model();
        m.setBrandId(brandId);
        return getDao().queryList(m);
    }

    /**
     * 验证机型名称是否存在
     *
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午8:21:48
     */
    public boolean checkModelName(String name, String brandId) {
        List<Model> list = getDao().queryByName(name, brandId);
        return list != null && list.size() > 0;
    }

    /**
     * 保存机型
     *
     * @param m
     * @param repairCosts
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    @Transactional
    public int save(Model m, String repairCosts, SessionUser su) {
        //生成id
        String id = UUID.randomUUID().toString();
        m.setId(id);
        //验证机型名称是否存在
        if (checkModelName(m.getName(), m.getBrandId())) {
            throw new SystemException("该机型名称已存在");
        }
        //解析维修费用
        JSONArray jsonArray = JSONArray.parseArray(repairCosts);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            RepairCost cost = new RepairCost();
            cost.setProjectId(json.getString("projectId"));
            cost.setModelId(id);
            cost.setPrice(new BigDecimal(json.getDouble("cost")));
            cost.setCreateUserid(su.getUserId());
            cost.setIdDel(0);
            repairCostService.add(cost);
        }
        //保存机型
        getDao().add(m);
        return 1;
    }

    /**
     * 更新维修项目
     *
     * @param m
     * @return
     * @CreateDate: 2016-8-31 下午7:08:33
     */
    @Transactional
    public int update(Model m, String repairCosts, SessionUser su) {
        if (m == null || StringUtils.isBlank(m.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        //先清空原维修费用
        repairCostService.deleteByModelId(m.getId());
        //解析维修费用
        JSONArray jsonArray = JSONArray.parseArray(repairCosts);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            RepairCost cost = new RepairCost();
            cost.setProjectId(json.getString("projectId"));
            cost.setModelId(m.getId());
            cost.setPrice(new BigDecimal(json.getDouble("cost")));
            cost.setCreateUserid(su.getUserId());
            cost.setIdDel(0);
            repairCostService.add(cost);
        }
        Model model = getDao().queryById(m.getId());
        if (model == null) {
            throw new SystemException("维修机型未找到，无法更新");
        }
        if (!m.getName().equals(model.getName())) {
            //验证机型名称是否存在
            if (checkModelName(m.getName(), m.getBrandId())) {
                throw new SystemException("该机型名称已存在");
            }
        }
        model.setName(m.getName());
        model.setBrandId(m.getBrandId());
        model.setBrandName(m.getBrandName());
        model.setColor(m.getColor());
        model.setUpdateUserid(m.getUpdateUserid());
        model.setSort(m.getSort());
        model.setLogo(m.getLogo());
        return getDao().update(model);
    }

    /**
     * 删除维修项目
     *
     * @param m
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int delete(Model m) {
        if (m == null || StringUtils.isBlank(m.getId())) {
            throw new SystemException("参数为空，无法更新");
        }
        Model model = getDao().queryById(m.getId());
        model.setIsDel(1);
        model.setUpdateUserid(m.getUpdateUserid());
        return getDao().update(model);
    }

    /**
     * 删除维修项目
     *
     * @param idStr
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    @Transactional
    public int deleteById(String idStr, SessionUser su) {
        if (StringUtils.isBlank(idStr)) {
            throw new SystemException("参数为空，无法更新");
        }
        //处理批量操作
        String[] ids = idStr.split(",");
        for (String id : ids) {
            Model m = getDao().queryById(id);
            m.setIsDel(1);
            m.setUpdateUserid(su.getUserId());
            getDao().update(m);
        }
        return 1;
    }

    /**
     * 查询机型对应项目的维修费用
     * 使用动态行转列
     *
     * @param params
     * @return
     * @CreateDate: 2016-9-7 下午9:02:54
     */
    public List<Map<String, Object>> queryModelPriceGroupByProject(Map<String, Object> params) {
        String subSql = buildModelPriceGroupByProjectSql();
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put("subSql", subSql);
        return getDao().queryModelPriceGroupByProject(params);
    }

    /**
     * 组装sql查询机型对象维修项目的维修金额
     *
     * @return
     * @CreateDate: 2016-9-7 下午9:55:05
     */
    private String buildModelPriceGroupByProjectSql() {
        List<Project> projectL = projectService.queryList(null);
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT model_id ");
        for (Project p : projectL) {
            sql.append(", MAX(IF(project_id=\'");
            sql.append(p.getId());
            sql.append("\',price,0)) AS \'");
            sql.append(p.getId());
            sql.append("\' ");
        }
        sql.append(" FROM kx_repair_cost GROUP BY model_id ");
        return sql.toString();
    }

    /**
     * 导出模板
     *
     * @param params
     */
    public void expExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";
        //查询维修项目
        List<Map<String, Object>> project = projectService.getDao().queryProjectName();

        List<String> cols = new ArrayList<String>();
        for (Map<String, Object> map : project) {
            cols.add(map.get("name").toString());
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cols", cols);

        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }

    /**
     * 商品导入主入口
     */
    @Transactional
    public void importExcel(MultipartFile file, ImportReport report, SessionUser su) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Workbook workbook = null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 根据后缀实例化，xls实例化HSSFWorkbook,xlsx实例化XSSFWorkbook
            if (extension.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }
            //检查模板是否正确
            if (checkExcelModel(workbook, report)) {
                //检查表格数据
                List<Model> list = checkData(workbook, report);
                if (report.isPass() && list.size() > 0) {
                    //保存数据
                    saveData(list, su);
                }
            } else {
                report.setContinueNext(false);
                report.setPass(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private static final BigDecimal zero = new BigDecimal(0);

    @Autowired
    private BrandService brandService;

    /**
     * 保存机型
     *
     * @param models
     * @param su
     * @return
     * @CreateDate: 2016-9-3 上午12:43:26
     */
    @Transactional
    public void saveData(List<Model> models, SessionUser su) {
        //获取维修项目
        List<Project> projects = projectService.queryList(null);
        Map<String, String> projectMap = new HashMap<String, String>();
        for (Project p : projects) {
            projectMap.put(p.getName(), p.getId());
        }

        for (Model m : models) {
            List<Brand> brands = brandService.queryByName(m.getBrandName());
            if (!CollectionUtils.isEmpty(brands)) {
                m.setBrandId(brands.get(0).getId());
            }
            //机型id初始
            String id="";
            //查询是否已录入此机型
            List<Model> models1 = getDao().queryByName(m.getName(), null);
            if (CollectionUtils.isEmpty(models1)) {
                //生成id
                id = UUID.randomUUID().toString();
                m.setId(id);
                //新增
                m.setCreateUserid(su.getUserId());
                m.setIsDel(0);
                getDao().add(m);
            } else {
                //修改
                //先清空原维修费用
                for(Model model:models1){
                    repairCostService.deleteByModelId(model.getId());
                }
                Model model = models1.get(0);
                id=model.getId();
                model.setName(m.getName());
                model.setBrandId(m.getBrandId());
                model.setBrandName(m.getBrandName());
                model.setColor(m.getColor());
                model.setUpdateUserid(su.getUserId());
                getDao().updateByNmae(model);
            }
            //解析维修费用
            Set<String> set = m.getRepairCostMap().keySet();
            for (String key : set) {
                if (m.getRepairCostMap().get(key).compareTo(zero) > 0) {
                    RepairCost cost = new RepairCost();
                    cost.setProjectId(projectMap.get(key));
                    cost.setModelId(id);
                    cost.setPrice(m.getRepairCostMap().get(key));
                    cost.setCreateUserid(su.getUserId());
                    cost.setIdDel(0);
                    repairCostService.add(cost);
                }
            }
        }
    }

    private static Map<Integer, String> titleMap = new HashMap<Integer, String>();

    static {
//        titleMap.put(0, "机型id（可选）");
//        titleMap.put(1, "机型名称（必填）");
//        titleMap.put(2, "品牌（必填）");
//        titleMap.put(3, "颜色 （必填，英文逗号分割）");
        titleMap.put(0, "机型名称（必填）");
        titleMap.put(1, "品牌（必填）");
        titleMap.put(2, "颜色 （必填，英文逗号分割）");
    }

    /**
     * 检查模板是否正确
     *
     * @param workbook
     * @return
     */
    private boolean checkExcelModel(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row0 = sheet.getRow(0);
        if (row0 == null) {
            return false;
        }
        //模板数据
        Set<Integer> set = titleMap.keySet();
        for (Integer key : set) {
            String t1 = row0.getCell(key).toString().trim();
            String t2 = titleMap.get(key);
            if (t1 == null || !t1.equals(t2)) {
                report.setContinueNext(false);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查表格数据
     *
     * @param workbook
     * @param report
     * @return
     * @CreateDate: 2016-9-17 下午6:09:33
     */
    private List<Model> checkData(Workbook workbook, ImportReport report) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        List<Model> list = new ArrayList<Model>();
        //存放维修项目名称
        Map<Integer, String> projectName = new HashMap<Integer, String>();
        Row rowTitle = sheet.getRow(0);
        for (int i = 3; ; i++) {
            String title = rowTitle.getCell(i).toString().trim();
            if (StringUtils.isBlank(title)) {
                break;
            }
            projectName.put(i, title);
        }

        if (projectName.size() == 0) {
            ImportError error = new ImportError();
            error.setPosition("第1行,3列");
            error.setMsgType("模板错误");
            error.setMessage("维修费用不能为空");
            report.getErrorList().add(error);
            report.setPass(false);
            return list;
        }

        for (int i = 1; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Model model = new Model();
            //model.setId(getCellValue(row, 0));
            //验证机型名称
            checkModelName(row, model, report, 0);
            //验证机型品牌
            checkModelBrand(row, model, report, 1);
            //验证机型颜色
            checkModelColor(row, model, report, 2);
            //验证机型维修费用
            Map<String, BigDecimal> repairCostMap = new HashMap<String, BigDecimal>();
            Set<Integer> set = projectName.keySet();
            for (Integer key : set) {
                if (row.getCell(key) == null) {
                    continue;
                }
                String cost = row.getCell(key).toString().trim();
                try {
                    if (StringUtils.isNotBlank(cost)) {
                        repairCostMap.put(projectName.get(key), new BigDecimal(cost));
                    } else {
                        repairCostMap.put(projectName.get(key), new BigDecimal(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (key + 1) + "列");
                    error.setMsgType("维修费用错误");
                    error.setMessage("维修费用格式不正确");
                    report.getErrorList().add(error);
                    report.setPass(false);
                }
            }
            model.setRepairCostMap(repairCostMap);
            list.add(model);
        }

        return list;
    }

    /**
     * 验证机型名称
     *
     * @param row
     * @param model
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkModelName(Row row, Model model, ImportReport report, int col) {
        //机型id
        String modelName = getCellValue(row, col);

        if (StringUtils.isBlank(modelName) || modelName.length() > 32) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("名称错误");
            error.setMessage("机型名称不能为空，长度不能超过32个字符！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            model.setName(modelName);
        }
    }

    /**
     * 验证机型品牌
     *
     * @param row
     * @param model
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkModelBrand(Row row, Model model, ImportReport report, int col) {
        //机型id
        String brandName = getCellValue(row, col);

        if (StringUtils.isBlank(brandName) || brandName.length() > 32) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("品牌错误");
            error.setMessage("机型品牌不能为空，长度不能超过32个字符！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            model.setBrandName(brandName);
        }
    }

    /**
     * 验证机型颜色
     *
     * @param row
     * @param model
     * @param report
     * @param col
     * @CreateDate: 2016-9-17 下午5:41:23
     */
    private void checkModelColor(Row row, Model model, ImportReport report, int col) {
        //机型id
        String color = getCellValue(row, col);

        if (StringUtils.isBlank(color)) {
            ImportError error = new ImportError();
            error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
            error.setMsgType("颜色错误");
            error.setMessage("机型颜色不能为空！");
            report.getErrorList().add(error);
            report.setPass(false);
        } else {
            model.setColor(color.replaceAll("，", ","));
        }
    }

    /**
     * 先设置单元格格式为string类型然后获取单元格内的值，
     *
     * @param row
     * @param columnIndex
     * @return
     * @CreateDate: 2016-9-17 下午5:43:50
     */
    public static String getCellValue(Row row, int columnIndex) {
        Cell cell = CellUtil.getCell(row, columnIndex);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        String value = cell.getStringCellValue().trim();
        return value;
    }

    /**
     * 已Excel形式导出列表数据
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expDataExcel(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String destFileName = params.get("outFileName") + "";

        //获取查询条件
        String name = MapUtils.getString(params, "query_name");
        Model m = new Model();
        m.setName(name);

        String idStr = MapUtils.getString(params, "ids");
        if (StringUtils.isNotBlank(idStr)) {
            String[] ids = StringUtils.split(idStr, ",");
            m.setQueryIds(Arrays.asList(ids));
        }

        List<Model> list = queryList(m);
        if (list != null && list.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Model model : list) {
                List<RepairCost> costs = repairCostService.queryListByModelId(model.getId());
                sb.setLength(0);
                if (costs != null && costs.size() > 0) {
                    for (int i = 0; i < costs.size(); i++) {
                        if (i > 0) {
                            sb.append("、");
                        }
                        sb.append(costs.get(i).getProjectName());
                    }
                }
                model.setId(sb.toString());
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("modelList", list);

        XLSTransformer transformer = new XLSTransformer();
        try {
            transformer.transformXLS(templateFileName, map, destFileName);
        } catch (ParsePropertyException e) {
            log.error("文件导出--ParsePropertyException", e);
        } catch (InvalidFormatException e) {
            log.error("文件导出--InvalidFormatException", e);
        } catch (IOException e) {
            log.error("文件导出--IOException", e);
        }
    }
}