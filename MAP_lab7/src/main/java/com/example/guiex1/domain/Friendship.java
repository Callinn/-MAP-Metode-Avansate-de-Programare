package com.example.guiex1.domain;

import java.sql.Date;
import java.time.LocalDate;


import static java.lang.Math.max;
import static java.lang.Math.min;

public class Friendship extends Entity<Long>{

    LocalDate date;
 //    String status;

    Long idUser1;
    Long idUser2;

    public Friendship(Long idUser1, Long idUser2, LocalDate date) {
        this.idUser1 = min(idUser1, idUser2);
        this.idUser2 = max(idUser1, idUser2);
        this.date = date;
    }
    public Long getIdUser1() {
        return idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    public LocalDate getDate() {
        return date;
    }
}