/**
 * Integração de Sistemas
 * Pedro Filipe Dinis Stamm de Matos, 2009116927
 */
public class SystemC {
    public static void main( String argv[]) {

        /****************************************************************************
         * Here we instantiate the filters.
         ****************************************************************************/

        SourceFilter sourceA = new SourceFilter("SubSetA.dat");
        SourceFilter sourceB = new SourceFilter("SubSetB.dat");
        JoinFilter joinFilter = new JoinFilter();
        SinkFileFilter sink = new SinkFileFilter("OutputC.dat");

        /****************************************************************************
         * Here we connect the filters
         ****************************************************************************/

        joinFilter.Connect(sourceA, sourceB);
        sink.Connect(joinFilter);
        /****************************************************************************
         * Here we start the filters up. All-in-all,... its really kind of boring.
         ****************************************************************************/

        sourceA.start();
        sourceB.start();
        joinFilter.start();
        sink.start();

    }
}
