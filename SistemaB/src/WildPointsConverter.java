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

        long measurement;				// This is the word used to store all measurements - conversions are illustrated.
        long lastValidMeasurement = 0;
        long nextValidMeasurement = 0;
        long average = 0;
        int id;							// This is the measurement id
        int i, j;							// This is a loop counter
        int k=0, actualFrame=1000;
        boolean valid=false;

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
                for (i=0; i<IdLength; i++ )
                {
                    databyte = ReadFilterInputPort();	// This is where we read the byte from the stream...
                    id = id | (databyte & 0xFF);		// We append the byte on to ID...
                    if (i != IdLength-1)				// If this is not the last byte, then slide the
                    {									// previously appended byte to the left by one byte
                        id = id << 8;					// to make room for the next byte we append to the ID
                    }
                    bytesread++;
                }

                if(id==0){
                    k++;
                    frame[k] = new StreamFrame();
                    frame[k].idTime = intToByteArray(id);
                    bytesread = bytesread + readFromPipe(frame[k].measurementTime, MeasurementLength);
                }
                if(id==2){
                    frame[k].idAltitude = intToByteArray(id);
                    bytesread = bytesread + readFromPipe(frame[k].measurementAltitude, MeasurementLength);
                }
                if(id==3){
                    frame[k].idPressure = intToByteArray(id);

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
                        if(valid==false) {
                            if (lastValidMeasurement != 0) {
                                System.out.println("lastValidMeasurement != 0 e k="+k);
                                nextValidMeasurement = measurement;
                                average = (lastValidMeasurement+nextValidMeasurement)/2;
                                frame[actualFrame].measurementPressure = longToByteArray(average);
                                actualFrame = k;
                            } else {
                                System.out.println("lastValidMeasurement == 0 e k="+k);
                                //Percorrer Array e colocar valor de measurement
                                //no valor de pressure (e mudar id)
                                for (i=actualFrame; i<k; i++)
                                {
                                    frame[i].measurementPressure = longToByteArray(measurement);
                                }
                            }
                        }
                        lastValidMeasurement = measurement;
                        valid=true;
                    } else {
                        if (k<=actualFrame)
                        {
                            actualFrame = k;
                        }
                        frame[k].idPressure = intToByteArray(6);
                        valid=false;
                        if (lastValidMeasurement != 0) {
                            //System.out.println("INVALID NUMBER lastValidMeasurement != 0 e frameactual= "+actualFrame+" k="+k);
                            frame[k].measurementPressure = longToByteArray(lastValidMeasurement);
                            actualFrame = k;
                            valid=true;
                        }
                    }
                }
                if(id==4){
                    frame[k].idTemperature = intToByteArray(id);
                    bytesread = bytesread + readFromPipe(frame[k].measurementTemperature, MeasurementLength);

                    if (valid==true){
                        //System.out.println("X=0 Frame actual: " + actualFrame +" k=" + k);
                        for (j = actualFrame; j < k; j++) {
                            //System.out.println("ELSE X=0 Frame actual: " + actualFrame + " j=" + j + " k=" + k);
                            writeAllToPipe(frame[j], IdLength, MeasurementLength);
                        }
                        writeAllToPipe(frame[k], IdLength, MeasurementLength);
                        //actualFrame = j;
                        valid=false;
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

    int readFromPipe(byte[] data, int length)
    {
        int i;
        for (i = 0; i < length; i++)
        {
            try {
                data[i] = ReadFilterInputPort();
            } catch (EndOfStreamException e) {
                e.printStackTrace();
            }
        }
        return length;
    }

    int writeToPipe(byte[] data, int length)
    {
        int i;
        for (i = 0; i < length; i++)
        {
            WriteFilterOutputPort(data[i]);
        }
        return length;
    }

    void writeAllToPipe(StreamFrame frame, int IdLength, int MeasurementLength)
    {
        writeToPipe(frame.idTime, IdLength);
        writeToPipe(frame.measurementTime, MeasurementLength);
        writeToPipe(frame.idAltitude, IdLength);
        writeToPipe(frame.measurementAltitude, MeasurementLength);
        writeToPipe(frame.idPressure, IdLength);
        writeToPipe(frame.measurementPressure, MeasurementLength);
        writeToPipe(frame.idTemperature, IdLength);
        writeToPipe(frame.measurementTemperature, MeasurementLength);
    }
}


