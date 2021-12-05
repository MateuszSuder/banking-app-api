package banking.application.model;

public class CryptoAccountInfo {
    private int accountLevel;
    private float levelProgress;
    float dollarsTraded;

    public void setAccountLevel(int accountLevel) throws Exception {
        if(accountLevel > 10 || accountLevel < 1) {
            throw new Exception("Only 1-10 values allowed");
        }
        this.accountLevel = accountLevel;
    }

    public void setLevelProgress(float levelProgress) throws Exception {
        if(levelProgress > 100 || levelProgress < 0) {
            throw new Exception("Only 0-100 values allowed");
        }
        this.levelProgress = levelProgress;
    }
}
