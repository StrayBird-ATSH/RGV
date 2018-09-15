public class Main {
    static CNC[] cncs;

    public static void main(String[] args) {

        /*
         * Initiate!!!
         * */
        int[] blades = {0, 1, 1, 0, 1, 1, 0, 0};
        RGV rgv = new RGV();
        cncs = new CNC[8];
        for (int i = 0; i < 8; i++)
            cncs[i] = new CNC(blades[i]);
        rgv.go();
    }
}
