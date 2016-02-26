import java.util.Calendar;

/**
 * Created by Pedro on 25-02-2016.
 */
class StreamFrame {
    boolean idTime = false, idSpeed = false, idAltitude = false,
            idPressure = false, idTemperature = false, idPitch = false;
    Calendar measurementTime;
    long measurementSpeed, measurementAltitude, measurementPressure,
            measurementTemperature, measurementPitch;
}