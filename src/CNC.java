class CNC {
    WorkingType_CNC workingType;
    int timeStarted = 0;
    int blade;

    CNC(int blade) {
        this.blade = blade;
        this.workingType = blade==0?WorkingType_CNC.WAITINGA:WorkingType_CNC.WAITINGB;
    }
}
