package com.bih.nic.e_wallet.entity;



import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by NIC2 on 2/23/2018.
 */

public class BookNoEntity implements Serializable,Comparable<BookNoEntity>{
    private String BookNo;
    private String MessageString;
    public String getBookNo() {
        return BookNo;
    }

    public void setBookNo(String bookNo) {
        BookNo = bookNo;
    }

    public String getMessageString() {
        return MessageString;
    }

    public void setMessageString(String messageString) {
        MessageString = messageString;
    }

    @Override
    public int compareTo(@NonNull BookNoEntity o) {
        return getBookNo().compareTo(o.getBookNo());
    }
}
