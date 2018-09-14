class RGV {
    private int position = 1;
    private static int totalTimeConsumed = 0;
    private static int count = 0;
    private static final int oddLoadTime = 0;
    private static final int evenLoadTime = 0;
    private static final int timeMoveInitiate = 0;
    private static final int timeMovePerUnit = 0;
    private static final int washingTime = 0;
    private static final int processingTime = 0;

    private void moveTo(int destination) {
        int distance = Math.abs(destination - position);
        if (distance > 0)
            position = destination;
    }

    void go() {
        refreshStatus();
        if (totalTimeConsumed >= (8 * 3600)) {
            System.out.println(totalTimeConsumed + ";" + count);
            return;
        }
        int destination = requestVacant();
        if (destination > 0) {
            moveTo(destination);
            go();
            return;
        }
        destination = requestDone();
        if (destination > 0) {
            moveTo(destination);
            go();
        }
    }

    private int requestVacant() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.WAITING) {
                int distance = Math.abs((i + 1) - position);
                currentTime =
                        (timeMoveInitiate + distance * timeMovePerUnit) +
                                ((i % 2) == 0 ? oddLoadTime : evenLoadTime);
                if ((currentTime < timeToBeConsumed)) {
                    selectedCNC = i;
                    timeToBeConsumed = currentTime;
                }
            }
        if (timeToBeConsumed == 10000)
            return 0;
        totalTimeConsumed += timeToBeConsumed;
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKING;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        return selectedCNC + 1;
    }

    private int requestDone() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.DONE) {
                int distance = Math.abs((i + 1) - position);
                currentTime =
                        (timeMoveInitiate + distance * timeMovePerUnit) +
                                ((i % 2) == 0 ? oddLoadTime : evenLoadTime) +
                                washingTime;
                if ((currentTime < timeToBeConsumed)) {
                    selectedCNC = i;
                    timeToBeConsumed = currentTime;
                }
            }
        if (timeToBeConsumed == 10000)
            return 0;
        count++;
        totalTimeConsumed += timeToBeConsumed;
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKING;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        return selectedCNC + 1;
    }

    private static void refreshStatus() {
        for (int i = 0; i < 8; i++)
            if ((totalTimeConsumed - Main.cncs[i].timeStarted) > processingTime)
                Main.cncs[i].workingType = WorkingType_CNC.DONE;
    }
}
