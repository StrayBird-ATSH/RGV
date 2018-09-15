class RGV {
    private int position = 1;
    private boolean holdingProduct = false;
    private boolean washedRecently = false;
    private static int totalTimeConsumed = 0;
    private static int count = 0;
    private static final int oddLoadTime = 27;
    private static final int evenLoadTime = 32;
    private static final int timeMoveInitiate = 4;
    private static final int timeMovePerUnit = 14;
    private static final int washingTime = 25;
    private static final int processingTimeA = 455;
    private static final int processingTimeB = 182;

    private void moveTo(int destination) {
        int distance = Math.abs(destination - position);
        if (distance > 0)
            position = destination;
    }

    void go() {
        System.out.println(count);
        refreshStatus();
        if (totalTimeConsumed >= (8 * 3600))
            return;
        int destination;
        if (!holdingProduct) {
            /*
             * At the start.
             * */
            destination = requestVacant();
            if (destination > 0) {
                System.out.println("UP," + destination + "," +
                        (totalTimeConsumed - ((destination % 2) != 0 ? oddLoadTime : evenLoadTime)));
                moveTo((destination + 1) / 2);
                go();
                return;
            }


            /*
             *
             * After a CNC-A has finished
             * */
            destination = requestDoneA();
            if (destination > 0) {
                int timeOfLoad = (totalTimeConsumed -
                        ((destination % 2) != 0 ? oddLoadTime : evenLoadTime));
                System.out.println("A,DOWN," + destination + "," + timeOfLoad);
                System.out.println("A,UP," + destination + "," + timeOfLoad);
                moveTo((destination + 1) / 2);
                go();
                return;
            }
        } else {
            destination = requestDoneB();
            if (destination > 0) {
                int timeOfLoad = (totalTimeConsumed -
                        ((destination % 2) != 0 ? oddLoadTime : evenLoadTime)) -
                        (washedRecently ? washingTime : 0);
                System.out.println("B,DOWN," + destination + "," + timeOfLoad);
                System.out.println("B,UP," + destination + "," + timeOfLoad);
                moveTo((destination + 1) / 2);
                go();
                return;
            }
        }
        int earliestFinish = 8 * 3600;
        for (int i = 0; i < 8; i++) {
            int bladeZero = Main.cncs[i].blade;
            int timeFinish = Main.cncs[i].timeStarted +
                    (bladeZero > 0 ? processingTimeB : processingTimeA);
            if (timeFinish < earliestFinish
                    && (Main.cncs[i].workingType == WorkingType_CNC.WORKINGA ||
                    Main.cncs[i].workingType == WorkingType_CNC.WORKINGB))
                earliestFinish = timeFinish;
        }
        totalTimeConsumed = earliestFinish;
        go();
    }

    private int requestVacant() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;


        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.WAITINGA) {
                int distance = Math.abs((i / 2) + 1 - position);
                currentTime =
                        ((distance > 0 ? timeMoveInitiate : 0) + distance * timeMovePerUnit) +
                                ((i % 2) == 0 ? oddLoadTime : evenLoadTime);
                if ((currentTime < timeToBeConsumed)) {
                    selectedCNC = i;
                    timeToBeConsumed = currentTime;
                }
            }

        if (timeToBeConsumed == 10000)
            return 0;
        totalTimeConsumed += timeToBeConsumed;
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKINGA;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        return selectedCNC + 1;
    }

    private int requestDoneA() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.DONEA) {
                int distance = Math.abs((i / 2) + 1 - position);
                currentTime =
                        ((distance > 0 ? timeMoveInitiate : 0) + distance * timeMovePerUnit) +
                                ((i % 2) == 0 ? oddLoadTime : evenLoadTime);
                if ((currentTime < timeToBeConsumed)) {
                    selectedCNC = i;
                    timeToBeConsumed = currentTime;
                }
            }
        if (timeToBeConsumed == 10000)
            return 0;
        holdingProduct = true;
        totalTimeConsumed += timeToBeConsumed;
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKINGA;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        return selectedCNC + 1;
    }


    private int requestDoneB() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.DONEB ||
                    (Main.cncs[i].workingType == WorkingType_CNC.WAITINGB)) {
                int distance = Math.abs((i / 2) + 1 - position);
                currentTime =
                        ((distance > 0 ? timeMoveInitiate : 0) + distance * timeMovePerUnit) +
                                ((i % 2) == 0 ? oddLoadTime : evenLoadTime);
                if ((currentTime < timeToBeConsumed)) {
                    selectedCNC = i;
                    timeToBeConsumed = currentTime;
                }
            }
        if (timeToBeConsumed == 10000)
            return 0;
        holdingProduct = false;
        count++;
        totalTimeConsumed += timeToBeConsumed;
        if (Main.cncs[selectedCNC].workingType == WorkingType_CNC.WAITINGB)
            washedRecently = false;
        else {
            washedRecently = true;
            totalTimeConsumed += washingTime;
        }
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKINGB;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        return selectedCNC + 1;
    }

    private static void refreshStatus() {
        for (int i = 0; i < 8; i++)
            if ((totalTimeConsumed - Main.cncs[i].timeStarted) >= processingTimeA
                    && Main.cncs[i].blade == 0)
                Main.cncs[i].workingType = WorkingType_CNC.DONEA;
            else if ((totalTimeConsumed - Main.cncs[i].timeStarted) >= processingTimeB
                    && Main.cncs[i].blade == 1)
                Main.cncs[i].workingType = WorkingType_CNC.DONEB;
    }
}
