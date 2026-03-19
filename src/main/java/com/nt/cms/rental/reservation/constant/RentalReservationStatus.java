package com.nt.cms.rental.reservation.constant;

/**
 * 렌탈 예약 상태 코드 상수
 */
public final class RentalReservationStatus {

    private RentalReservationStatus() {
    }

    public static final String REQUESTED = "REQUESTED";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED_BY_USER = "CANCELLED_BY_USER";
    public static final String REJECTED_BY_ADMIN = "REJECTED_BY_ADMIN";
    public static final String CANCELLED_BY_ADMIN = "CANCELLED_BY_ADMIN";
}

