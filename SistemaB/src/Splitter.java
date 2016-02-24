/**
 * Created by tagow on 24/02/2016.
 */
public class Splitter extends FilterFramework {

    public void run()
    {


        int bytesread = 0;					// Number of bytes read from the input file.
        int byteswritten = 0;				// Number of bytes written to the stream.
        byte databyte = 0;					// The byte of data read from the file

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::Splitter Reading ");

        while (true)
        {
            /*************************************************************
             *	Here we read a byte and write a byte
             *************************************************************/

            try
            {
                databyte = ReadFilterInputPort();
                bytesread++;
                WriteFilterOutputPort(databyte);
                WriteFilterOutputPort2(databyte);
                byteswritten++;

            } // try

            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Splitter Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

    } // run

    void WriteFilterOutputPort2(byte datum)
    {
        try
        {
            OutputWritePort2.write((int) datum );
            OutputWritePort2.flush();

        } // try

        catch( Exception Error )
        {
            System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

        } // catch

        return;

    }
}
