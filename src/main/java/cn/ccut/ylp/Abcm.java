package cn.ccut.ylp;

public class Abcm {
    public String datetime;
    //交易日期
    public String date;
    //交易时间
    public String time;
    public String amount;
    public String balance;
    // 对方户名
    public String counterpartyName;
    //对方账号
    public String counterpartyAccount;
    public String tradingBank;
    public String tradingChannels;
    public String transactionType;
    // 交易用途
    public String transactionPurpose;
    // 交易摘要
    public String tradingSummary;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getCounterpartyAccount() {
        return counterpartyAccount;
    }

    public void setCounterpartyAccount(String counterpartyAccount) {
        this.counterpartyAccount = counterpartyAccount;
    }

    public String getTradingBank() {
        return tradingBank;
    }

    public void setTradingBank(String tradingBank) {
        this.tradingBank = tradingBank;
    }

    public String getTradingChannels() {
        return tradingChannels;
    }

    public void setTradingChannels(String tradingChannels) {
        this.tradingChannels = tradingChannels;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionPurpose() {
        return transactionPurpose;
    }

    public void setTransactionPurpose(String transactionPurpose) {
        this.transactionPurpose = transactionPurpose;
    }

    public String getTradingSummary() {
        return tradingSummary;
    }

    public void setTradingSummary(String tradingSummary) {
        this.tradingSummary = tradingSummary;
    }
    //    Trading bank Trading channels Transaction Type
//    Transaction purpose Trading Summary
}
