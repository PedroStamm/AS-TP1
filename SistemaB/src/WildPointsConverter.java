/**
 * Created by tagow on 24/02/2016.
 */

public class WildPointsConverter extends FilterFramework {

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
        int IdLength = 4;				// This is the length of IDs in the byte stream
        int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes

        byte databyte = 0;				// This is the data byte read from the stream
        int bytesread = 0;				// This is the number of bytes read from the stream
        int byteswritten = 0;				// Number of bytes written to the stream.

        byte [] intarray;
        byte [] longarray;

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        int id;							// This is the measurement id
        int i;							// This is a loop counter
        int k=0, x=0;

        // Next we write a message to the terminal to let the world know we are alive...

        System.out.print( "\n" + this.getName() + "::WildPoints Converter Reading...");
        StreamFrame[] frame = new StreamFrame[1000];
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
                id=0;
                //Ir ver que id é
                for (i=0; i<IdLength; i++ )
                {
                    databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...
                    id = id | (databyte & 0xFF);		// We append the byte on to ID...
                    if (i != IdLength-1)				// If this is not the last byte, then slide the
                    {									// previously appended byte to the left by one byte
                        id = id << 8;					// to make room for the next byte we append to the ID
                    }
                    bytesread++;						// Increment the byte count
                }

                if(id==0){
                    k++;
                    frame[k] = new StreamFrame();
                    for (i=0; i<IdLength; i++ )
                    {
                        frame[k].idTime = intToByteArray(id);
                        bytesread++;
                    }
                    for (i=0; i<MeasurementLength; i++ )
                    {
                        frame[k].measurementTime[i] = ReadFilterInputPort();
                        bytesread++;
                    }
                }
                if(id==2){
                    for (i=0; i<IdLength; i++ )
                    {
                        frame[k].idAltitude = intToByteArray(id);
                        bytesread++;
                    }
                    for (i=0; i<MeasurementLength; i++ )
                    {
                        frame[k].measurementAltitude[i] = ReadFilterInputPort();
                        bytesread++;
                    }
                }
                if(id==3){
                    for (i=0; i<IdLength; i++ )
                    {
                        frame[k].idPressure = intToByteArray(id);
                        bytesread++;
                    }
                    measurement = 0;
                    for (i=0; i<MeasurementLength; i++ )
                    {
                        frame[k].measurementPressure[i] = ReadFilterInputPort();
                        measurement = measurement | (frame[k].measurementPressure[i] & 0xFF);	// We append the byte on to measurement...

                        if (i != MeasurementLength-1)					// If this is not the last byte, then slide the
                        {												// previously appended byte to the left by one byte
                            measurement = measurement << 8;				// to make room for the next byte we append to the measurement
                        }
                        bytesread++;
                    }
                    if(Double.longBitsToDouble(measurement) > 50.0 && Double.longBitsToDouble(measurement) < 80.0){
                        x=1;
                    } else {
                        ///
                        ///
                        /// ALTERAR PARA MUDAR O VALOR DA PRESSAO UMA VEZ QUE APENAS ESTAO IDENTIFICADOS OS PONTOS SELVAGENS!
                        ///
                        ///
                        frame[k].idPressure = intToByteArray(6);
                        x=1;
                    }
                }
                if(id==4){
                    for (i=0; i<IdLength; i++ )
                    {
                        frame[k].idTemperature = intToByteArray(id);
                        bytesread++;
                    }
                    for (i=0; i<MeasurementLength; i++ )
                    {
                        frame[k].measurementTemperature[i] = ReadFilterInputPort();
                        bytesread++;
                    }

                    if (x==1){
                        for (i=0; i<IdLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].idTime[i]);
                            byteswritten++;
                        }
                        for (i=0; i<MeasurementLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].measurementTime[i]);
                            byteswritten++;
                        }
                        for (i=0; i<IdLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].idAltitude[i]);
                            byteswritten++;
                        }
                        for (i=0; i<MeasurementLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].measurementAltitude[i]);
                            byteswritten++;
                        }
                        for (i=0; i<IdLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].idPressure[i]);
                            byteswritten++;
                        }
                        for (i=0; i<MeasurementLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].measurementPressure[i]);
                            byteswritten++;
                        }
                        for (i=0; i<IdLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].idTemperature[i]);
                            byteswritten++;
                        }
                        for (i=0; i<MeasurementLength; i++ )
                        {
                            WriteFilterOutputPort(frame[k].measurementTemperature[i]);
                            byteswritten++;
                            x=0;
                        }
                    }
                }
            } // try
            catch (EndOfStreamException e)
            {
                ClosePorts();
                System.out.print( "\n" + this.getName() + "::WildPoints Converter Exiting; bytes read: "+ bytesread + " bytes written: " + byteswritten);
                break;

            } // catch
        } // while
    } // run
}
