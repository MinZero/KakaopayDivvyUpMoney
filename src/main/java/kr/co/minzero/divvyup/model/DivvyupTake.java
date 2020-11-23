package kr.co.minzero.divvyup.model;

import java.util.Date;

public class DivvyupTake {
    /**
     * 받는 금액 시퀀스 값
     */
    private int seqNo;
    /**
     * token 값
     */
    private String token;
    /**
     * 대화방 식별값
     */
    private String roomId;
    /**
     *
     */
    private long takeMoney;
    private long takeUserId;
    private Date takeDate;

    private String type;
    private String resultCode;
    private String resultMessage;

    public DivvyupTake() {
    }

    public DivvyupTake(String token, String roomId) {
        this.token = token;
        this.roomId = roomId;
    }
    public DivvyupTake(String token, String roomId, long takeMoney) {
        this.token = token;
        this.takeMoney = takeMoney;
        this.roomId = roomId;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public long getTakeMoney() {
        return takeMoney;
    }

    public void setTakeMoney(long takeMoney) {
        this.takeMoney = takeMoney;
    }

    public long getTakeUserId() {
        return takeUserId;
    }

    public void setTakeUserId(long takeUserId) {
        this.takeUserId = takeUserId;
    }

    public Date getTakeDate() {
        return takeDate;
    }

    public void setTakeDate(Date takeDate) {
        this.takeDate = takeDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
