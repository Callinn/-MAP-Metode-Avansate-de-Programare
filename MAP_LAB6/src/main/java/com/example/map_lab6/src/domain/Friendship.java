package com.example.map_lab6.src.domain;

import java.time.LocalDateTime;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Friendship extends Entity<Long>{

    LocalDateTime date;
    private String status;

    Long idUser1;
    Long idUser2;

    public Friendship(Long idUser1, Long idUser2, LocalDateTime date) {
        this.idUser1 = min(idUser1, idUser2);
        this.idUser2 = max(idUser1, idUser2);
        this.date = date;
        this.status = "PENDING";  // Setăm un status implicit
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getIdUser1() {
        return idUser1;
    }

    public Long getIdUser2() {
        return idUser2;
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}
