public class Main {
    static CNC[] cncs;

    public static void main(String[] args) {

        /*
         * Initiate!!!
         * */

        RGV rgv = new RGV();
        cncs = new CNC[8];
        rgv.go();
    }
}
