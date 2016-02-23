/******************************************************************************************************************
 * File:Plumber.java
 * Course: 17655
 * Project: Assignment 1
 * Copyright: Copyright (c) 2003 Carnegie Mellon University
 * Versions:
 *	1.0 November 2008 - Sample Pipe and Filter code (ajl).
 *
 * Description:
 *
 * This class serves as an example to illstrate how to use the PlumberTemplate to create a main thread that
 * instantiates and connects a set of filters. This example consists of three filters: a source, a middle filter
 * that acts as a pass-through filter (it does nothing to the data), and a sink filter which illustrates all kinds
 * of useful things that you can do with the input stream of data.
 *
 * Parameters: 		None
 *
 * Internal Methods:	None
 *
 ******************************************************************************************************************/
public class SystemA
{
    public static void main( String argv[])
    {
        /****************************************************************************
         * Here we instantiate three filters.
         ****************************************************************************/

        /*
        SourceFilter Filter1 = new SourceFilter();
        MiddleFilter Filter2 = new MiddleFilter();
        SinkFilter Filter3 = new SinkFilter();
        */

        TemperatureFilter temperatureFilter = new TemperatureFilter();
        AltitudeFilter altitudeFilter = new AltitudeFilter();
        SinkFileFilter sinkFilter = new SinkFileFilter("Output.dat");
        SourceFilter sourceFilter = new SourceFilter("FlightData.dat");
        CleanFilter cleanFilter = new CleanFilter(1);
        CleanFilter cleanFilter2 = new CleanFilter(3);
        CleanFilter cleanFilter3 = new CleanFilter(5);

        /****************************************************************************
         * Here we connect the filters starting with the sink filter (Filter 1) which
         * we connect to Filter2 the middle filter. Then we connect Filter2 to the
         * source filter (Filter3).
         ****************************************************************************/

        /*
        Filter3.Connect(Filter2); // This esstially says, "connect Filter3 input port to Filter2 output port
        Filter2.Connect(Filter1); // This esstially says, "connect Filter2 intput port to Filter1 output port
        */

        cleanFilter.Connect(sourceFilter);
        cleanFilter2.Connect(cleanFilter);
        cleanFilter3.Connect(cleanFilter2);
        temperatureFilter.Connect(cleanFilter3);
        altitudeFilter.Connect(temperatureFilter);
        sinkFilter.Connect(altitudeFilter);

        /****************************************************************************
         * Here we start the filters up. All-in-all,... its really kind of boring.
         ****************************************************************************/

        /*
        Filter1.start();
        Filter2.start();
        Filter3.start();
        */

        sourceFilter.start();
        cleanFilter.start();
        cleanFilter2.start();
        cleanFilter3.start();
        temperatureFilter.start();
        altitudeFilter.start();
        sinkFilter.start();

    } // main

} // Plumber