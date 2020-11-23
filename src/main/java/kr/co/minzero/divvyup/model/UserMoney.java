package kr.co.minzero.divvyup.model;

public class UserMoney {
    /**
     * 사용자 식별값
     */
    private long userId;
    /**
     * 사용자 잔고
     */
    private long money;

    public UserMoney() {

    }

    public UserMoney(long userId) {
        this.userId = userId;
    }
    public UserMoney(long userId, long money) {
        this.userId = userId;
        this.money = money;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
