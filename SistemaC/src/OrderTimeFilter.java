import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Integração de Sistemas
 * Pedro Filipe Dinis Stamm de Matos, 2009116927
 */
public class OrderTimeFilter extends FilterFramework {

    void ClosePorts()
    {
        try
        {
            InputReadPort.close();

        }
        catch( Exception Error )
        {
            System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

        } // catch

    } // ClosePorts

    void CloseWritePort()
    {
        try
        {
            OutputWritePort.close();

        }
        catch( Exception Error )
        {
            System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

        } // catch

    } // ClosePorts

    public void run(){
        List frames = new ArrayList<StreamFrame>();
        StreamFrame frame = new StreamFrame();


        int id;
        long measurement;

        int idLength = 4;
        int measurementLength = 8;

        int bytesread=0;
        int byteswritten=0;

        int i;

        System.out.print( "\n" + this.getName() + "::Order Time Reading ");

        while (true) {
            try {
                /***************************************************************************
                 // We know that the first data coming to this filter is going to be an ID and
                 // that it is IdLength long. So we first decommutate the ID bytes.
                 ****************************************************************************/

                id = readId(idLength);
                bytesread += idLength;                        // Increment the byte count


                /****************************************************************************
                 // Here we read measurements. All measurement data is read as a stream of bytes
                 // and stored as a long value. This permits us to do bitwise manipulation that
                 // is neccesary to convert the byte stream into data words. Note that bitwise
                 // manipulation is not permitted on any kind of floating point types in Java.
                 // If the id = 0 then this is a time value and is therefore a long value - no
                 // problem. However, if the id is something other than 0, then the bits in the
                 // long value is really of type double and we need to convert the value using
                 // Double.longBitsToDouble(long val) to do the conversion which is illustrated.
                 // below.
                 *****************************************************************************/

                measurement = readMeasurement(measurementLength);
                bytesread += measurementLength;

                /****************************************************************************
                 // Here we look for an ID of 0 which indicates this is a time measurement.
                 // Every frame begins with an ID of 0, followed by a time stamp which correlates
                 // to the time that each proceeding measurement was recorded. Time is stored
                 // in milliseconds since Epoch. This allows us to use Java's calendar class to
                 // retrieve time and also use text format classes to format the output into
                 // a form humans can read. So this provides great flexibility in terms of
                 // dealing with time arithmetically or for string display purposes. This is
                 // illustrated below.
                 ****************************************************************************/

                if (id == 0) {
                    frame = new StreamFrame();
                    frames.add(frame);
                    Calendar TimeStamp = Calendar.getInstance();
                    TimeStamp.setTimeInMillis(measurement);
                    frame.idTime=true;
                    frame.measurementTime = TimeStamp;
                } // if

                /****************************************************************************
                 // Here we pick up a measurement (ID = 4 in this case), but you can pick up
                 // any measurement you want to. All measurements in the stream are
                 // decommutated by this class. Note that all data measurements are double types
                 // This illustrates how to convert the bits read from the stream into a double
                 // type. Its pretty simple using Double.longBitsToDouble(long value). So here
                 // we print the time stamp and the data associated with the ID we are interested
                 // in.
                 ****************************************************************************/

                else {
                    if (id == 1) {
                        frame.idSpeed = true;
                        frame.measurementSpeed = measurement;
                    } else if (id == 2) {
                        frame.idAltitude = true;
                        frame.measurementAltitude = measurement;
                    } else if (id == 3) {
                        frame.idPressure = true;
                        frame.measurementPressure = measurement;
                    } else if (id == 4) {
                        frame.idTemperature = true;
                        frame.measurementTemperature = measurement;
                    } else if (id == 5) {
                        frame.idPitch = true;
                        frame.measurementPitch = measurement;
                    }
                } // if

            } // try

            /*******************************************************************************
             *	The EndOfStreamExeception below is thrown when you reach end of the input
             *	stream (duh). At this point, the filter ports are closed and a message is
             *	written letting the user know what is going on.
             ********************************************************************************/
            catch (EndOfStreamException e) {
                ClosePorts();
                System.out.print("\n" + this.getName() + "::Order Time done reading; bytes read: " + bytesread);
                break;

            } // catch
        } // while

        Comparator<StreamFrame> c = (s1, s2) -> s1.measurementTime.compareTo(s2.measurementTime);
        frames.sort(c);

        for(i=0; i<frames.size();i++){
            frame= (StreamFrame) frames.get(i);
            try {
                if (frame.idTime) {
                    writeId(0, idLength);
                    writeMeasurement(frame.measurementTime.getTimeInMillis(), measurementLength);
                    byteswritten+=idLength+measurementLength;
                }
                if (frame.idSpeed){
                    writeId(1, idLength);
                    writeMeasurement(frame.measurementSpeed, measurementLength);
                    byteswritten+=idLength+measurementLength;
                }
                if (frame.idAltitude){
                    writeId(2, idLength);
                    writeMeasurement(frame.measurementAltitude, measurementLength);
                    byteswritten+=idLength+measurementLength;
                }
                if (frame.idPressure){
                    writeId(3, idLength);
                    writeMeasurement(frame.measurementPressure, measurementLength);
                    byteswritten+=idLength+measurementLength;
                }
                if (frame.idTemperature){
                    writeId(4, idLength);
                    writeMeasurement(frame.measurementTemperature, measurementLength);
                    byteswritten+=idLength+measurementLength;
                }
                if (frame.idPitch){
                    writeId(5, idLength);
                    writeMeasurement(frame.measurementPitch, measurementLength);
                    byteswritten+=idLength+measurementLength;
                }
            } catch (EndOfStreamException e) {
                e.printStackTrace();
            }
        }
        CloseWritePort();
        System.out.print("\n" + this.getName() + "::Order Time Exiting; bytes read: " + bytesread+ " bytes written: " + byteswritten );
    }
}
