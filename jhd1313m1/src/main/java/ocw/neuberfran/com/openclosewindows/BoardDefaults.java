package ocw.neuberfran.com.openclosewindows;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import ocw.neuberfran.com.openclosewindows.R;
import com.google.android.things.pio.PeripheralManagerService;
import java.util.List;


import mraa.mraa;

public class BoardDefaults {
    // returned by Build.device (does not differentiate the carrier boards)
    public static final String DEVICE_EDISON = "edison";
    public static final String DEVICE_JOULE = "joule";

    // determined by this module (includes the carrier board information)
    // note: edison_sparkfun and edison_miniboard use the same busses and gpios
    //       so we don't distinguish between them.
    public static final String DEVICE_EDISON_ARDUINO = "edison_arduino";
    public static final String DEVICE_EDISON_SPARKFUN = "edison_sparkfun";
    public static final String DEVICE_JOULE_TUCHUCK = "joule_tuchuck";
    public static final String DEVICE_NOT_KNOWN = "UNKNOWN";

    private Context context;
    private static Resources res;
    private static String sBoardVariant = "";

    public BoardDefaults(Context applicationContext) {
        this.context = applicationContext;
        res = this.context.getResources();
    }

    public static String getGPIOForLED() {
        switch (getBoardVariant()) {
            case DEVICE_EDISON_ARDUINO:
                return "IO13";
            case DEVICE_EDISON:
                return "GP45";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public  String getGPIOForButton() {
        switch (getBoardVariant()) {
            case DEVICE_EDISON_ARDUINO:
                return "IO12";
            case DEVICE_EDISON:
                return "GP44";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public  static String getBoardVariant() {
        if (!sBoardVariant.isEmpty()) {
            return sBoardVariant;
        }

        // We start with the most generic device description and try to narrow it down.
        sBoardVariant = Build.DEVICE;

        if (sBoardVariant.equals(DEVICE_EDISON)) {
            // For the edison check the pin prefix
            // to always return Edison Breakout pin name when applicable.
            if (mraa.getGpioLookup(res.getString(R.string.GPIO_Edison_Arduino)) != -1)
                sBoardVariant = DEVICE_EDISON_ARDUINO;
            else
                sBoardVariant = DEVICE_EDISON_SPARKFUN;

        } else if (sBoardVariant.equals(DEVICE_JOULE)) {
            sBoardVariant = DEVICE_JOULE_TUCHUCK;

        } else {
            sBoardVariant = DEVICE_NOT_KNOWN;
        }

        return sBoardVariant;
    }
}

