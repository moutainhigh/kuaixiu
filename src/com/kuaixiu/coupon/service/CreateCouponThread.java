package com.kuaixiu.coupon.service;


import com.kuaixiu.coupon.entity.Coupon;

/**
 * 使用单线程生成优惠码
 *
 * @author Cerehat
 */
public class CreateCouponThread implements Runnable {
    private Coupon c;
    public static int count;
    private CouponService couponService;

    public CreateCouponThread(int count) {
        CreateCouponThread.count = count;
    }

    public CreateCouponThread(Coupon c, CouponService couponService) {
        this.c = c;
        this.couponService = couponService;
    }

    @Override
    public void run() {
        while (count > 0) {
            synchronized (c) {
                if (count > 0) {
                    try {
                        //生成优惠码
                        couponService.saveByNewCode(c);
                    } catch (Exception e) {
                        try {
                            //生成优惠码
                            couponService.saveByNewCode(c);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    count--;
                }
            }
            try {
                Thread.sleep(1);//休息一秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//		for(int i = 0; i < count; i++){
//    		try{
//    			//生成优惠码
//    			couponService.saveByNewCode(c);
//        	}catch(Exception e){
//        		try{
//        			//生成优惠码
//        			couponService.saveByNewCode(c);
//            	}catch(Exception ex){
//            		ex.printStackTrace();
//            	}
//        	}
//    	}
    }

}