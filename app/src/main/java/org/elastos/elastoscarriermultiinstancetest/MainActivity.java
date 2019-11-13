package org.elastos.elastoscarriermultiinstancetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.CarrierException;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textView ;
    String basePath = "" ;
    int index = 0 ;
    ArrayList<Carrier> carriers = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Sampler.getInstance().init(getApplicationContext(), 100L , "/sdcard/info.txt");
//        Sampler.getInstance().start();
        carriers = new ArrayList<>();
        basePath = this.getFilesDir().getParent()+ File.separator+"test";

        findViewById(R.id.create1instance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create1Instance();
            }
        });

        findViewById(R.id.create10instance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create10Instance();
            }
        });

        textView = findViewById(R.id.textView);
        setText();
    }
    
    private void create1Instance(){
        index++;
        Carrier carrier = null;
        CarrierOptions options = new CarrierOptions(basePath+File.separator+index);
        CarrierHandler carrierHandler = new CarrierHandler();
        try {
            carrier = new Carrier(options,carrierHandler);
            carrier.start(0);
        } catch (CarrierException e) {
            e.printStackTrace();
        }
        carriers.add(carrier);

        setText();
    }

    private void create10Instance(){
        for (int i = 0 ; i< 10 ; i++){
            create1Instance();
        }
    }

    @Override
    protected void onDestroy() {
        for (Carrier carrier : carriers){
            carrier.kill();
        }
        carriers.clear();
        index = 0;
        super.onDestroy();
    }

    private void setText(){
        textView.setText("Current Carrier instance count is "+index);
    }
}
