public class RGV {
    private int position = 1;
    private WorkingType_RGV workingType = WorkingType_RGV.WAITING;
    int timeRemaining = 0;
    static int totalTimeConsumed = 0;
    private static final int timeMoveInitiate = 0;
    private static final int timeMovePerUnit = 0;

    void moveTo(int destination) {
        int gap = Math.abs(destination - position);
        if (gap > 0 && workingType == WorkingType_RGV.WAITING) {
            totalTimeConsumed += (timeMoveInitiate + gap * timeMovePerUnit);
            position = destination;
        }
    }

    void go() {
        if (totalTimeConsumed >= (8 * 3600)) {
            System.out.println(totalTimeConsumed + ";" + Product.count);
            return;
        }

    }

    private int requestVacant() {
        int selectedCNC = 0;
        int gap = 4;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.WAITING
                    && (Math.abs(position - (i / 2)) < gap)) {
                selectedCNC = i;
                gap = Math.abs(position - (i / 2));
            }
        if (gap == 4)
            return 0;
        return selectedCNC + 1;
    }
}
