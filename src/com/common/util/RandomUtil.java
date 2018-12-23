package com.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018/8/22
 * @Description:  类似微信红包
 * 生成的随机数组 如果个数太多可能会生成的总数不等  但生成的总数一定大于输入总数 且总数多出的部分一定会小于平均值
 */
public class RandomUtil {

    private static final float TIMES = 2.1f;   //每个红包最大是平均值的倍数


    /**
     * 判断红包金额是否合法
     * @param money
     * @param count
     * @param MAXMONEY
     * @param MINMONEY
     * @return
     */
    private static boolean isRight(float  money,int count,float MAXMONEY,float MINMONEY)
    {
        double avg = money/count;
        if(avg<MINMONEY){
            return false;
        }
        else if(avg>MAXMONEY)
        {
            return false;
        }
        return true;
    }


    /**
     * 生成一个随机红包  保留四位小数
     * @param money
     * @param mins
     * @param maxs
     * @param count
     * @param MAXMONEY
     * @param MINMONEY
     * @return
     */
    private static float randomRedPacket(float money,float mins,float maxs,int count,float MAXMONEY,float MINMONEY)
    {
        if(count==1)
        {
            return (float)(Math.round(money*10000))/10000;
        }
        if(mins == maxs)
        {
            return mins;//如果最大值和最小值一样，就返回mins
        }
        float max = maxs>money?money:maxs;
        float one = ((float)Math.random()*(max-mins)+mins);
        one = (float)(Math.round(one*10000))/10000;
        float moneyOther = money - one;
        if(isRight(moneyOther,count-1,MAXMONEY,MINMONEY))
        {
            return one;
        }
        else{
            //重新分配
            float avg = moneyOther / (count-1);
            if(avg<MINMONEY)
            {
                return randomRedPacket(money,mins,one,count,MAXMONEY,MINMONEY);
            }else if(avg>MAXMONEY)
            {
                return randomRedPacket(money,one,maxs,count,MAXMONEY,MINMONEY);
            }
        }
        return one;
    }


    /**
     * 生成最终红包数组
     * @param money     总金额
     * @param count     红包个数
     * @param MAXMONEY  最大金额
     * @param MINMONEY  最小金额
     * @param tip       是否一定要数据一致  true是
     * @return
     */
    public static List<Float> splitRedPackets(float money, int count,float MAXMONEY,float MINMONEY,boolean tip) {
        float realMoney=money;
        if(!isRight(money,count,MAXMONEY,MINMONEY))
        {
            return null;
        }
        List<Float> list = new ArrayList<Float>();
        float max = (float)(money*TIMES/count);

        max = max>MAXMONEY?MAXMONEY:max;
        for(int i=0;i<count;i++)
        {
            float one = randomRedPacket(money,MINMONEY,max,count-i,MAXMONEY,MINMONEY);
            list.add(one);
            money-=one;
        }
        BigDecimal big=new BigDecimal("0");
        for(Float f:list){
            big=big.add(new BigDecimal(String.valueOf(f)));
        }
        if(tip&&big.floatValue()-realMoney!=0){
            //如果总数不对再次生成
            System.out.println("总数不对再次调用");
            return splitRedPackets(realMoney, count,MAXMONEY,MINMONEY,true);
        }

        System.out.println("生成总数："+big+" 输入的总数"+realMoney);
        System.out.println(list);

        System.out.println("------------------------------------------------");
        return list;
    }


    public static void main(String[] args) {
            float money=1f;
            int count=900;
            float MAXMONEY=0.1f;
            float MINMONEY=0.001f;

           RandomUtil.splitRedPackets(money, count,MAXMONEY,MINMONEY,false);







    }
}
