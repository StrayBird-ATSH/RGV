class RGV {
    private int position = 1;
    private boolean doNotWash = false;
    private static int totalTimeConsumed = 0;
    private static int count = 0;
    private static final int oddLoadTime = 27;
    private static final int evenLoadTime = 32;
    private static final int timeMoveInitiate = 4;
    private static final int timeMovePerUnit = 14;
    private static final int washingTime = 25;
    private static final int processingTime = 545;

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
        int destination = requestVacant();
        if (destination > 0) {
            System.out.println("UP," + destination + "," +
                    (totalTimeConsumed - ((destination % 2) != 0 ? oddLoadTime : evenLoadTime)));
            moveTo((destination + 1) / 2);
            go();
            return;
        }
        destination = requestDone();
        if (destination > 0) {
            int timeOfLoad = (totalTimeConsumed -
                    ((destination % 2) != 0 ? oddLoadTime : evenLoadTime)) - washingTime;
            System.out.println("DOWN," + destination + "," + timeOfLoad);
            System.out.println("UP," + destination + "," + timeOfLoad);
            moveTo((destination + 1) / 2);
            go();
            return;
        }
        int earliestStarted = 8 * 3600;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].timeStarted < earliestStarted
                    && (Main.cncs[i].workingType == WorkingType_CNC.WORKING))
                earliestStarted = Main.cncs[i].timeStarted;
        totalTimeConsumed = earliestStarted + processingTime;
        go();
    }

    private int requestVacant() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.WAITING) {
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
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKING;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        randomFault(Main.cncs[selectedCNC]);
        return selectedCNC + 1;
    }

    private int requestDone() {
        int selectedCNC = 0;
        int timeToBeConsumed = 10000;
        int currentTime;
        boolean allWorking = true;
        int earliestStarted = 8 * 3600;

        if (count >= 9 && !doNotWash) {
            totalTimeConsumed += washingTime;
            refreshStatus();
        }

        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.DONE ||
                    Main.cncs[i].workingType == WorkingType_CNC.RepairFinish)
                allWorking = false;
        if (allWorking) {
            for (int i = 0; i < 8; i++)
                if (Main.cncs[i].timeStarted < earliestStarted
                        && (Main.cncs[i].workingType == WorkingType_CNC.WORKING)) {
                    earliestStarted = Main.cncs[i].timeStarted;
                }
            totalTimeConsumed = earliestStarted + processingTime;
            refreshStatus();
        }
        for (int i = 0; i < 8; i++)
            if (Main.cncs[i].workingType == WorkingType_CNC.DONE ||
                    Main.cncs[i].workingType == WorkingType_CNC.RepairFinish) {
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
        if (Main.cncs[selectedCNC].workingType == WorkingType_CNC.RepairFinish)
            doNotWash = true;
        else
            count++;
        totalTimeConsumed += timeToBeConsumed;
        Main.cncs[selectedCNC].workingType = WorkingType_CNC.WORKING;
        Main.cncs[selectedCNC].timeStarted = totalTimeConsumed;
        randomFault(Main.cncs[selectedCNC]);
        return selectedCNC + 1;
    }

    private void randomFault(CNC cnc) {
        if (Math.random() < 0.01) {
            System.out.println("FAULT!!!");
            cnc.workingType = WorkingType_CNC.FAULT;
            cnc.timeRepairFinish = cnc.timeStarted + (int) (processingTime * Math.random()) +
                    600 + (int) (600 * Math.random());
        }
    }

    private static void refreshStatus() {
        for (int i = 0; i < 8; i++)
            if ((totalTimeConsumed - Main.cncs[i].timeStarted) >= processingTime &&
                    Main.cncs[i].workingType == WorkingType_CNC.WORKING)
                Main.cncs[i].workingType = WorkingType_CNC.DONE;
            else if (totalTimeConsumed >= Main.cncs[i].timeRepairFinish &&
                    Main.cncs[i].workingType == WorkingType_CNC.FAULT)
                Main.cncs[i].workingType = WorkingType_CNC.RepairFinish;
    }
}
