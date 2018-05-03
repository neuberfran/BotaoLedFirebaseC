package ocw.neuberfran.com.openclosewindows;

import android.app.UiAutomation;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import android.app.Activity;


import mraa.mraa;

public class JhdActivity extends Activity {
    private static final String TAG = "JhdActivity";

    private static long intervalBetweenBlinksMs = 1000;

    private static String LedState = "ON";

    private Handler mHandler = new Handler();
    private Handler mbHandler = new Handler();
    private Handler mcHandler = new Handler();
    private Handler mdHandler = new Handler();

    private Gpio mLedGpio;
    private final String BUTTON_GPIO_PIN = "IO12";
    private com.google.android.things.contrib.driver.button.Button mButton;
    private boolean mLedState = false;
    private String mensagem = "ON";

    public DatabaseReference mDatabase;
 //   private DatabaseReference mcDatabase;


    //  private upm_jhd1313m1.Jhd1313m1 lcd;
//    private HandlerThread mDisplayThread;

//    private Runnable mDisplayLoop = this::displayOnLcd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting JhdActivity");

        BoardDefaults bd = new BoardDefaults(this.getApplicationContext());
        int i2cIndex = -1;

        switch (bd.getBoardVariant()) {
            case BoardDefaults.DEVICE_EDISON_ARDUINO:
                i2cIndex = mraa.getI2cLookup(getString(R.string.Lcd_Edison_Arduino));
                break;
            case BoardDefaults.DEVICE_EDISON_SPARKFUN:
                i2cIndex = mraa.getI2cLookup(getString(R.string.Lcd_Edison_Sparkfun));
                break;
            case BoardDefaults.DEVICE_JOULE_TUCHUCK:
                i2cIndex = mraa.getI2cLookup(getString(R.string.Lcd_Joule_Tuchuck));
                break;
            default:
                throw new IllegalStateException("Unknown Board Variant: " + bd.getBoardVariant());
        }

        Log.d(TAG, "i2cIndex = " + i2cIndex);

        mDatabase = FirebaseDatabase.getInstance().getReference();
     //   mcDatabase = FirebaseDatabase.getInstance().getReference();

        //     initializeDoorbellButton();
          getDataInit();
  //      mDisplayThread = new HandlerThread("Display Thread");
  //      mDisplayThread.start();
    //    Handler mDisplayHandler = new Handler(mDisplayThread.getLooper());
 //       mDisplayHandler.post(mDisplayLoop);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Display Loop Destroyed");
    }


    private Runnable mcButtonRunable = new Runnable() {
        @Override
        public void run() {

            Log.d(TAG, "to na mCCButtonRunnable " );

            FirebaseDatabase database = FirebaseDatabase.getInstance();

           final DatabaseReference mDatabase = database.getReference();

/*
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    Action action = dataSnapshot.getValue(Action.class);

                    LedState = action.getMyaction();

                    if (LedState.equalsIgnoreCase("ON")){
                        //   mHandler.post(mBlinkRunnable);
                        inverte = "OFF";
                    }else{
                        inverte = "ON";
                    }

                    mDatabase.setValue(inverte);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

            */

//            mcHandler.postDelayed(mcButtonRunable, intervalBetweenBlinksMs );
        }
    };

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit Runnable if the GPIO is already closed
            if (mLedGpio == null) {
                return;
            }
            try {
                Log.d(TAG, "State set to " + mLedState);

                Log.i(TAG, "mBlinkRunnable   " + LedState);


                if (LedState.equalsIgnoreCase("ON")){
                    //   mHandler.post(mBlinkRunnable);
                    mLedGpio.setValue(true);

                }else{

                    mLedGpio.setValue(false);

                }
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler.postDelayed(mBlinkRunnable, intervalBetweenBlinksMs );
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    private void getDataInit() {
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "volto 1");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference();

                PeripheralManagerService service = new PeripheralManagerService();

                Action action = dataSnapshot.getValue(Action.class);

                Log.d(TAG, "volto 2" );
                LedState = action.getMyaction();
                Log.i(TAG,  LedState);

                try {
                    mButton = new Button(BUTTON_GPIO_PIN,
                            Button.LogicState.PRESSED_WHEN_LOW);

                mButton.setOnButtonEventListener(new Button.OnButtonEventListener() {

                    @Override
                    public void onButtonEvent(Button button, boolean pressed) {
                        if (pressed) {

                            Log.d(TAG, "volto 2.2" + LedState);

                            Log.i(TAG, "volto 2.4" + LedState);

                            if (LedState.equalsIgnoreCase("ON")) {
                                //   mHandler.post(mBlinkRunnable);
                                LedState = "OFF";

                            }
                            else if (LedState.equalsIgnoreCase("OFF")) {
                                //   mHandler.post(mBlinkRunnable);
                                LedState = "ON";

                            }

                            Log.i(TAG, LedState );

                //            Action action = new Action();
                            action.setMyaction(LedState);
                            mDatabase.setValue(action);

                            Log.d(TAG, "volto 3" + LedState);

                            Log.d(TAG, "button pressed tal tal tal tal tal");
                        }
                    }

                });

                } catch (IOException e) {
                    // couldn't configure the button...
                }

                try {
                    String pinName = BoardDefaults.getGPIOForLED();
                    mLedGpio = service.openGpio(pinName);
                    mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

                    Log.i(TAG, "Start blinking LED GPIO pin");

                    Log.i(TAG,  LedState);

                    mHandler.post(mBlinkRunnable);

                } catch (IOException e) {
                    Log.e(TAG, "Error on PeripheralIO API", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());

            }
        };
        mDatabase.addValueEventListener(dataListener);
    }

    private void initializeDoorbellButton() {
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PeripheralManagerService service = new PeripheralManagerService();

                try {
                    mButton = new Button(BUTTON_GPIO_PIN,
                            Button.LogicState.PRESSED_WHEN_LOW);

                    mButton.setOnButtonEventListener(new Button.OnButtonEventListener() {
                        @Override
                        public void onButtonEvent(Button button, boolean pressed) {
                            if (pressed) {

                                mDatabase.setValue("ON");

                            } else {

                                mDatabase.setValue("OFF");

                            }

                            //    putvbutton.setValue("ON");
                            Log.d(TAG, "button pressed tal tal tal tal tal");
                        }

                    });

                } catch (IOException e) {
                    // couldn't configure the button...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());

            }
        };
        mDatabase.addValueEventListener(dataListener);
    }

 }

