package com.example.gamedemo1;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private GridView gridView ;
    private int[] imageSrc = {R.drawable.ic_accessibility_black_24dp,R.drawable.ic_accessible_black_24dp,
    R.drawable.ic_airline_seat_recline_extra_black_24dp,R.drawable.ic_directions_bike_black_24dp,
    R.drawable.ic_directions_run_black_24dp,R.drawable.ic_directions_walk_black_24dp,
    R.drawable.ic_wc_black_24dp};
    private int[] itemSrc ;
    private MyAdapter myAdapter ;

    //表格的行数和列数;
    private int colNum = 6 ;
    private int rawNum = 6 ;
    private int total ;
    private int itemType = 5 ;

    private int score ;


    private HandlerThread handlerThread ;

    private Thread computeThread ;

    private Handler computeHandler ;
    private Handler refreshUIHandler ;
    private Message message2child;

    public Handler getMainHandler() {
        return mainHandler;
    }

    private Handler mainHandler ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total = colNum * rawNum ;

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 200:
                        itemSrc = msg.getData().getIntArray("items");
                        myAdapter.notifyDataSetChanged();
                        break;
                    case 201:
                        Log.d(TAG,"main get message 201");
                        int[] delete = msg.getData().getIntArray("items");
                        setInvisible(delete);
                        message2child = computeHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("items",delete);
                        message2child.what = 103 ;
                        computeHandler.sendMessage(message2child);
                        Log.d(TAG,"main send message 103 to child to refresh views");
                        break;
                    case 202:
                        Log.d(TAG,"Parent Get Message 202");
                        refreshItem(msg.getData().getIntArray("items"));
                        myAdapter.notifyDataSetChanged();
                        break;
                    case 204:
                        Log.d(TAG,"main get message 204");
                        int[] nDelete = msg.getData().getIntArray("delete");

                        refreshItem(msg.getData().getIntArray("items"));
                        initVisible();

                        myAdapter.notifyDataSetChanged();

                        setInvisible(nDelete);
                        break;

                }
            }
        };

        computeThread = new ComThread(mainHandler);
        computeThread.start();

        gridView = (GridView) findViewById(R.id.gv);
        gridView.setNumColumns(colNum);
        initData(1);
        myAdapter = new MyAdapter(this,itemSrc);
        gridView.setAdapter(myAdapter);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"click on " + position);
                computeHandler = ComThread.getHandler(1);
                message2child = computeHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("position",position);
                bundle.putIntArray("items",myAdapter.getCloneItemsSet());
                message2child.setData(bundle);
                message2child.what = 101 ;
                computeHandler.sendMessage(message2child);
                Log.d(TAG,"send onclick message 101 to child " );
            }
        });


    }



    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_1:
                initVisible();
                computeHandler = ComThread.getHandler(1);
                message2child = computeHandler.obtainMessage();
                message2child.what = 102;
                computeHandler.sendMessage(message2child);
                Log.d(TAG,"send message 102 to child");
                break;

        }

    }

    private void initData(){

        Log.d(TAG,"init Handler");

            computeHandler = ComThread.getHandler(1);

            if (computeHandler != null) {
                Log.d(TAG, "get Handler1 OK");
                message2child = computeHandler.obtainMessage();
                message2child.what = 102;
                computeHandler.sendMessage(message2child);
                Log.d(TAG,"send message 102 to child");
            }else {
                Log.d(TAG,"Null Handler");
            }

    }


    private void initData(int a){
        itemSrc = new int[total];
        for (int i = 0 ; i < total ;i ++){
            itemSrc[i] = imageSrc[(int) (Math.random()*itemType)];
        }
    }

    private void setInvisible(int[] a){
        for (int i = 0 ; i < a.length ;i++){
            gridView.getChildAt(a[i]).setVisibility(View.GONE);
        }

    }

    private void initVisible(){
        for (int i = 0 ; i < total ; i ++){
            gridView.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }


    public void refreshItem(int[] neI){
        for (int i = 0 ; i < neI.length ; i ++){
            itemSrc[i] = neI[i];
        }
    }







}
