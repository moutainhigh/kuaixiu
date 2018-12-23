package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.common.util.NOUtil;
import com.kuaixiu.card.dao.BatchCardMapper;
import com.kuaixiu.card.entity.BatchCard;
import com.kuaixiu.card.entity.BatchImport;
import com.kuaixiu.card.entity.TelecomCard;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/7/25
 * @Description:卡号批量导入
 */
@Service
public class BatchCardService extends BaseService<BatchCard> {

    private static final Logger log= Logger.getLogger(BatchCardService.class);


    @Autowired
    private BatchCardMapper<BatchCard> mapper;

    @Override
    public BatchCardMapper<BatchCard> getDao() {
        return mapper;
    }

    @Autowired
    private TelecomCardService telecomCardService;

    @Autowired
    private BatchImportService batchImportService;



    /**
     * 电渠卡号批量导入
     * @param file
     * @param report
     * @param su
     */
    @Transactional
    public void importExcel(MultipartFile file, ImportReport report, SessionUser su){
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        Workbook workbook = null;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 根据后缀实例化，xls实例化HSSFWorkbook,xlsx实例化XSSFWorkbook
            if (extension.equalsIgnoreCase("xls")){
                workbook = new HSSFWorkbook(inputStream);
            }
            else {
                workbook = new XSSFWorkbook(inputStream);
            }
            //检查模板是否正确
            if(checkExcelModel(workbook,report)){
                //检查表格数据
                List<BatchCard> list=checkData(workbook, report, su);
                if(report.isPass() && list.size() > 0){
                    //保存数据
                    saveData(list, su,report);
                }
            }
            else{
                report.setContinueNext(false);
                report.setPass(false);
            }
        }
        catch (SystemException e) {
            e.printStackTrace();
            report.setPass(false);
            report.setContinueNext(false);
            report.setError("导入错误："+ e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            report.setPass(false);
            report.setContinueNext(false);
            report.setError("系统异常请联系管理员");
        }
        finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                }
                catch (IOException e) {

                }
            }
        }

    }


    /**
     * 添加卡号到数据库   保存批次
     * @param list
     * @param su
     */
    @Transactional
    public void saveData(List<BatchCard> list, SessionUser su,ImportReport report){
        String batch= NOUtil.getNo("HK");    //生成批次
        //讲同一批次数据统一汇总
        BatchImport batchImport=new BatchImport();
        batchImport.setBatchId(batch);
        int sum=0;  //该批次的总数量
        try {
            for(BatchCard b:list){
                  String id=UUID.randomUUID().toString().replace("-","");
                  b.setId(id);
                  b.setBatch(batch);
                  b.setCreateUserid(su.getUserId());
                  this.add(b);
                  //给当前批次依次插入号卡数据
                  insertTeleCard(b,report);
                  sum+=b.getSum();
            }
            batchImport.setSum(sum);
            batchImport.setProvince(list.get(0).getProvince());
            batchImport.setType(list.get(0).getType());
            batchImport.setCardName(list.get(0).getCardName());
            batchImport.setCreateUserid(list.get(0).getCreateUserid());
            batchImport.setLoseEfficacy(list.get(0).getLoseEfficacy());
            batchImportService.add(batchImport);
        } catch (Exception e) {
            e.printStackTrace();
            ImportError error = new ImportError();
            error.setPosition("无");
            error.setMsgType("数据库插入批次错误");
            error.setMessage(e.getMessage().substring(0,512));
            report.getErrorList().add(error);
            report.setPass(false);

        }
    }




    /**
     * 根据批次生成单个卡号  mysql拼装sql有上限  经测试3万条以上会出现数据过长  在这里以3万条为界限进行拆分
     * @param b
     */
    @Transactional
    public void insertTeleCard(BatchCard b,ImportReport report){
        int sum=0;
        int maxSum=30000;
        int size=b.getSum();
        int pushSum=(((double)size/(double)maxSum)>(size/maxSum)?size/maxSum+1:size/maxSum);   //推送的次数  向上取整
        try {
            for (int i = 0; i <pushSum ; i++) {
                List<TelecomCard> list=new ArrayList<TelecomCard>();
                for(int j=sum;j<sum+maxSum;j++){
                    if(j>=size){
                        //如果超过最大数 跳出循环
                        break;
                    }else{
                        TelecomCard telecomCard = createTelecomCard(b,j);
                        list.add(telecomCard);
                    }
                }
                telecomCardService.addBatch(list);
                sum=sum+maxSum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ImportError error = new ImportError();
            error.setPosition("无");
            error.setMsgType("数据库批量插入号卡出错");
            error.setMessage(e.getMessage().substring(0,512));
            report.getErrorList().add(error);
            report.setPass(false);
        }


    }




    /**
     * 根据批次生成一个号卡
     * @param b
     * @return
     */
    public TelecomCard createTelecomCard(BatchCard b,int i){
        TelecomCard t=new TelecomCard();
        t.setBatch(b.getBatch());
        t.setBatchId(b.getId());
        t.setProvince(b.getProvince());
        t.setType(b.getType());
        t.setCardName(b.getCardName());
        t.setLoseEfficacy(b.getLoseEfficacy());
        t.setIccid((new BigDecimal(b.getBeginIccid()).add(new BigDecimal(i))).toString());
        return t;

    }

    /**
     * 导入模板字段
     */
    private static Map<Integer,String>titleMap=new HashMap<Integer, String>();
    static {
        titleMap.put(0, "所属本地网");
        titleMap.put(1, "起始ICCID");
        titleMap.put(2, "结束ICCID");
        titleMap.put(3, "数量");
        titleMap.put(4, "号卡类型");
        titleMap.put(5, "号卡名称");
    }








    /**
     * 检查模板是否正确
     * @param workbook
     * @return
     */
    private boolean checkExcelModel(Workbook workbook, ImportReport report){
        Sheet sheet=workbook.getSheetAt(0);
        Row row0=sheet.getRow(0);
        if(row0 == null){
            return false;
        }
        //模板数据
        Set<Integer> set = titleMap.keySet();
        for (Integer key : set) {
            String t1=row0.getCell(key).toString().trim();
            String t2=titleMap.get(key);
            if(t1==null||!t1.equals(t2)){
                report.setPass(false);
                report.setContinueNext(false);
                report.setError("导入模板不正确");
                return false;
            }
        }
        return true;
    }

    /**
     * 检查表格数据  将数据转化为对应卡号批次实体类  自动生成一个批次
     * @param workbook
     * @param report
     * @param su
     * @return
     */
    private List<BatchCard> checkData(Workbook workbook,ImportReport report,SessionUser su){
        Calendar calendar = new GregorianCalendar(1900,0,-1);   //处理poi读取excel时间格式为数字的结果
        Date d = calendar.getTime();

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Sheet sheet=workbook.getSheetAt(0);
        int rowNum=sheet.getLastRowNum();
        //添加的批次卡号
        List<BatchCard> batchCardList=new ArrayList<BatchCard>();

        for(int i = 1; i <=rowNum; i++){
            Row row = sheet.getRow(i);
            if(row==null){
                continue;
            }
            BatchCard b=new BatchCard();

            int col = 0;
            //所属本地网
            String value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value) || value.length() > 18) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMessage("所属本地网不能为空，长度不能超过18个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                b.setProvince(value);
            }
            //起始ICCID
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            BigDecimal begin=new BigDecimal(value);
            if (StringUtils.isBlank(value) || value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("起始ICCID错误");
                error.setMessage("起始ICCID不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                b.setBeginIccid(value);
            }
            //结束ICCID
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            BigDecimal end=new BigDecimal(value);
            if (StringUtils.isBlank(value) || value.length() > 36) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("结束ICCID错误");
                error.setMessage("结束ICCID不能为空，长度不能超过36个字符！");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                b.setEndIccid(value);
            }
            //数量
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("数量错误");
                error.setMessage("数量不能为空");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                //判断号卡区间和对应数量是否匹配
                BigDecimal s=end.subtract(begin);
                if(s.add(new BigDecimal(1)).intValue()!=(Integer.parseInt(value))){
                    ImportError error = new ImportError();
                    error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                    error.setMsgType("号卡区间数量和输入数量不匹配");
                    report.getErrorList().add(error);
                    report.setPass(false);
                }
                b.setSum(Integer.parseInt(value));
            }
            //号卡类型
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("号卡类型错误");
                error.setMessage("号卡类型不能为空");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
               b.setType(Integer.parseInt(value));
            }
            //号卡名称
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("号卡名称错误");
                error.setMessage("号卡名称不能为空");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
               b.setCardName(Integer.parseInt(value));
            }
            //失效时间
            col++;
            value = ExcelUtil.getCellValue(row, col).trim();
            if (StringUtils.isBlank(value)) {
                ImportError error = new ImportError();
                error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
                error.setMsgType("失效时间错误");
                error.setMessage("失效时间不能为空");
                report.getErrorList().add(error);
                report.setPass(false);
            } else {
                    Date date = DateUtils.addDays(d,Integer.valueOf(value));
                    b.setLoseEfficacy(date);
            }
            batchCardList.add(b);

        }
        return batchCardList;
    }



    /**
     * 下载导入模板
     *
     * @param params
     */
    @SuppressWarnings("rawtypes")
    public void expImportTemplate(Map<String, Object> params) {
        String templateFileName = params.get("tempFileName") + "";
        String outFileName = params.get("outFileName") + "";
        try {
            Workbook workbook = new HSSFWorkbook(new FileInputStream(templateFileName));
            FileOutputStream fileOut = new FileOutputStream(outFileName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (ParsePropertyException e) {
            e.printStackTrace();
            log.error("文件导出--ParsePropertyException", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件导出--IOException", e);
        }
    }



}
