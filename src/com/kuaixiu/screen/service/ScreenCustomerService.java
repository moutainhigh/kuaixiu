package com.kuaixiu.screen.service;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.importExcel.ExcelUtil;
import com.common.importExcel.ImportError;
import com.common.importExcel.ImportReport;
import com.kuaixiu.screen.dao.ScreenCustomerMapper;
import com.kuaixiu.screen.entity.ScreenCustomer;
import com.system.basic.user.entity.SessionUser;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2018年3月5日 下午2:57:05
 * @version: V 1.0
 * 
 */
@Service("screenCustomerService")
public class ScreenCustomerService extends BaseService<ScreenCustomer> {

	private static final Logger log = Logger.getLogger(ScreenCustomerService.class);

	@Autowired
	private ScreenCustomerMapper<ScreenCustomer> mapper;

	@Override
	public ScreenCustomerMapper<ScreenCustomer> getDao() {

		return mapper;
	}

	/**
	 * 通过手机号查询
	 * 
	 * @param mobile
	 * @return
	 */
	public ScreenCustomer queryByMobile(String mobile) {
		return getDao().queryByMobile(mobile);
	}
	
	/**
	 * 通过订单号查询
	 */
	public ScreenCustomer queryByOrderNo(String orderNo) {
		return getDao().queryByOrderNo(orderNo);
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
		// 获取查询条件
		String queryStartTime = MapUtils.getString(params, "query_startTime");
		String queryEndTime = MapUtils.getString(params, "query_endTime");

		ScreenCustomer sc = new ScreenCustomer();
		sc.setQueryStartTime(queryStartTime);
		sc.setQueryEndTime(queryEndTime);
		String idStr = MapUtils.getString(params, "ids");
		if (StringUtils.isNotBlank(idStr)) {
			String[] ids = StringUtils.split(idStr, ",");
			sc.setQueryIds(Arrays.asList(ids));
		}
		List<ScreenCustomer> list = queryList(sc);
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (ScreenCustomer o : list) {
			// 格式化时间
			String startTime = sdf.format(o.getInTime());
			o.setStringInTime(startTime);
			if (o.getIsActive() == 0) {
				o.setActive("未激活");
			} else {
				o.setActive("已激活");
			}
		}
		map.put("orderList", list);

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

	/**
	 * 导入
	 * 
	 * @param file
	 * @param report
	 * @param su
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
			// 检查模板是否正确
			if (checkExcelModel(workbook, report)) {
				// 检查表格数据
				List<ScreenCustomer> list = checkData(workbook, report);
				if (report.isPass() && list.size() > 0) {
					// 保存数据
					saveData(list, su);
				}
			} else {
				report.setContinueNext(false);
				report.setPass(false);
			}
		} catch (SystemException e) {
			e.printStackTrace();
			report.setPass(false);
			report.setContinueNext(false);
			report.setError("导入错误：" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			report.setPass(false);
			report.setContinueNext(false);
			report.setError("系统异常请联系管理员");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private static Map<Integer, String> titleMap = new HashMap<Integer, String>();
	static {
		titleMap.put(0, "型号");
		titleMap.put(1, "产商");
		titleMap.put(2, "终端串码");
		titleMap.put(3, "终端手机号码");
	}

	/**
	 * 检查模板是否正确 导入模板标题只要判断列数正确即可 其他数据信息由实际数据来判断
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
//		// 模板数据
//		Set<Integer> set = titleMap.keySet();
//		System.out.println("模板："+set.size()+"实际："+sheet.getLastRowNum());
//		if (set.size() != sheet.getLastRowNum()) {
//			report.setPass(false);
//			report.setContinueNext(false);
//			report.setError("导入模板不正确");
//			return false;
//		}
		// for (Integer key : set) {
		// String t1=row0.getCell(key).toString().trim();
		// String t2=titleMap.get(key);
		// System.out.println("t1："+t1+" t2："+t2);
		// if(t1==null||!t1.equals(t2)){
		// report.setPass(false);
		// report.setContinueNext(false);
		// report.setError("导入模板不正确");
		// return false;
		// }
		// }
		return true;
	}

	/**
	 * 检查表格数据
	 * 
	 * @param workbook
	 * @param report
	 * @return
	 */
	private List<ScreenCustomer> checkData(Workbook workbook, ImportReport report) {
		Sheet sheet = workbook.getSheetAt(0);
		int rowNum = sheet.getLastRowNum();
		List<ScreenCustomer> list = new ArrayList<ScreenCustomer>();
		for (int i = 1; i <= rowNum; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			ScreenCustomer p = new ScreenCustomer();
			int col = 0;
			String value = ExcelUtil.getCellValue(row, col).trim();
			if (StringUtils.isBlank(value) || value.length() > 32) {
				ImportError error = new ImportError();
				error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
				error.setMsgType("型号错误");
				error.setMessage("型号不能为空，长度不能超过32个字符！");
				report.getErrorList().add(error);
				report.setPass(false);
			} else {
				p.setModel(value);
			}
			col++;
			value = ExcelUtil.getCellValue(row, col).trim();
			if (StringUtils.isBlank(value) || value.length() > 32) {
				ImportError error = new ImportError();
				error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
				error.setMsgType("厂商错误");
				error.setMessage("厂商不能为空，长度不能超过32个字符！");
				report.getErrorList().add(error);
				report.setPass(false);
			} else {
				p.setBrand(value);
			}
			col++;
			value = ExcelUtil.getCellValue(row, col).trim();
			if (StringUtils.isBlank(value) || value.length() > 32) {
				ImportError error = new ImportError();
				error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
				error.setMsgType("终端串码错误");
				error.setMessage("终端串码不能为空，长度不能超过32个字符！");
				report.getErrorList().add(error);
				report.setPass(false);
			} else {
				p.setImei(value);
			}

			col++;
			value = ExcelUtil.getCellValue(row, col).trim();
			if (StringUtils.isBlank(value) || value.length() > 16) {
				ImportError error = new ImportError();
				error.setPosition("第" + (row.getRowNum() + 1) + "行," + (col + 1) + "列");
				error.setMsgType("终端手机号码错误");
				error.setMessage("终端手机号码不能为空，长度不能超过16个字符！");
				report.getErrorList().add(error);
				report.setPass(false);
			} else {
				p.setMobile(value);
			}
			p.setIsDel(0);
			p.setIsActive(0);
			p.setId(UUID.randomUUID().toString().replace("-", ""));
			list.add(p);

		}
		return list;
	}

	/**
	 * 开始新增免激活用户
	 */
	@Transactional
	public void saveData(List<ScreenCustomer> list, SessionUser su) {
		for (ScreenCustomer p : list) {
			// 新增记录
			getDao().add(p);

		}
	}

	/**
	 * 查询有效手机品牌 5天内的
	 * 
	 * @param mobile
	 * @return
	 */
	public List<String> queryAllBrand() {
		return getDao().queryAllBrand();
	}

	/**
	 * 查询有效品牌下机型
	 * 
	 * @param mobile
	 * @return
	 */
	public List<String> queryAllModel(String brand) {
		return getDao().queryAllModel(brand);
	}
}
