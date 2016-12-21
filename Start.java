
package Main;

/**
 *
 * @author Niclas Johansson
 */
public class Start {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TrafikLabConnector tg = new TrafikLabConnector();
        tg.getStations("arl");
    }
    
}
