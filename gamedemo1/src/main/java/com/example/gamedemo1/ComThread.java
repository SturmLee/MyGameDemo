package com.example.gamedemo1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.TransactionTooLargeException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by stonegroup on 2016/10/31.
 */

public class ComThread extends Thread {

    private final static String TAG = "MainComThread";

    private int[] imageSrc = {R.drawable.ic_accessibility_black_24dp,R.drawable.ic_accessible_black_24dp,
            R.drawable.ic_airline_seat_recline_extra_black_24dp,R.drawable.ic_directions_bike_black_24dp,
            R.drawable.ic_directions_run_black_24dp,R.drawable.ic_directions_walk_black_24dp,
            R.drawable.ic_wc_black_24dp};
    private int imgType = 5 ;

    public static Handler comHandler ;
    public static Handler actHandler ;

    private Handler parentHandler ;

    public Message message ;

    private int[] items ;
    private int[] itemsClone;
    private int itemLen = 36 ;

    private int pos1 = -1 ;
    private int pos2 = -1;


    public int col = 6 ;
    public int raw = 6 ;

    public ComThread(Handler parHandler){
        parentHandler = parHandler ;

    }

    public static Handler getHandler(int a){
        if (a == 1) {

                if (comHandler != null) {
                    Log.d(TAG, "getHandler");
                    return comHandler;
                }

        }else {

                if (actHandler != null) {
                    Log.d(TAG,"get Handler2");
                    return actHandler;
                }

        }
        Log.d(TAG,"nulllllllll!!!");
       return null;
    }



