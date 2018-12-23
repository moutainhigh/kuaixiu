package com.common.systemEnum;

/**
 * @Auther: anson
 * @Date: 2018/7/30
 * @Description:电渠系统物流公司编码
 */
public enum ChannelExpress {

    shunfeng("顺丰"),
    zhongtong("中通"),
    yuantong("圆通快递"),
    qita("其他"),
    tiantian("天天快递"),
    yunda("韵达快运"),
    emsguoji("中运全速"),
    zhaijisong("宅急送-山西"),
    gstongyikd("甘肃融溢快递有限公司"),
    ems("EMS");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private ChannelExpress(String name){
        this.name=name;
    }



    public static void main(String[] args) {
        for(ChannelExpress e: ChannelExpress.values()){
            System.out.println(e.toString()+e.getName());
        }
    }

}
