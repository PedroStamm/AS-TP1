/**
 * Created by Pedro on 25-02-2016.
 */
class StreamFrame {
    byte[] idDatabytes = new byte[4];
    byte[] idTime = new byte[4];
    byte[] measurementTime = new byte[8];
    byte[] idAltitude = new byte[4];
    byte[] measurementAltitude = new byte[8];
    byte[] idPressure = new byte[4];
    byte[] measurementPressure = new byte[8];
    byte[] idTemperature = new byte[4];
    byte[] measurementTemperature = new byte[8];
}