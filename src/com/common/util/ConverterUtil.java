package com.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.system.util.SystemUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音工具类.
 * 
 * @CreateDate: 2016-9-6 下午7:47:43
 * @version: V 1.0
 */
public class ConverterUtil {

    private static final HanyuPinyinOutputFormat OUTPUT_FORMAT = new HanyuPinyinOutputFormat();
    
    static{
        //转换后以全小写方式输出
        OUTPUT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //无声调表示
        OUTPUT_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
    }
    
    private static final int MAX_ASCII_NUMBER = 128;
    
    /**
     * 重庆重要重大重现重复重启重新重写重修重量重点
     * @param args
     * @CreateDate: 2016-9-6 下午7:27:34
     */
    public static void main(String[] args) {
        String str = "长沙市长";  
        
        //List<String> pinyin = converterToSpell(str);  
        //System.out.println(str+" pin yin ："+pinyin);  
          
        //pinyin = converterToFirstSpell(str);  
        //System.out.println(str+" short pin yin ："+pinyin);

        str = getFirstSpellForAreaName("北京");
        System.out.println(str);
        str = getFirstSpellForAreaName("长沙");
        System.out.println(str);
    }
    
    /**
     * 去地市名称的首字母
     * @param areaName
     * @return
     * @CreateDate: 2016-9-6 下午9:23:56
     */
    public static String getFirstSpellForAreaName(String areaName){
        String firstSpell = "";
        //地市名称预处理
        //对多余两个字的地市去除末尾的市、地区、林区、特别行政区、区、县、州
        if(areaName != null && areaName.length() > 2){
            areaName = removeEndSuffix(areaName);
        }
        List<String> spells = converterToFirstSpell(areaName);
        if(spells.size() > 1){
            //如果有多音字不做处理使用配置文件数据
            firstSpell = SystemUtil.getSysCfgProperty(areaName);
        }
        else{
            firstSpell = spells.get(0);
        }
        return firstSpell;
    }
    
    private static String[] suffixs = {"市", "地区", "林区", "特别行政区", "区", "县", "州"};
    
    /**
     * 
     * @param areaName
     * @return
     * @CreateDate: 2016-9-6 下午10:08:02
     */
    private static String removeEndSuffix(String areaName){
        if (areaName == null) {
            return null;
        }
        for (String sfx : suffixs) {
            if(areaName.endsWith(sfx)){
                areaName = areaName.substring(0, areaName.length() - sfx.length());
                break;
            }
        }
        return areaName;
    }
    
    /** 
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如（长沙市长:cssc,zssz,zssc,cssz） 
     *  
     * @param chines 
     *            汉字 
     * @return 拼音 
     */  
    public static List<String> converterToFirstSpell(String chines) {  
        char[] nameChar = chines.toCharArray();
        List<Set<Object>> list = new ArrayList<Set<Object>>();
        Set<Object> tmp = null;
        for (int i = 0; i < nameChar.length; i++) {  
            tmp = new HashSet<Object>();
            if (nameChar[i] > MAX_ASCII_NUMBER) {
                try {  
                    // 取得当前汉字的所有全拼  
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(  
                            nameChar[i], OUTPUT_FORMAT);  
                    if (strs != null) {
                        for (int j = 0; j < strs.length; j++) {  
                            // 取首字母  
                            tmp.add(strs[j].charAt(0));
                        }  
                    }
                } 
                catch (BadHanyuPinyinOutputFormatCombination e) {  
                    e.printStackTrace();  
                }  
            } 
            else {  
                tmp.add(nameChar[i]);
            }  
            list.add(tmp);
        }  
        // return pinyinName.toString();  
        return parseTheChineseByObject(list);  
    }  
  
    /** 
     * 汉字转换位汉语全拼，英文字符不变，特殊字符丢失 
     * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen 
     * ,chongdangshen,zhongdangshen,chongdangcan） 
     *  
     * @param chines 
     *            汉字 
     * @return 拼音 
     */  
    public static List<String> converterToSpell(String chines) {  
        char[] nameChar = chines.toCharArray();  
        List<Set<Object>> list = new ArrayList<Set<Object>>();
        Set<Object> tmp = null;
        for (int i = 0; i < nameChar.length; i++) {
            tmp = new HashSet<Object>();
            if (nameChar[i] > MAX_ASCII_NUMBER) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], OUTPUT_FORMAT);
                    if (strs != null) {
                        for (String s : strs) {
                            tmp.add(s);
                        }
                    }
                }
                catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }
            else {
                tmp.add(nameChar[i]);
            }  
            list.add(tmp);  
        }  
        return parseTheChineseByObject(list);  
    }
  
    /** 
     * 解析并组合拼音，对象合并方案(推荐使用) 
     *  
     * @return 
     */  
    private static List<String> parseTheChineseByObject(List<Set<Object>> list) {  
        // 拼音组合
        Set<Object> spellGroups = new HashSet<Object>();
        // 每一组集合与上一次组合的Map  
        Set<Object> temp = new HashSet<Object>();
        StringBuffer sb = new StringBuffer();
        // 遍历每一组集合
        for (int i = 0; i < list.size(); i++) {
            // 第一次循环，spellGroups为空
            if (spellGroups.size() != 0) {
                temp = new HashSet<Object>();
                // 取出上次组合与此次集合的字符，并保存
                for (Object s : spellGroups) {
                    for (Object s1 : list.get(i)) {
                        sb.setLength(0);
                        temp.add(sb.append(s).append(s1).toString());
                    }
                }
                // 保存组合数据以便下次循环使用  
                if (temp != null && temp.size() > 0) {
                    spellGroups = temp;  
                }
            } 
            else {  
                for (Object s : list.get(i)) {  
                    spellGroups.add(s);  
                }  
            }  
        }
        List<String> spells = new ArrayList<String>();
        for (Object s : spellGroups) {
            spells.add(s.toString());
        }
        return spells;
    }

}
