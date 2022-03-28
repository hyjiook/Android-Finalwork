package com.example.finalwork;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.GridLayout;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.content.Context.MODE_PRIVATE;
import static com.example.finalwork.GameActivity.SP_KEY_BEST_SCORE;

public class GameView extends GridLayout {
    private int type=0;
    private int MSG_SUB_TO_MAIN=0;
    private Card[][] cardsMap = new Card[4][4];
    private List<Point> emptyPoints = new ArrayList<Point>();

    public GameView(Context context) {
        super(context);
        initGameView();
    }
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGameView();
    }
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }
    public void gamescore() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = MSG_SUB_TO_MAIN;
                GameActivity.getGameActivity().handler.sendMessage(message);
            }
        };
        new Thread(runnable).start();
    }
    public void setType(int i){
        this.type=i;
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int cardWidth = (Math.min(w, h) - 20) / 4;
        addCards(cardWidth, cardWidth);
        if(type==3){
            restartGame();
        } else {
           startGame();
        }
    }
    private void initGameView( ) {//初始化
        setColumnCount(4);
        setBackgroundColor(0xffFFE9B2);

        setOnTouchListener(new View.OnTouchListener() {//（startX,startY）开始坐标，offsetX,offsetY偏移量，判断用户意图
            private float startX, startY, offsetX, offsetY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -50) {
                                swipeUp();
                            } else if (offsetX > 50) {
                                swipeDown();
                            }
                        } else {
                            if (offsetY < -50) {
                                swipeLeft();
                            } else if (offsetY > 50) {
                                swipeRight();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void addCards(int cardWidth, int cardHeight) {
        Card c;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
               c = new Card(getContext());
               c.setNum(0);
               addView(c, cardWidth, cardHeight);
               cardsMap[x][y] = c;
            }
        }
    }

    public void startGame() {
        MSG_SUB_TO_MAIN=5;
        gamescore();

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                cardsMap[x][y].setNum(0);
            }
        }
        addRandomNum();
        addRandomNum();
    }
    public void restartGame() {
        MSG_SUB_TO_MAIN=3;
        gamescore();
        SharedPreferences sp = GameActivity.getGameActivity().getSharedPreferences("data", 0);
        setType(sp.getInt("type",0));
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                String key=x+""+y+"";
                int data = sp.getInt(key, 0);
                cardsMap[x][y].setNum(data);
            }
        }
    }
    public int getType(){
        return type;
    }
    public void savedata(){
        SharedPreferences sp = GameActivity.getGameActivity().getSharedPreferences("data", 0);
        SharedPreferences.Editor sharedata = sp.edit();
        sharedata.putInt("type",type);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                String key=x+""+y+"";
                sharedata.putInt(key,cardsMap[x][y].getNum());
            }
        }
        sharedata.commit();
    }
    private void addRandomNum() {
        emptyPoints.clear();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (cardsMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }
        if (emptyPoints.size() > 0) {
            Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size()));
            if(type==0)
                cardsMap[p.x][p.y].setNum(Math.random() > 0.5 ? 8 : 16);
            else if(type==1)
                cardsMap[p.x][p.y].setNum(Math.random() > 0.2 ? 4 : 8);
            else
                cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
        }
    }
    private void swipeLeft() {
        boolean merge = false;
        for(int y=0;y<4;y++){
            for (int x=0; x <4; x++){
                for (int x1 = x + 1; x1 <4; x1++) {
                if (cardsMap[x1][y].getNum() > 0) {
                    if (cardsMap[x][y].getNum() <= 0) {
                        cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                        cardsMap[x1][y].setNum(0);
                        x--;
                        merge = true;
                    } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                        cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                        cardsMap[x1][y].setNum(0);
                        GameActivity.getGameActivity().sound();
                        MSG_SUB_TO_MAIN=cardsMap[x][y].getNum();
                        System.out.println(MSG_SUB_TO_MAIN);
                        gamescore();
                        merge = true;
                    }
                    break;
                }
            }
        }
    }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
}
    private void swipeRight(){
        boolean merge = false;

        for (int y=0; y<4; y++) {
            for (int x=3; x>=0; x--) {
                for (int x1=x-1; x1>=0; x1--) {
                    if (cardsMap[x1][y].getNum()>0) {
                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);
                            x++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            GameActivity.getGameActivity().sound();
                            MSG_SUB_TO_MAIN=cardsMap[x][y].getNum();
                            System.out.println(MSG_SUB_TO_MAIN);
                            gamescore();
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }
    private void swipeUp(){
        boolean merge = false;

        for (int x=0; x<4; x++) {
            for (int y=0; y<4; y++) {
                for (int y1=y+1; y1<4; y1++) {
                    if (cardsMap[x][y1].getNum()>0) {
                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y--;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            GameActivity.getGameActivity().sound();
                            MSG_SUB_TO_MAIN=cardsMap[x][y].getNum();
                            System.out.println(MSG_SUB_TO_MAIN);
                            gamescore();
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }
    private void swipeDown(){
        boolean merge = false;

        for (int x=0; x<4; x++) {
            for (int y=3; y>=0; y--) {
                for (int y1=y-1; y1>=0; y1--) {
                    if (cardsMap[x][y1].getNum()>0) {
                        if (cardsMap[x][y].getNum()<=0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            GameActivity.getGameActivity().sound();
                            MSG_SUB_TO_MAIN=cardsMap[x][y].getNum();
                            System.out.println(MSG_SUB_TO_MAIN);
                            gamescore();
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }
    private void checkComplete(){
        boolean complete = true;
        ALL:
        for (int y=0; y<4; y++) {
            for (int x=0; x<4; x++) {
                if (cardsMap[x][y].getNum()==0||
                        (x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
                        (x<4-1&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
                        (y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<4-1&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {
                    complete = false;
                    break ALL;
                }
            }
        }
        if (complete) {
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束! 您的最终的得分为"+GameActivity.getGameActivity().getFinalScore()+"!").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    androidx.appcompat.app.AlertDialog.Builder builder =
                            new androidx.appcompat.app.AlertDialog.Builder(GameActivity.getGameActivity());
                    builder.setItems(getResources().getStringArray(R.array.level), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setType(which);//难度选择
                            startGame();
                        }
                    });
                    builder.show();
                }
            }).show();
        }
    }
}