    @Override
    public void run() {

        Looper.prepare();
        comHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 101:
                        Log.d(TAG,"child get message 101");
                        int pos = msg.getData().getInt("position");
                        items = msg.getData().getIntArray("items");
                        changePosition(pos);
                        break;
                    case 102:
                        Log.d(TAG,"get Message 102");
                        items = getHardItem();
                        notifyView(202,items);
                        break;
                    case 103:
                        Log.d(TAG,"child get message 103 ");
//                        int[] delete = msg.getData().getIntArray("items");
                        getFallItem(itemsClone);
                        break;

                }
            }
        };
        Looper.loop();
    }


    public int getRandom(int a){
        return (int) (Math.random()*a);
    }


    public int[] getHardItem(){
        int[] neIt = new int[itemLen];
        boolean isGoon = false ;
        do{
            neIt = getNewItems();
            isGoon = checkWholeItems(neIt);
        }while (isGoon);
        return neIt;
    }

    public int[] getNewItems(){
        int[] newItem = new int[itemLen];
        for (int i = 0 ; i < itemLen ;i ++){
            newItem[i] = imageSrc[getRandom(imgType)];
        }
        return newItem;
    }

    public void restoreItems(int[] delete){
        int[] aX = new int[delete.length];
        int[] aY = new int[delete.length];

        int[] newItem = new int[itemLen];

        for (int i = 0 ; i < delete.length; i++){
            aX[i] = getCoordinate(delete[i])[0];
            aY[i] = getCoordinate(delete[i])[1];
        }

        int[] neCol = new int[raw];
        int[] colCount = new int[col];
        for (int i = 0 ; i < col ; i++){
            for (int j = 0 ; j < aY.length;j++){
                if (aY[j] == i ){
                    colCount[i]++;
                }
            }
        }

        for (int i = 0 ; i < col ; i ++ ){
            for (int j = 0 ; j < raw ; j++){

            }
        }



    }

    public boolean cheArray(int a ,int[] b){
        boolean isIn  = false ;
        for (int i = 0 ; i < b.length ; i ++){
            if (a == b[i]){
                isIn = true ;
            }
        }
        return isIn;
    }

    //交换位置
    public void changePosition(int position){

        pos1 = position ;

        checkPoint(position);

        Log.d(TAG,"开始判定相邻");
        if (pos2 != -1) {
            Log.d(TAG,"不是第一次点击");
            if (checkNear(pos1, pos2)) {
                Log.d(TAG,"相邻");
                Log.d(TAG,pos1+">>>>>"+pos2);

                changeItems(pos1,pos2);
                notifyView(200,items);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (checkPoint(pos1) || checkPoint(pos2)){
                    Log.d(TAG,"消除中");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "不可以消除");
                    changeItems(pos1, pos2);
                    notifyView(200,items);
                }
            }
        }
        pos2 = position ;

    }






    public boolean checkNear(int a ,int b){

        int[] aC = getCoordinate(a);
        int[] bC = getCoordinate(b);

        return Math.abs(aC[0]-bC[0])+Math.abs(aC[1]-bC[1]) == 1;
    }

    //获得在表格的坐标
    public int[] getCoordinate(int a){
        return new int[]{a / col,a % col};
    }

    public int getPosition(int[] aa){
        return aa[0]*col+aa[1];
    }


    public void changeItems(int from,int to){
        int temp = items[to];
        items[to] = items[from];
        items[from] = temp;
        //刷新视图!!!!!!!!!!!!!
    }




    public boolean checkWholeItems(int[] whoIt){
        boolean can = false ;
        int tempX = 0 ;
        int tempY = 0 ;
        //存储消除信息
        //遍历行看是否有消除
        for (int i = 0 ; i < raw ; i++){
            for (int j = 0 ; j < col -1 ; j++){
                if (whoIt[i*col+j + 1] == whoIt[i*col+j]) {
                    tempX++;
                    if (tempX >= 2) {
                        can = true ;
                    }
                }else {
                    tempX = 0 ;
                }
            }
        }

        //遍历列
        for (int i = 0 ; i < col ; i++){
            for (int j = 0 ; j < raw - 1  ; j++){
                if (whoIt[j*col + i + 1] == whoIt[j*col + i]) {
                    tempY++;
                    if (tempY >= 2) {
                        can = true ;
                    }
                }else {
                    tempY = 0 ;
                }
            }
        }
        return can;
    }





    //判断以某个位点为中心的十字形区域是否可以消除
    public boolean checkPoint(int a){

        boolean can = false ;

        int[] aC = getCoordinate(a);

        int rawX = aC[0] ;
        int colY = aC[1] ;
        int xStart = Math.max(0,colY-2);
        int xEnd = Math.min(col-1,colY+2);
        int x = xEnd - xStart +1 ;
        int xS = colY - xStart  ;
        int yStart = Math.max(0,rawX-2);
        int yEnd = Math.min(raw - 1,rawX+2);
        int y = yEnd - yStart  + 1 ;
        int yS = rawX - yStart ;

        int tempX = 0 ;
        int tempY = 0 ;
        int xNum = 0 ;
        int x_end = 0 ;
        int yNum = 0 ;
        int y_end = 0 ;

        int[] deleteSet ;

        Log.d(TAG,"X lines : from " + xStart + " to " + xEnd + "\n");
        Log.d(TAG,"Y lines : from " + yStart + " to " + yEnd + "\n");

        Log.d(TAG,"\n##############xxxxxxxxxxxx");
        for (int i = 0 ; i < x -1   ; i ++) {
            Log.d(TAG,"position: "+ (a - xS + i) +"  RowX: "+(xStart+i)+ "  tempX: "+tempX);

            if (items[a - xS + i] == items[a - xS + 1 +i]) {
                tempX++;
                if (tempX >= 2) {
                    can = true;
                    Log.d(TAG,"X十字判断通过");
                    xNum = tempX + 1;
                    x_end = a - xS + 1 +i;
                }
            }else {
                tempX = 0 ;
            }
        }
        Log.d(TAG," X消除end" + x_end);

        Log.d(TAG,"\n##################yyyyyyyyyyyyyy");
        for (int i = 0 ; i < y -1   ; i++){
            Log.d(TAG,"position: "+ (a-(yS-i)*col) +"  RowY: "+(yStart+i)+ "  tempY: "+tempY);

            if (items[a-(yS-i)*col] == items[a-(yS-i-1)*col]){
                tempY++;
                if (tempY >= 2){
                    can = true ;
                    Log.d(TAG,"Y十字判断通过");
                    yNum = tempY + 1 ;
                    y_end = a-(yS-i-1)*col ;
                }
            }else {
                tempY = 0 ;
            }
        }
        Log.d(TAG," y消除end " + y_end);


        //获取待消除的项
        if (can){
            int flag = 0 ;
            int len = xNum + yNum ;
            int[] deleteSet0 = new int[xNum + yNum ];
            if (xNum!=0) {
                for (int i = 0; i < xNum; i++) {
                    deleteSet0[i] = x_end - xNum + i + 1;
                    flag++;
                    Log.d(TAG,"消除xxx" + (x_end - xNum + i + 1));
                }
            }
            if (yNum!=0) {
                for (int i = 0  ; i < yNum; i++) {
                    if (cheArray(y_end - yNum * col + i * col,deleteSet0)) {
                        len--;
                        deleteSet0[xNum + i] = -1 ;
                    }
                    deleteSet0[xNum + i] = y_end - yNum * col + (i+1) * col;
                    flag++;
                    Log.d(TAG,"消除yyy "+(y_end - yNum * col + (i+1) * col));

                }
            }

            Log.d(TAG,"一共有可以消除个数"+flag);
            deleteSet = new int[flag];
            for (int i = 0 ; i < flag ; i ++){
                if (deleteSet0[i] == -1) {
                    deleteSet[i] = deleteSet0[flag];
                    Log.d(TAG, "位置 " + deleteSet[i]);
                }else {
                    deleteSet[i] = deleteSet0[i];
                    Log.d(TAG, "位置 " + deleteSet[i]);
                }
            }

            itemsClone = items ;
            for (int i = 0 ; i < deleteSet.length ; i ++ ){
                itemsClone[deleteSet[i]] = 0 ;
            }

            notifyView(201,deleteSet);

        }



        return can;
    }

    //获得下落后的视图
    public void getFallItem(int[] item){
        int po = 0 ;
        int k = 0 ;
        for (int i = 0 ; i < col ; i ++){ //列
            for (int j = raw - 1 ; j >= 0 ; j --){  //行
                k = 0 ;
                if (item[getPosition(new int[]{j,i})] == 0 ){
                    do {
                        k++;
                    }while (getPosition(new int[]{j-k,i}) >= 0 &&item[getPosition(new int[]{j-k,i})] == 0 );
                   if (getPosition(new int[]{j-k,i}) >= 0){
                       item[getPosition(new int[]{j,i})] = item[getPosition(new int[]{j-k,i})];
                       item[getPosition(new int[]{j-k,i})] = 0 ;
                   }else {
                       item[getPosition(new int[]{j,i})] = imageSrc[getRandom(imgType)];
                   }

                }
            }
        }
        int flag = 0 ;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0 ; i < itemLen ; i ++){

            if (item[i] == 0){
                list.add(i);
                flag++;
                continue;
            }

            items[i] = item[i];
        }
        int[] newDelete = new int[flag];
        for (int j = 0 ;j < flag ; j ++){
            newDelete[j] = list.get(j);
            Log.d(TAG,"~~~~~~"+newDelete[j]);
        }

        Bundle bundle = new Bundle();
        bundle.putIntArray("items",items);
        bundle.putIntArray("delete",newDelete);

        notifyView(204,bundle);


    }




    //判断是否可解，交换后是否可消除
    public boolean preCheck(){
        boolean result = false ;

        //行交换遍历
        for (int i = 0 ; i < raw ;i++ ){
            for (int j = 0 ; j < col - 1 ; j ++ ){
                changeItems(i*col+j ,i*col+j+1);

                if (checkPoint(i*col+j ) || checkPoint(i*col+j+1)){
                    result = true ;
                    changeItems(i*col+j ,i*col+j+1);
                    return result;
                }

            }
        }

        for (int i = 0 ; i < col  ; i ++ ){
            for (int j = 0 ; j < raw - 1 ; j ++){
                changeItems(j*col+i ,(j+1)*col+i );
                if (checkPoint(j*col+i ) || checkPoint((j+1)*col+i )){
                    result = true ;
                    changeItems(j*col+i ,(j+1)*col+i );
                    return result;
                }
            }
        }

        return result;
    }


    //发送消息提示刷新视图
    public void notifyView(int what,int[] a){
        message = parentHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putIntArray("items",a);
        message.setData(b);
        message.what = what;
        parentHandler.sendMessage(message);
        Log.d(TAG,"Child send Message "+what+" to Parent");
    }

    public void notifyView(int what,Bundle bundle){
        message = parentHandler.obtainMessage();
        message.setData(bundle);
        message.what = what;
        parentHandler.sendMessage(message);
        Log.d(TAG,"Child send Message "+what+" to Parent");
    }





}
