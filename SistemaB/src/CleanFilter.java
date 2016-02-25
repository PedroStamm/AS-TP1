import java.nio.ByteBuffer;

/**
 * Created by dbast on 22/02/2016.
 */
public class CleanFilter extends FilterFramework {

    private int idfilter;

    public CleanFilter(int id){
        this.idfilter = id;
    }

    public void run()
    {
        int bytesread = 0;					// Number of bytes read from the input file.
        int byteswritten = 0;				// Number of bytes written to the stream.
        byte databyte = 0;					// The byte of data read from the file
        int id, i;
        int IdLength = 4;				// This is the length of IDs in the byte stream
        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes

        System.out.print( "\n" + this.getName() + "::Cleaning Filter Reading...");

        while (true)
        {
            try
            {
                id=0;
                for (i=0; i<IdLength; i++ ) {
                    databyte = ReadFilterInputPort();
                    id = id | (databyte & 0xFF);        // We append the byte on to ID...
                    if (i != IdLength - 1)                // If this is not the last byte, then slide the
                    {                                    // previously appended byte to the left by one byte
                        id = id << 8;                    // to make room for the next byte we append to the ID
                    } // if
                    bytesread++;
                } // for
                if (id != idfilter) {        // select only the values for filtering out
                    byte[] bytes = ByteBuffer.allocate(4).putInt(id).array(); // transform id to bytes
                    for (i=0;i<bytes.length;i++)
                    {
                        WriteFilterOutputPort(bytes[i]); // write id to output
                        byteswritten++;
                    }

                    for (i=0; i<MeasurementLength; i++ )
                    {
                        databyte = ReadFilterInputPort();
                        bytesread++;									// Increment the byte count
                        WriteFilterOutputPort(databyte);                // write data to output
                        byteswritten++;
                    } // for
                } else {
                    for (i=0; i<MeasurementLength; i++ ) {
                        databyte = ReadFilterInputPort();
                        bytesread++;
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
