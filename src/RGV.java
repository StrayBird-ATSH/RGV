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

}
