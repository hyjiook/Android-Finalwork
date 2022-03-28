package com.example.finalwork;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;

public class GameActivity extends Activity {


    private int type,maxhecheng=2,extra = 0,score = 0;//extra 接受gameview传过来的数据 score 计算得分
    private SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    private HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();//存放声音数据
    private String[] massageList;
    private int[] pictureList = new int[]{R.mipmap.picture0,R.mipmap.picture1,R.mipmap.picture2,R.mipmap.picture3,R.mipmap.picture4,R.mipmap.picture5,R.mipmap.picture6,R.mipmap.picture7,R.mipmap.picture8};
    private ImageView imageView,iv;
    private TextView tvScore,tvBestScore,tv;
    private Button btnNewGame,music,dialogbutton;
    private int Isopen=1;
    private GameView gameView;
    public static final String TYPE = "type";
    public static final String STRING_SCORE = "singleScore";
    public static final String SCORE = "Score";
    public static final String SP_KEY_BEST_SCORE = "bestScore";
    private static GameActivity gameActivity = null;
    public Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            extra=msg.what;
            System.out.println("recall-->"+extra);
            handler.post(runnableUi);
        }
    };
    public static GameActivity getGameActivity() {
        return gameActivity;
    }
    public GameActivity() {
        gameActivity = this;
    }
    protected  void onPause() {
        super.onPause();
        gameView.savedata();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //接收MainActivity选择的按钮数据，判断继续游戏还是开始新游戏
        Intent intent = getIntent();
        Bundle mybundle = intent.getExtras();
        type = mybundle.getInt("type");

        massageList=new Info().getInfo();
        soundMap.put(0, soundPool.load(gameActivity,R.raw.ding, 1));
        gameView = (GameView) findViewById(R.id.gameView);
        if(type==0) saveSingleScore(16);
        else if(type==1) saveSingleScore(8);
        else if(type==2) saveSingleScore(4);
        gameView.setType(type);

        tvScore = (TextView) findViewById(R.id.tvScore);
        tvBestScore = (TextView) findViewById(R.id.tvBestScore);
        music=(Button)findViewById(R.id.music);
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(R.mipmap.p2);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Isopen==1){
                    music.setText("音乐：关");
                    Isopen=0;
                }else{
                    music.setText("音乐：开");
                    Isopen=1;
                }
            }
        });
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//重新开始游戏
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setItems(getResources().getStringArray(R.array.level), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0) saveSingleScore(16);
                        else if(which==1) saveSingleScore(8);
                        else saveSingleScore(4);
                        gameView.setType(which);
                        gameView.startGame();
                    }
                });
                builder.show();
            }
        });
    }
    public void sound(){
        if(Isopen==1)
        this.soundPool.play(soundMap.get(0), 1, 1, 0, 0, 1);
    }
    public void alertDialog(int s){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_d, null);
        dialog.setView(dialogView);
        dialog.show();

        tv = dialogView.findViewById(R.id.word);
        tv.setText(massageList[s]);//数据置入
        iv=dialogView.findViewById(R.id.image);
        iv.setImageResource(pictureList[s]);
        dialogbutton=dialogView.findViewById(R.id.dialog);

        dialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    public void comparehighscore(int maxhecheng){
        if(maxhecheng==2){ imageView.setImageResource(R.mipmap.p2);}
        else  if(maxhecheng==4){imageView.setImageResource(R.mipmap.p4);}
        else  if(maxhecheng==8&&type!=1){ imageView.setImageResource(R.mipmap.p8);alertDialog(0);}
        else  if(maxhecheng==16&&type!=0){ imageView.setImageResource(R.mipmap.p16);alertDialog(1);}
        else  if(maxhecheng==32){ imageView.setImageResource(R.mipmap.p32);alertDialog(2);}
        else  if(maxhecheng==64){imageView.setImageResource(R.mipmap.p64);alertDialog(3);}
        else  if(maxhecheng==128){imageView.setImageResource(R.mipmap.p128);alertDialog(4);}
        else  if(maxhecheng==256){imageView.setImageResource(R.mipmap.p256);alertDialog(5);}
        else  if(maxhecheng==512){imageView.setImageResource(R.mipmap.p512);alertDialog(6);}
        else  if(maxhecheng==1024){imageView.setImageResource(R.mipmap.p1024);alertDialog(7);}
        else  if(maxhecheng==2048){imageView.setImageResource(R.mipmap.p2048);alertDialog(8);}
        else{imageView.setImageResource(R.mipmap.p2);}
    }
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
           if(extra==3){
               score=getScore();
               showScore(score);
               maxhecheng=getSingleScore();
               type=gameView.getType();
               showBestScore(getBestScore());
            }else if(extra==5){
               clearScore();
               maxhecheng=getSingleScore();
               type=gameView.getType();
               showBestScore(getBestScore());
               comparehighscore(maxhecheng);
           }
           else{
               if(extra>maxhecheng){
               maxhecheng=extra;
               System.out.println("maxhecheng-->"+extra);
               comparehighscore(maxhecheng);
                }
                addScore(extra);
            }
        }

    };
    protected void onDestroy() {
        //将线程销毁掉
        handler.removeCallbacks(runnableUi);
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void clearScore(){
        score = 0;
        showScore(score);
    }

    public void addScore(int s){
        score+=s;
        showScore(score);

        int maxScore = Math.max(score, getBestScore());
        saveScore(score);
        saveBestScore(maxScore);
        showBestScore(maxScore);
    }
    //显示数据
    public void showScore(int s){ tvScore.setText(s+""); }
    public void showBestScore(int s){ tvBestScore.setText(s+""); }
    //随时存储游戏数据

    public void saveSingleScore(int s){
        Editor e = getPreferences(MODE_PRIVATE).edit();
        e.putInt(STRING_SCORE, s);
        e.commit();
    }
    public void saveScore(int s){
        Editor e = getPreferences(MODE_PRIVATE).edit();
        e.putInt(SCORE,s);
        e.commit();
    }
    public void saveBestScore(int s){
        Editor e = getPreferences(MODE_PRIVATE).edit();
        if(type==0)  e.putInt("simple", s);
        else if(type==1) e.putInt("commom", s);
        else e.putInt("hard", s);
        e.commit();
    }
    //读取上一次游戏中的数据
    public int getFinalScore(){
        return score;
    }
    public int getSingleScore(){ return getPreferences(MODE_PRIVATE).getInt(STRING_SCORE, 0); }
    public int getBestScore(){
        int best;
        if(type==0)  best=getPreferences(MODE_PRIVATE).getInt("simple", 0);
        else if(type==1) best=getPreferences(MODE_PRIVATE).getInt("commom", 0);
        else best=getPreferences(MODE_PRIVATE).getInt("hard", 0);
        return best; }
    public int getScore(){ return getPreferences(MODE_PRIVATE).getInt(SCORE, 0); }
}
