/******************************************************************************************************************
* File:SinkFileFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* This class serves as an example for using the SinkFilterTemplate for creating a sink filter. This particular
* filter reads some input from the filter's input port and does the following:
*
*	1) It parses the input stream and "decommutates" the measurement ID
*	2) It parses the input steam for measurments and "decommutates" measurements, storing the bits in a long word.
*
* This filter illustrates how to convert the byte stream data from the upstream filterinto useable data found in
* the stream: namely time (long type) and measurements (double type).
*
*
* Parameters: 	None
*
* Internal Methods: None
*
******************************************************************************************************************/
import java.io.*;
import java.util.*;						// This class is used to interpret time words
import java.text.SimpleDateFormat;		// This class is used to format and write time in a string format.

public class SinkFileFilter extends FilterFramework
{
	private String filename;
	public SinkFileFilter(String filename){
		super();
		this.filename=filename;
	}
	public void run()
    {
		/************************************************************************************
		*	TimeStamp is used to compute time using java.util's Calendar class.
		* 	TimeStampFormat is used to format the time value so that it can be easily printed
		*	to the terminal.
		*************************************************************************************/

		Calendar TimeStamp = Calendar.getInstance();
		SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

		int MeasurementLength = 8;		// This is the length of all measurements (including time) in bytes
		int IdLength = 4;				// This is the length of IDs in the byte stream

		int bytesread = 0;				// This is the number of bytes read from the stream

		long measurement;				// This is the word used to store all measurements - conversions are illustrated.
		int id;							// This is the measurement id

		boolean written = false;

		/*************************************************************
		*	First we announce to the world that we are alive...
		**************************************************************/

		System.out.print( "\n" + this.getName() + "::Sink Reading ");
		File f = new File(filename);
		try {
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			while (true) {
				try {
					/***************************************************************************
					 // We know that the first data coming to this filter is going to be an ID and
					 // that it is IdLength long. So we first decommutate the ID bytes.
					 ****************************************************************************/

					id = readId(IdLength);
					bytesread+=IdLength;

					/****************************************************************************
					 // Here we read measurements. All measurement data is read as a stream of bytes
					 // and stored as a long value. This permits us to do bitwise manipulation that
					 // is neccesary to convert the byte stream into data words. Note that bitwise
					 // manipulation is not permitted on any kind of floating point types in Java.
					 // If the id = 0 then this is a time value and is therefore a long value - no
					 // problem. However, if the id is something other than 0, then the bits in the
					 // long value is really of type double and we need to convert the value using
					 // Double.longBitsToDouble(long val) to do the conversion which is illustrated.
					 // below.
					 *****************************************************************************/

					measurement = readMeasurement(MeasurementLength);
					bytesread+=IdLength;

					/****************************************************************************
					 // Here we look for an ID of 0 which indicates this is a time measurement.
					 // Every frame begins with an ID of 0, followed by a time stamp which correlates
					 // to the time that each proceeding measurement was recorded. Time is stored
					 // in milliseconds since Epoch. This allows us to use Java's calendar class to
					 // retrieve time and also use text format classes to format the output into
					 // a form humans can read. So this provides great flexibility in terms of
					 // dealing with time arithmetically or for string display purposes. This is
					 // illustrated below.
					 ****************************************************************************/

					if (id == 0) {
						if (written) {
							bw.append("\n");
						} else {
							written = true;
						}
						TimeStamp.setTimeInMillis(measurement);
						bw.append(TimeStampFormat.format(TimeStamp.getTime()) + "\t");
					} // if

					/****************************************************************************
					 // Here we pick up a measurement (ID = 4 in this case), but you can pick up
					 // any measurement you want to. All measurements in the stream are
					 // decommutated by this class. Note that all data measurements are double types
					 // This illustrates how to convert the bits read from the stream into a double
					 // type. Its pretty simple using Double.longBitsToDouble(long value). So here
					 // we print the time stamp and the data associated with the ID we are interested
					 // in.
					 ****************************************************************************/

					else {
						if (id == 1) {
							bw.append("(Speed) ");
						}
						if (id == 2) {
							bw.append("(Altitude) ");
						}
						if (id == 3 || id == 6) {
							bw.append("(Pressure) ");
						}
						if (id == 4) {
							bw.append("(Temperature) ");
						}
						if (id == 5) {
							bw.append("(Pitch) ");
						}
						bw.append( Double.longBitsToDouble(measurement) + "");
						if (id == 6) {
							bw.append("*");
						}
						bw.append("\t");

					} // if

				} // try

				/*******************************************************************************
				 *	The EndOfStreamExeception below is thrown when you reach end of the input
				 *	stream (duh). At this point, the filter ports are closed and a message is
				 *	written letting the user know what is going on.
				 ********************************************************************************/
				catch (EndOfStreamException e) {
					ClosePorts();
					System.out.print("\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesread);
					break;

				} // catch
				catch (IOException e) {
					e.printStackTrace();
				}

			} // while
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	} // run

	public String getFilename() {
		return filename;
	}

} // SingFilter