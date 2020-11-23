package kr.co.minzero.divvyup.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DivvyupMaster {

    private String token;
    private String roomId;
    private long userId;
    private long totalMoney;
    private int count;
    private Date createDate;

    private String resultCode;
    private String resultMessage;

    private List<DivvyupTake> takeList = new ArrayList<>();

    public DivvyupMaster() {
    }

    public DivvyupMaster(String token, String roomId) {
        this.token = token;
        this.roomId = roomId;
    }

    public DivvyupMaster(String token, String roomId, long userId, long money, int count) {
        this.token = token;
        this.roomId = roomId;
        this.userId = userId;
        this.totalMoney = money;
        this.count = count;
        this.createDate = new Date();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }


    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<DivvyupTake> getTakeList() {
        return takeList;
    }

    public void setTakeList(List<DivvyupTake> takeList) {
        this.takeList = takeList;
    }

    public void addTakeList(DivvyupTake take) {
        this.takeList.add(take);
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
