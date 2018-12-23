package com.common.util;
/**
* @author: anson
* @CreateDate: 2017年11月3日 上午10:26:46
* @version: V 1.0
* 
*/
public class MyABC extends Thread {
    private static Object o = new Object();// 对象锁，必须是static
    private static int count = 0;// 控制输出哪个字母
    private char ID;// 字母
    private int id;// 字母对应的数字
    private int num = 0;// 打印次数
    public MyABC(int id, char ID) {
        this.id = id;
        this.ID = ID;
    }

    @Override
    public void run() {
        synchronized (o) {
            while (num < 10) {
                if (count % 3 == id) {
                    System.out.print(ID);
                    count++;
                    num++;

                    // 唤醒所有等待线程
                    o.notifyAll();
                } else {
                    try {
                        // 如果不满足条件，则阻塞等待
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        (new MyABC(0, 'A')).start();
        Thread.sleep(10);
        (new MyABC(1, 'B')).start();
        Thread.sleep(10);
        (new MyABC(2, 'C')).start();
        Thread.sleep(10);
    }
}
