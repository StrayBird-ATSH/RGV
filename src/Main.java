public class Main {
    static CNC[] cncs;

    public static void main(String[] arg) {


        for (int k = 0; k < 256; k++) {
            int i = k;
            int[] originData = new int[8];
            originData[0] = i / 128;
            i -= (originData[0] * 128);
            originData[1] = i / 64;
            i -= (originData[1] * 64);
            originData[2] = i / 32;
            i -= (originData[2] * 32);
            originData[3] = i / 16;
            i -= (originData[3] * 16);
            originData[4] = i / 8;
            i -= (originData[4] * 8);
            originData[5] = i / 4;
            i -= (originData[5] * 4);
            originData[6] = i / 2;
            i -= (originData[6] * 2);
            originData[7] = i;
            RGV rgv = new RGV();
            cncs = new CNC[8];
            for (i = 0; i < 8; i++)
                cncs[i] = new CNC(originData[i]);
            rgv.go();
            System.out.println(k + "," + RGV.count);
            RGV.count = 0;
            RGV.totalTimeConsumed = 0;
        }


    }

    public static int getResultValue(int[] arg) {
        RGV rgv = new RGV();
        cncs = new CNC[8];
        for (int i = 0; i < 8; i++)
            cncs[i] = new CNC(arg[i]);
        rgv.go();
        return RGV.count;
    }
}
