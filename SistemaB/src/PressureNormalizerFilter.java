import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pedro on 25-02-2016.
 */
public class PressureNormalizerFilter extends FilterFramework {

    public void run() {
        List frameList = new LinkedList<StreamFrame>();
        StreamFrame frameRead;

        int idLength = 4;
        int MeasurementLength = 8;

        byte databyte;
        byte[] bytes;

        int bytesread = 0;
        int byteswritten = 0;

        int id;
        long measurement;

        int i;
        System.out.print( "\n" + this.getName() + "::Altitude Reading ");

        while (true)
        {
            try {
                /***************************************************************************
                 // We know that the first data coming to this filter is going to be an ID and
                 // that it is IdLength long. So we first decommutate the ID bytes.
                 ****************************************************************************/

                id = readId(idLength);
                bytesread+=idLength;

                if(id==0){
                    frameRead = new StreamFrame();
                    frameRead.idTime = toByteArray(0);
                }

                /****************************************************************************
                 // Here we look for an ID of 2 which indicates this is an altitude measurement.
                 // If the ID is not 2, the measurement is passed along as is.
                 // If it is 2, the altitude will be converted to meters and then be sent to
                 // the next filter.
                 ****************************************************************************/

                if ( id !=2 )
                {
                    bytes = toByteArray(id);
                    for(i=0; i<bytes.length; i++){
                        WriteFilterOutputPort(bytes[i]);
                        byteswritten++;
                    }
                    for(i=0; i<MeasurementLength; i++){
                        databyte = ReadFilterInputPort();
                        WriteFilterOutputPort(databyte);
                        bytesread++;
                        byteswritten++;
                    }

                }  else {
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

                    measurement = 0;

                    for (i=0; i<MeasurementLength; i++ ){
                        databyte = ReadFilterInputPort();
                        measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...
                        bytesread++;

                        if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
                        {												// previously appended byte to the left by one byte
                            measurement = measurement << 8;				// to make room for the next byte we append to the
                            // measurement
                        } // if

                    } // for

                    bytes = toByteArray(id);
                    for(i=0; i<bytes.length; i++){
                        WriteFilterOutputPort(bytes[i]);
                        byteswritten++;
                    }
                    bytes = toByteArray(measurement);
                    for(i=0; i<bytes.length; i++){
                        WriteFilterOutputPort(bytes[i]);
                        byteswritten++;
                    }

                } // if else

            } // try

            /*******************************************************************************
             *	The EndOfStreamException below is thrown when you reach end of the input
             *	stream (duh). At this point, the filter ports are closed and a message is
             *	written letting the user know what is going on.
             ********************************************************************************/

            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Altitude Exiting; bytes read: " + bytesread + "; bytes written: " + byteswritten );
                break;

            } // catch

        } // while

    } // run

}
