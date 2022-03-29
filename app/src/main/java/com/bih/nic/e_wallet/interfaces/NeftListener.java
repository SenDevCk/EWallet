package com.bih.nic.e_wallet.interfaces;

import com.bih.nic.e_wallet.entity.NeftEntity;

import java.util.ArrayList;

public interface NeftListener {
    public void getNeftList(ArrayList<NeftEntity> neftEntities);
}
