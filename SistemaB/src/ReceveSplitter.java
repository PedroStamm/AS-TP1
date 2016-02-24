/**
 * Created by tagow on 24/02/2016.
 */
public class ReceveSplitter extends FilterFramework{

    @Override
    void Connect(FilterFramework Filter){
        try
        {
            // Connect this filter's input to the upstream pipe's output stream

            this.InputReadPort.connect( Filter.OutputWritePort );
            this.InputFilter = Filter;

        } // try

        catch( Exception Error )
        {
            try
            {
                // Connect this filter's input to the upstream pipe's output stream

                this.InputReadPort.connect( Filter.OutputWritePort2 );
                this.InputFilter = Filter;

            } // try

            catch( Exception Error2)
            {
                System.out.println( "\n" + this.getName() + " FilterFramework error connecting::"+ Error2 );

            } // catch

        } // catch
    }

    public void run()
    {


        int bytesread = 0;					// Number of bytes read from the input file.
        int byteswritten = 0;				// Number of bytes written to the stream.
        byte databyte = 0;					// The byte of data read from the file

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::ReceveSplitter Reading ");

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
                System.out.print( "\n" + this.getName() + "::ReceveSplitter Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

    } // run
}
