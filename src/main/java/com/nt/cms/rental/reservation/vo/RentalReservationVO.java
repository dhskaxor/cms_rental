package com.nt.cms.rental.reservation.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RentalReservationVO {

    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String status;
    private Long totalPrice;
    private String memo;
    private String paymentStatus;
    private String paymentId;

    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Boolean deleted;
}

