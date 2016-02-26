import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Integração de Sistemas
 * Pedro Filipe Dinis Stamm de Matos, 2009116927
 */
public class JoinFilter extends FilterFramework {

    // Define filter input and output ports

    PipedInputStream InputReadPort2 = new PipedInputStream();

    // The following reference to a filter is used because java pipes are able to reliably
    // detect broken pipes on the input port of the filter. This variable will point to
    // the previous filter in the network and when it dies, we know that it has closed its
    // output pipe and will send no more data.

    FilterFramework InputFilter2;

    /**
     * Overriding Connect method
     */
    void Connect(FilterFramework filterA, FilterFramework filterB)
    {
        try
        {
            // Connect this filter's input to the upstream pipe's output stream
            InputReadPort.connect( filterA.OutputWritePort );
            InputFilter = filterA;

            InputReadPort2.connect( filterB.OutputWritePort );
            InputFilter2 = filterB;

        } // try

        catch( Exception Error )
        {
            System.out.println( "\n" + this.getName() + " FilterFramework error connecting::"+ Error );

        } // catch

    } // Connect

    /**
     * Method to read data from second Input pipe
     */

    byte ReadFilterInputPort2() throws EndOfStreamException
    {
        byte datum = 0;

        /***********************************************************************
         * Since delays are possible on upstream filters, we first wait until
         * there is data available on the input port. We check,... if no data is
         * available on the input port we wait for a quarter of a second and check
         * again. Note there is no timeout enforced here at all and if upstream
         * filters are deadlocked, then this can result in infinite waits in this
         * loop. It is necessary to check to see if we are at the end of stream
         * in the wait loop because it is possible that the upstream filter completes
         * while we are waiting. If this happens and we do not check for the end of
         * stream, then we could wait forever on an upstream pipe that is long gone.
         * Unfortunately Java pipes do not throw exceptions when the input pipe is
         * broken.
         ***********************************************************************/

        try
        {
            while (InputReadPort2.available()==0 )
            {
                if (EndOfInputStream2())
                {
                    throw new EndOfStreamException("End of input stream reached");

                } //if

                sleep(250);

            } // while

        } // try

        catch( EndOfStreamException Error )
        {
            throw Error;

        } // catch

        catch( Exception Error )
        {
            System.out.println( "\n" + this.getName() + " Error in read port wait loop::" + Error );

        } // catch

        /***********************************************************************
         * If at least one byte of data is available on the input
         * pipe we can read it. We read and write one byte to and from ports.
         ***********************************************************************/

        try
        {
            datum = (byte)InputReadPort2.read();
            return datum;

        } // try

        catch( Exception Error )
        {
            System.out.println( "\n" + this.getName() + " Pipe read error::" + Error );
            return datum;

        } // catch

    } // ReadFilterPort

    /**
     * Overriding EndOfInputStream method
     */

    private boolean EndOfInputStream2()
    {
        if (InputFilter2.isAlive())
        {
            return false;

        } else {

            return true;

        } // if

    } // EndOfInputStream

    /**
     * Overriding run method
     */

    public void run(){
        int bytesread = 0;					// Number of bytes read from the input file.
        int byteswritten = 0;				// Number of bytes written to the stream.
        int bytesread2 = 0;					// Number of bytes read from the input file.
        int byteswritten2 = 0;				// Number of bytes written to the stream.
        byte databyte = 0;					// The byte of data read from the file

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::Join Reading ");

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
                byteswritten++;

            } // try

            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Join done reading FilterA; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

        while (true)
        {
            /*************************************************************
             *	Here we read a byte and write a byte
             *************************************************************/

            try
            {
                databyte = ReadFilterInputPort2();
                bytesread2++;
                WriteFilterOutputPort(databyte);
                byteswritten2++;

            } // try

            catch (EndOfStreamException e)
            {
                ClosePorts2();
                System.out.print( "\n" + this.getName() + "::Join done reading FilterB; bytes read: " + bytesread2 + " bytes written: " + byteswritten2 );
                break;

            } // catch

        } // while

        bytesread+=bytesread2;
        byteswritten+=byteswritten2;

        System.out.print( "\n" + this.getName() + "::Join Exiting: " + bytesread + " bytes written: " + byteswritten );
    }

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
    void ClosePorts2()
    {
        try
        {
            InputReadPort2.close();
            OutputWritePort.close();

        }
        catch( Exception Error )
        {
            System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

        } // catch

    } // ClosePorts
}
