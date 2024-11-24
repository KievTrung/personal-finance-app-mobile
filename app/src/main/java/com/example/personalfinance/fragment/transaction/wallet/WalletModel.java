package com.example.personalfinance.fragment.transaction.wallet;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class WalletModel implements Parcelable {
    private Integer id;
    private String wallet_title;
    private Double wallet_amount;
    private String wallet_description;
    private Boolean current_use;

    public static final Creator<WalletModel> CREATOR = new Creator<WalletModel>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public WalletModel createFromParcel(Parcel parcel) {
            return new WalletModel(parcel);
        }

        @Override
        public WalletModel[] newArray(int i) {
            return new WalletModel[i];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected WalletModel(Parcel in){
        wallet_title = in.readString();
        wallet_amount = in.readDouble();
        wallet_description = in.readString();
        current_use = in.readBoolean();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(wallet_title);
        parcel.writeDouble(wallet_amount);
        parcel.writeString(wallet_description);
        parcel.writeBoolean(current_use);
        parcel.writeInt(id);
    }

    public WalletModel() {
    }

    public String getWallet_title() {
        return wallet_title;
    }

    public void setWallet_title(String wallet_title) {
        this.wallet_title = wallet_title;
    }

    public Double getWallet_amount() {
        return wallet_amount;
    }

    public void setWallet_amount(Double wallet_amount) {
        this.wallet_amount = wallet_amount;
    }

    public String getWallet_description() {
        return wallet_description;
    }

    public void setWallet_description(String wallet_description) {
        this.wallet_description = wallet_description;
    }

    public Boolean getCurrent_use() {
        return current_use;
    }

    public void setCurrent_use(Boolean current_use) {
        this.current_use = current_use;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "WalletModel{" +
                "id=" + id +
                ", wallet_title='" + wallet_title + '\'' +
                ", wallet_amount=" + wallet_amount +
                ", wallet_description='" + wallet_description + '\'' +
                ", current_use=" + current_use +
                '}';
    }
}
