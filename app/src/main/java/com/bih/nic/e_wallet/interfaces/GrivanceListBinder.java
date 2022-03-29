package com.bih.nic.e_wallet.interfaces;

import com.bih.nic.e_wallet.entity.GrivanceEntity;

import java.util.ArrayList;

public interface GrivanceListBinder {

    void grivanceFound(ArrayList<GrivanceEntity> grivanceEntityArrayList);
    void grivanceNotFound(String response);
}
