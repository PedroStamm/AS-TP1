/**
 * Created by dbast on 22/02/2016.
 */
public class TemperatureFilter extends FilterFramework {

    public byte[] longToByteArray(long value) {
        return new byte[] {
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public byte[] intToByteArray(int value){
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public void run()
    {
        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
        int IdLength = 4;				// This is the length of IDs in the byte stream

        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream
        int byteswritten = 0;				// Number of bytes written to the stream.

        byte [] intarray;
        byte [] longarray;

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter



        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::Middle Reading ");

        while (true)
        {
            /*************************************************************
             *	Here we read a byte and write a byte
             *************************************************************/

            try
            {
                /***************************************************************************
                 // We know that the first data coming to this filter is going to be an ID and
                 // that it is IdLength long. So we first decommutate the ID bytes.
                 ****************************************************************************/

                id = 0;

                //Ir ver que id é
                for (i=0; i<IdLength; i++ )
                {

                    databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...

                    id = id | (databyte & 0xFF);		// We append the byte on to ID...

                    if (i != IdLength-1)				// If this is not the last byte, then slide the
                    {									// previously appended byte to the left by one byte
                        id = id << 8;					// to make room for the next byte we append to the ID

                    } // if

                    bytesread++;						// Increment the byte count

                }


                if(id == 4){
                    //Se for Id de temperatura, buscar medida
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

                    }

                    //Foruma de conversao (F  -  32)  x  5/9 = C
                    measurement = (measurement - 32) * 5 / 9;

                    intarray = intToByteArray(id);
                    longarray = longToByteArray(measurement);

                    for(i=0;i<intarray.length;i++){
                        WriteFilterOutputPort(intarray[i]);
                    }
                    for(i=0;i<longarray.length;i++){
                        WriteFilterOutputPort(longarray[i]);
                    }

                }
                else{
                    //Converte o id novamente e envia para o output

                    intarray = intToByteArray(id);
                    for(i=0;i<intarray.length;i++){
                        WriteFilterOutputPort(intarray[i]);
                    }

                    for (i=0; i<MeasurementLength; i++ )
                    {
                        databyte = ReadFilterInputPort();
                        WriteFilterOutputPort(databyte);
                    }
                }

            } // try

            catch (FilterFramework.EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::Temperature Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
                break;

            } // catch

        } // while

    } // run
}
