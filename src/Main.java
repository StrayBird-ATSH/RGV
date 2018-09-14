public class Main {
    static CNC[] cncs;

    public static void main(String[] args) {

        /*
         * Initiate!!!
         * */

        RGV rgv = new RGV();
        cncs = new CNC[8];
        for (int i = 0; i < 8; i++)
            cncs[i] = new CNC();
        rgv.go();
    }
}
