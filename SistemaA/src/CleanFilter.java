/**
 * Created by dbast on 22/02/2016.
 */
public class CleanFilter extends FilterFramework {
    public void run()
    {
        int bytesread = 0;					// Number of bytes read from the input file.
        int byteswritten = 0;				// Number of bytes written to the stream.
        byte databyte = 0;					// The byte of data read from the file
        int id, i;
        int IdLength = 4;				// This is the length of IDs in the byte stream
        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes

        System.out.print( "\n" + this.getName() + "::Cleaning data");

        while (true)
        {
            try
            {
                id=-1;
                for (i=0; i<IdLength; i++ ) {
                    databyte = ReadFilterInputPort();
                    id = id | (databyte & 0xFF);        // We append the byte on to ID...
                } // for
                if (id == 0 || id==2 || id==4) {        // select only the values for timestamp, temperature and altitude
                    if (i != IdLength - 1)                // If this is not the last byte, then slide the
                    {                                    // previously appended byte to the left by one byte
                        id = id << 8;                    // to make room for the next byte we append to the ID
                    } // if
                    bytesread++;
                    WriteFilterOutputPort(databyte);
                    byteswritten++;

                    measurement = 0;
                    for (i=0; i<MeasurementLength; i++ )
                    {
                        databyte = ReadFilterInputPort();
                        measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...
                        if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
                        {												// previously appended byte to the left by one byte
                            measurement = measurement << 8;				// to make room for the next byte we append to the
                            // measurement
                        } // if
                        bytesread++;									// Increment the byte count
                        WriteFilterOutputPort(databyte);
                        byteswritten++;
                    } // for
                } else {
                    for (i=0; i<MeasurementLength; i++ ) {
                        databyte = ReadFilterInputPort();
                    }
                }
            } // try
            catch (FilterFramework.EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Clean Filter Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

    } // run
}
