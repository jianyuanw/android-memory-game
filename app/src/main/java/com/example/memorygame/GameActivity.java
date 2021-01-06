package com.example.memorygame;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class GameActivity extends AppCompatActivity {
    boolean isChanged1 = false;
    boolean isChanged11 = false;
    boolean isChanged2 = false;
    boolean isChanged21 = false;
    boolean isChanged3 = false;
    boolean isChanged31 = false;
    boolean isChanged4 = false;
    boolean isChanged41 = false;
    boolean isChanged5 = false;
    boolean isChanged51 = false;
    boolean isChanged6 = false;
    boolean isChanged61 = false;
    boolean isChanged7 = false;
    boolean isChanged71 = false;
    boolean isChanged8 = false;
    boolean isChanged81 = false;
    int id = 0;
    int score = 0;
    ImageView Ims1,Ims11,Ims2,Ims21,Ims3,Ims31,Ims4,Ims41,Ims5,Ims51,Ims6,Ims61,Ims7,Ims71,Ims8,Ims81;
    TextView scores;

    void blank(){

        if (!isChanged1) {
            Ims1.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged11) {
            Ims11.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged2) {
            Ims2.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged21) {
            Ims21.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged3) {
            Ims3.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged31) {
            Ims31.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged4) {
            Ims4.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged41) {
            Ims41.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged5) {
            Ims5.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged51) {
            Ims51.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged6) {
            Ims6.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged61) {
            Ims61.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged7) {
            Ims7.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged71) {
            Ims71.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged8) {
            Ims8.setBackgroundResource(R.drawable.blank);
        }
        if (!isChanged81) {
            Ims81.setBackgroundResource(R.drawable.blank);
        }

    };

    void message(){

        Builder builder  = new Builder(GameActivity.this);
        builder.setTitle("score+1" ) ;
        builder.setMessage("You hava found the same animals" ) ;
        builder.setPositiveButton("YES" ,  null );
        builder.show();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(GameActivity.this , MainActivity.class);
                startActivity(i);
            }
        });


        Ims1 = (ImageView) findViewById(R.id.im1);
        Ims11 = (ImageView) findViewById(R.id.im11);
        Ims2 = (ImageView) findViewById(R.id.im2);
        Ims21 = (ImageView) findViewById(R.id.im21);
        Ims3 = (ImageView) findViewById(R.id.im3);
        Ims31 = (ImageView) findViewById(R.id.im31);
        Ims4 = (ImageView) findViewById(R.id.im4);
        Ims41 = (ImageView) findViewById(R.id.im41);
        Ims5 = (ImageView) findViewById(R.id.im5);
        Ims51 = (ImageView) findViewById(R.id.im51);
        Ims6 = (ImageView) findViewById(R.id.im6);
        Ims61 = (ImageView) findViewById(R.id.im61);
        Ims7 = (ImageView) findViewById(R.id.im7);
        Ims71 = (ImageView) findViewById(R.id.im71);
        Ims8 = (ImageView) findViewById(R.id.im8);
        Ims81 = (ImageView) findViewById(R.id.im81);
        scores=(TextView)findViewById(R.id.score);
        scores.setText( score+" " );

        blank();





        Ims1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims1) {
                    blank() ;
                    if (isChanged1) {
                        Ims1.setBackgroundResource(R.drawable.elephant);
                    } else {
                        Ims1.setBackgroundResource(R.drawable.elephant);

                        if(id==11)
                        {
                            isChanged1 = !isChanged1;
                            isChanged11 = !isChanged11;

                            Ims11.setBackgroundResource(R.drawable.elephant);

                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=1;
                        }
                    }
                }
            }
        });




        Ims11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims11) {
                    blank() ;
                    if (isChanged11) {
                        Ims11.setBackgroundResource(R.drawable.elephant);
                    } else {
                        Ims11.setBackgroundResource(R.drawable.elephant);

                        if(id==1)
                        {
                            isChanged11 = !isChanged11;
                            isChanged1 = !isChanged1;

                            Ims1.setBackgroundResource(R.drawable.elephant);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=11;
                        }
                    }
                }
            }
        });



        Ims2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims2) {
                    blank() ;
                    if (isChanged2) {
                        Ims2.setBackgroundResource(R.drawable.cat);
                    } else {
                        Ims2.setBackgroundResource(R.drawable.cat);

                        if(id==21)
                        {
                            isChanged21 = !isChanged21;
                            isChanged2 = !isChanged2;

                            Ims21.setBackgroundResource(R.drawable.cat);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=2;
                        }
                    }
                }
            }
        });




        Ims21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims21) {
                    blank() ;
                    if (isChanged21) {
                        Ims21.setBackgroundResource(R.drawable.cat);
                    } else {
                        Ims21.setBackgroundResource(R.drawable.cat);

                        if(id==2)
                        {
                            isChanged21 = !isChanged21;
                            isChanged2 = !isChanged2;

                            Ims2.setBackgroundResource(R.drawable.cat);
                            score++;
                            scores.setText( score+" " );
                           // message();
                        }
                        else
                        {
                            id=21;
                        }
                    }
                }
            }
        });


        Ims3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims3) {
                    blank() ;
                    if (isChanged3) {
                        Ims3.setBackgroundResource(R.drawable.rhino);
                    } else {
                        Ims3.setBackgroundResource(R.drawable.rhino);

                        if(id==31)
                        {
                            isChanged31 = !isChanged31;
                            isChanged3 = !isChanged3;

                            Ims31.setBackgroundResource(R.drawable.rhino);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=3;
                        }
                    }
                }
            }
        });




        Ims31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims31) {
                    blank() ;
                    if (isChanged31) {
                        Ims31.setBackgroundResource(R.drawable.rhino);
                    } else {
                        Ims31.setBackgroundResource(R.drawable.rhino);

                        if(id==3)
                        {
                            isChanged31 = !isChanged31;
                            isChanged3 = !isChanged3;

                            Ims3.setBackgroundResource(R.drawable.rhino);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=31;
                        }
                    }
                }
            }
        });



        Ims4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims4) {
                    blank() ;
                    if (isChanged4) {
                        Ims4.setBackgroundResource(R.drawable.lion);
                    } else {
                        Ims4.setBackgroundResource(R.drawable.lion);

                        if(id==41)
                        {
                            isChanged41 = !isChanged41;
                            isChanged4 = !isChanged4;

                            Ims41.setBackgroundResource(R.drawable.lion);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=4;
                        }
                    }
                }
            }
        });




        Ims41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims41) {
                    blank() ;
                    if (isChanged41) {
                        Ims41.setBackgroundResource(R.drawable.lion);
                    } else {
                        Ims41.setBackgroundResource(R.drawable.lion);

                        if(id==4)
                        {
                            isChanged41 = !isChanged41;
                            isChanged4 = !isChanged4;

                            Ims4.setBackgroundResource(R.drawable.lion);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=41;
                        }
                    }
                }
            }
        });


        Ims5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims5) {
                    blank() ;
                    if (isChanged5) {
                        Ims5.setBackgroundResource(R.drawable.pig);
                    } else {
                        Ims5.setBackgroundResource(R.drawable.pig);

                        if(id==51)
                        {
                            isChanged51 = !isChanged51;
                            isChanged5 = !isChanged5;

                            Ims51.setBackgroundResource(R.drawable.pig);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=5;
                        }
                    }
                }
            }
        });




        Ims51.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims51) {
                    blank() ;
                    if (isChanged51) {
                        Ims51.setBackgroundResource(R.drawable.pig);
                    } else {
                        Ims51.setBackgroundResource(R.drawable.pig);

                        if(id==5)
                        {
                            isChanged51 = !isChanged51;
                            isChanged5 = !isChanged5;

                            Ims5.setBackgroundResource(R.drawable.pig);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=51;
                        }
                    }
                }
            }
        });


        Ims6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims6) {
                    blank() ;
                    if (isChanged6) {
                        Ims6.setBackgroundResource(R.drawable.kang);
                    } else {
                        Ims6.setBackgroundResource(R.drawable.kang);

                        if(id==61)
                        {
                            isChanged61 = !isChanged61;
                            isChanged6 = !isChanged6;

                            Ims61.setBackgroundResource(R.drawable.kang);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=6;
                        }
                    }
                }
            }
        });




        Ims61.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims61) {
                    blank() ;
                    if (isChanged61) {
                        Ims61.setBackgroundResource(R.drawable.kang);
                    } else {
                        Ims61.setBackgroundResource(R.drawable.kang);

                        if(id==6)
                        {
                            isChanged61 = !isChanged61;
                            isChanged6 = !isChanged6;

                            Ims6.setBackgroundResource(R.drawable.kang);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=61;
                        }
                    }
                }
            }
        });



        Ims7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims7) {
                    blank() ;
                    if (isChanged7) {
                        Ims7.setBackgroundResource(R.drawable.tiger);
                    } else {
                        Ims7.setBackgroundResource(R.drawable.tiger);

                        if(id==71)
                        {
                            isChanged71 = !isChanged71;
                            isChanged7 = !isChanged7;

                            Ims71.setBackgroundResource(R.drawable.tiger);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=7;
                        }
                    }
                }
            }
        });




        Ims71.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims71) {
                    blank() ;
                    if (isChanged71) {
                        Ims71.setBackgroundResource(R.drawable.tiger);
                    } else {
                        Ims71.setBackgroundResource(R.drawable.tiger);

                        if(id==7)
                        {
                            isChanged71 = !isChanged71;
                            isChanged7 = !isChanged7;

                            Ims7.setBackgroundResource(R.drawable.tiger);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=71;
                        }
                    }
                }
            }
        });

        Ims8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims8) {
                    blank() ;
                    if (isChanged8) {
                        Ims8.setBackgroundResource(R.drawable.wolf);
                    } else {
                        Ims8.setBackgroundResource(R.drawable.wolf);

                        if(id==81)
                        {
                            isChanged81 = !isChanged81;
                            isChanged8 = !isChanged8;

                            Ims81.setBackgroundResource(R.drawable.wolf);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=8;
                        }
                    }
                }
            }
        });




        Ims81.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == Ims81) {
                    blank() ;
                    if (isChanged81) {
                        Ims81.setBackgroundResource(R.drawable.wolf);
                    } else {
                        Ims81.setBackgroundResource(R.drawable.wolf);

                        if(id==8)
                        {
                            isChanged81 = !isChanged81;
                            isChanged8 = !isChanged8;

                            Ims8.setBackgroundResource(R.drawable.wolf);
                            score++;
                            scores.setText( score+" " );
                            //message();
                        }
                        else
                        {
                            id=81;
                        }
                    }
                }
            }
        });




    }

}

