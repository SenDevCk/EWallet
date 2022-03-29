package com.bih.nic.e_wallet.entity;


import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by NIC2 on 1/8/2018.
 */

public class ConsumerEntity implements Serializable,Comparable<ConsumerEntity>{
    private String id;
    private String name;
    private String accountNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public int compareTo(@NonNull ConsumerEntity o) {
        return getName().compareTo(o.getName());
    }
}
