package com.example.widget;

import android.content.Context;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ex-huangzhiyi001 on 2019/1/9.
 */
public class Utils {

    public static int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void main(String[] args){
//        LinkedList<Integer> linkedList = new LinkedList<>();
//        for (int i = 0; i < 20; i++){
//            linkedList.add(i);
//        }
        LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 4; i++){
            linkedBlockingQueue.add(i);
        }
        try{

            System.out.println(linkedBlockingQueue.take());
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        try{

            System.out.println(linkedBlockingQueue.take());
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        try{

            System.out.println(linkedBlockingQueue.take());
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        try{

            System.out.println(" "+linkedBlockingQueue.take());
        } catch (InterruptedException e){
            e.printStackTrace();
            System.out.println("err");
        }
    }

}
