class CNC {
    WorkingType_CNC workingType = WorkingType_CNC.WAITING;
    int timeStarted = 0;
    int blade;

    CNC(int blade) {
        this.blade = blade;
    }
}
