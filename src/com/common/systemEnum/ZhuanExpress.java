package com.common.systemEnum;

/**
 * @Auther: anson
 * @Date: 2018/7/31
 * @Description:转转系统物流公司编码
 */
public enum ZhuanExpress {

    shunfeng("顺丰"),
    yuantong("圆通"),
    zhongtong("中通"),
    yunda("韵达"),
    shentong("申通"),
    tiantian("天天快递"),
    huitong("汇通"),
    ems("EMS"),
    jd("京东快递"),
    unknow("未知快递");



    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private ZhuanExpress(String name){
        this.name=name;
    }



    public static void main(String[] args) {
        for(ZhuanExpress e: ZhuanExpress.values()){
            System.out.println(e.toString()+e.getName());
        }
    }

}
