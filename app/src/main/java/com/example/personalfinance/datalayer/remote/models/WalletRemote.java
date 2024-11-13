package com.example.personalfinance.datalayer.remote.models;

public class WalletRemote {
    private Integer walletId;
    private String walletTitle;
    private Double walletAmount;
    private String walletDescription;

    public WalletRemote(){}

    public Integer getWallet_id() {
        return walletId;
    }

    public void setWallet_id(Integer walletId) {
        this.walletId = walletId;
    }

    public String getWallet_title() {
        return walletTitle;
    }

    public void setWallet_title(String walletTitle) {
        this.walletTitle = walletTitle;
    }

    public Double getWallet_amount() {
        return walletAmount;
    }

    public void setWallet_amount(Double walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getWallet_description() {
        return walletDescription;
    }

    public void setWallet_description(String walletDescription) {
        this.walletDescription = walletDescription;
    }


    @Override
    public String toString() {
        return "WalletRemote{" +
                "walletId=" + walletId +
                ", walletTitle='" + walletTitle + '\'' +
                ", walletAmount=" + walletAmount +
                ", walletDescription='" + walletDescription + '\'' +
                '}';
    }
}
