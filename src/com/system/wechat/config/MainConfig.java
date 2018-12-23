package com.system.wechat.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.common.wechat.api.WxMpConfigStorage;
import com.common.wechat.api.WxMpInMemoryConfigStorage;
import com.common.wechat.api.WxMpService;
import com.common.wechat.api.impl.WxMpServiceImpl;
import com.system.constant.SystemConstant;



/**
 * Created by FirenzesEagle on 2016/5/30 0030.
 * Email:liumingbo2008@gmail.com
 */
@Configuration
public class MainConfig {

    //填写公众号开发信息

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(SystemConstant.APP_ID);
        configStorage.setSecret(SystemConstant.APP_SECRET);
        configStorage.setToken(SystemConstant.TOKEN);
        configStorage.setAesKey(SystemConstant.AES_KEY);
        configStorage.setPartnerId(SystemConstant.MCH_ID);
        configStorage.setPartnerKey(SystemConstant.MCH_KEY);
        configStorage.setSSLContext(getSSLContext(SystemConstant.MCH_ID));
        return configStorage;
    }
    
    /**
     * 获取证书内容
     * @param mchId
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-23 下午2:28:08
     */
    private SSLContext getSSLContext(String mchId){
        FileInputStream instream = null;
        SSLContext sslContext = null;
        try {
            KeyStore keyStore  = KeyStore.getInstance("PKCS12");
            instream = new FileInputStream(new File(SystemConstant.WEB_REAL_PATH + "/WEB-INF/pk/kx.p12"));
            keyStore.load(instream, mchId.toCharArray());
            // Trust own CA and all self-signed certs
            sslContext = SSLContexts.custom()
                  .loadKeyMaterial(keyStore, mchId.toCharArray())
                  .build();
        } 
        catch (KeyStoreException e) {
            e.printStackTrace();
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        catch (CertificateException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        catch (KeyManagementException e) {
            e.printStackTrace();
        } 
        catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                if(instream != null){
                    instream.close();
                }
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sslContext;
    }

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }

}
