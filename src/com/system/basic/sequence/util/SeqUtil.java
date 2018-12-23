package com.system.basic.sequence.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.system.basic.sequence.service.SequenceService;

/**
 * 序列工具类.
 * 
 * @CreateDate: 2016-9-3 下午11:20:20
 * @version: V 1.0
 */
public class SeqUtil {
    
    private static SeqUtil seqUtil = new SeqUtil(); 
    
    @Autowired
    private SequenceService sequenceService;
    
    public SequenceService getSequenceService() {
        return sequenceService;
    }

    /**
     * 
     * @param sequenceService
     * @CreateDate: 2016-9-4 上午12:11:01
     */
    public void setSequenceService(SequenceService sequenceService) {
        System.out.println("SeqUtil init start ...");
        this.sequenceService = sequenceService;
        seqUtil.sequenceService = sequenceService;
        System.out.println("SeqUtil init end");
    }

    /**
     * 根据key查询当前序列
     * @param key
     * @return
     * @CreateDate: 2016-9-3 下午11:34:23
     */
    public static String getNext(String key){
        return getNext(key, null);
    }
    
    /**
     * 根据key和类型查询当前序列
     * @param key
     * @param type
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-3 下午11:34:04
     */
    public static String getNext(String key, String type){
        return seqUtil.sequenceService.getNext(key, type);
    }
}
