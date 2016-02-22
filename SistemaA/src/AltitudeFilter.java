import java.nio.ByteBuffer;

/**
 * Created by dbast on 22/02/2016.
 */
public class AltitudeFilter extends FilterFramework {
    public void run()
    {
        /************************************************************************************
         *	TimeStamp is used to compute time using java.util's Calendar class.
         * 	TimeStampFormat is used to format the time value so that it can be easily printed
         *	to the terminal.
         *************************************************************************************/


        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
        int IdLength = 4;				// This is the length of IDs in the byte stream

        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream
        byte bytes[];                   // This is an array to hold bytes

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter

        /*************************************************************
         *	First we announce to the world that we are alive...
         **************************************************************/

        System.out.print( "\n" + this.getName() + "::Sink Reading ");

        while (true)
        {
            try
            {
                /***************************************************************************
                 // We know that the first data coming to this filter is going to be an ID and
                 // that it is IdLength long. So we first decommutate the ID bytes.
                 ****************************************************************************/

                id = 0;

                for (i=0; i<IdLength; i++ )
                {
                    databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...
                    bytesread++;						// Increment the byte count
                    id = id | (databyte & 0xFF);		// We append the byte on to ID...
                    if (i != IdLength-1)				// If this is not the last byte, then slide the
                    {									// previously appended byte to the left by one byte
                        id = id << 8;					// to make room for the next byte we append to the ID
                    } // if
                } // for

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
                    }
                    for(i=0; i<MeasurementLength; i++){
                        databyte = ReadFilterInputPort();
                        WriteFilterOutputPort(databyte);
                        bytesread++;
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

                    measurement = feetToMeters(measurement);

                    bytes = toByteArray(id);
                    for(i=0; i<bytes.length; i++){
                        WriteFilterOutputPort(bytes[i]);
                    }
                    bytes = toByteArray(measurement);
                    for(i=0; i<bytes.length; i++){
                        WriteFilterOutputPort(bytes[i]);
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
                System.out.print( "\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesread );
                break;

            } // catch

        } // while

    } // run

    private long feetToMeters(long altitude){
        altitude = (long) (altitude*0.3048);
        return altitude;
    }

    private byte[] toByteArray(int number){
        byte bytes[];
        bytes = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE).putInt(number).array();
        return bytes;
    }

    private byte[] toByteArray(long number){
        byte bytes[];
        bytes = ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(number).array();
        return bytes;
    }
}
