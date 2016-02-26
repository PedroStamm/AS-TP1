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

        System.out.print( "\n" + this.getName() + "::Cleaning data");

        while (true)
        {
            try
            {
                id=readId(IdLength);
                bytesread+=IdLength;
                if (id != idfilter) {        // select only the values for timestamp, temperature and altitude
                    writeId(id, IdLength);
                    passMeasurement(MeasurementLength);
                    bytesread+=MeasurementLength;
                    byteswritten+=IdLength+MeasurementLength;
                } else {
                    readMeasurement(MeasurementLength);
                    bytesread+=MeasurementLength;
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
