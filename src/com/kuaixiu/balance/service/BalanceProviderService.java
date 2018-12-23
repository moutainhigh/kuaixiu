package com.kuaixiu.balance.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ImportReport;
import com.common.util.FileUtil;
import com.common.util.NOUtil;
import com.kuaixiu.balance.dao.BalanceProviderMapper;
import com.kuaixiu.balance.entity.BalanceProvider;

import com.kuaixiu.model.entity.Model;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.service.OrderService;
import com.system.basic.user.entity.SessionUser;

import jodd.jtx.meta.Transaction;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
 * Balance Service
 * @CreateDate: 2016-10-15 下午02:51:40
 * @version: V 1.0
 */
@Service("balanceProviderService")
public class BalanceProviderService extends BaseService<BalanceProvider> {
    private static final Logger log= Logger.getLogger(BalanceProviderService.class);

    @Autowired
    private BalanceProviderMapper<BalanceProvider> mapper;
    @Autowired
    private OrderService orderService;


    public BalanceProviderMapper<BalanceProvider> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 查询结算单
     * @param batchNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-26 下午10:41:01
     */
    public List<BalanceProvider> queryByBatchNo(String batchNo){
        BalanceProvider t = new BalanceProvider();
        t.setBatchNo(batchNo);
        return getDao().queryList(t);
    }
    
    /**
     * 上传打款回执
     * @param file
     * @param id
     * @param su
     * @author: lijx
     * @CreateDate: 2016-10-22 上午1:24:07
     */
    @Transactional
    public String importImg(MultipartFile file, String id,SessionUser su){
        if(StringUtils.isBlank(id)){
            throw new SystemException("参数错误");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        InputStream inputStream = null;
        String filePath = "";
        try {
            inputStream = file.getInputStream();
            String fileName = NOUtil.getNo("bank_slip_") + "." + extension;
            filePath = "/upload/" + fileName;
            //保存图片
            FileUtil.writeFromInputStream(inputStream, filePath);
            
            //更新结算单状态
            BalanceProvider balance = queryById(id);
            if(balance == null){
                throw new SystemException("结算单未找到");
            }
            balance.setStatus(1);
            balance.setBankSlipImg(filePath);
            balance.setUpdateUserid(su.getUserId());
            getDao().update(balance);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new SystemException("上传图片失败");
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
        return filePath;
    }
    
    /**
     * 删除图片
     * 
     * @author: lijx
     * @CreateDate: 2016-10-22 上午2:06:45
     */
    public void deleteImg(String id, SessionUser su){
        if(StringUtils.isBlank(id)){
            throw new SystemException("参数错误");
        }
      //更新结算单状态
        BalanceProvider balance = queryById(id);
        if(balance == null){
            throw new SystemException("结算单未找到");
        }
        balance.setStatus(0);
        String fileName = balance.getBankSlipImg();
        balance.setBankSlipImg("");
        balance.setUpdateUserid(su.getUserId());
        getDao().update(balance);
        try{
            FileUtil.deleteFile(fileName);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
}