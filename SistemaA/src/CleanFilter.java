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
                } // if
                WriteFilterOutputPort(databyte);
                byteswritten++;
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
