/**
 * Created by Pedro on 25-02-2016.
 */
class StreamFrame {
    int idTime;
    byte[] measurementTime = new byte[8];
    byte[] idSpeed = new byte[4];
    byte[] measurementSpeed = new byte[8];
    byte[] idAltitude = new byte[4];
    byte[] measurementAltitude = new byte[8];
    byte[] idPressure = new byte[4];
    byte[] measurementPressure = new byte[8];
    byte[] idTemperature = new byte[4];
    byte[] measurementTemperature = new byte[8];
    byte[] idPitch = new byte[4];
    byte[] measurementPitch = new byte[8];
}