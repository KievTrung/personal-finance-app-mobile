package com.example.personalfinance.datalayer.local.dataclass;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalfinance.datalayer.local.entity.Transact;
import com.example.personalfinance.datalayer.local.entity.Wallet;

import java.util.List;

public class WalletWithTransacts {
    @Embedded
    public Wallet wallet;

    @Relation(
            parentColumn = "wallet_title",
            entityColumn = "wallet_title"
    )
    public List<Transact> transactList;
}
