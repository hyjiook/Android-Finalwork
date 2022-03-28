package com.example.finalwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start=findViewById(R.id.start);
        Button contine=findViewById(R.id.contine);
        Button introduce=findViewById(R.id.introduce);

        start.setOnClickListener(new ClickLinster());
        contine.setOnClickListener(new ClickLinster());
        introduce.setOnClickListener(new ClickLinster());
    }
    class ClickLinster implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            if (b.getText().toString().equals("继续游戏")) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GameActivity.class);
                Bundle mybundle = new Bundle();
                mybundle.putInt("type", 3);
                intent.putExtras(mybundle);
                MainActivity.this.startActivity(intent);
            }else if(b.getText().toString().equals("游戏规则")){
                AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(MainActivity.this);
                alertdialogbuilder.setMessage(R.string.about);
                AlertDialog alertdialog=alertdialogbuilder.create();
                alertdialog.show();
            }
            else if(b.getText().toString().equals("开始游戏")){
                showPopMenu(v);
            }
        }
    }
    public void showPopMenu(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(getResources().getStringArray(R.array.level), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GameActivity.class);
                Bundle mybundle = new Bundle();
                mybundle.putInt("type", which);
                intent.putExtras(mybundle);
                MainActivity.this.startActivity(intent);
            }
        });
        builder.show();
    }

}

