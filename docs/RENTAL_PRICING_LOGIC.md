# 렌탈 요금 로직 가이드

사용자 예약, 관리자 예약, 정산 등 여러 곳에서 사용되는 요금 계산의 기준 로직입니다. 신규 기능 개발 시 이 문서를 참고하여 동일한 규칙을 적용하세요.

---

## 1. 요금 체계 개요

### 1.1 기본 원칙

- **단위 시간**: 모든 요금은 **1시간(60분)** 기준입니다.
- **적용 우선순위**: 특수 요금 > 주말·공휴일 요금 > 기본 요금
- **휴관일**: 요금 계산 대상이 아님 (예약 불가)

### 1.2 요금 종류

| 종류 | 저장 위치 | 설명 | 관리 위치 |
|------|----------|------|----------|
| **기본 요금** | `rental_room_pricing_base` | 평일 기본 1시간당 금액 | 룸 설정 |
| **주말·공휴일 요금** | `rental_room_pricing_weekend_holiday` | 주말/공휴일에 적용되는 1시간당 금액 | 룸 설정 |
| **특수 요금** | `rental_room_pricing_special` | 특정 일자·시간대에만 적용되는 1시간당 금액 | 요금 달력 |

---

## 2. DB 구조

### 2.1 rental_room_pricing_base

- **룸당 1건** (uk_rrpb_room unique)
- `room_id`, `unit_minutes`(60 고정), `price`

### 2.2 rental_room_pricing_weekend_holiday

- **룸당 1건** (UPDATE 후 없으면 INSERT)
- `room_id`, `apply_to`, `unit_minutes`(60), `price`
- **apply_to**: `WEEKEND`(주말만), `HOLIDAY`(공휴일만), `BOTH`(주말+공휴일)

### 2.3 rental_room_pricing_special

- **다건** (특정 일자·시간대별)
- `room_id`, `date`, `start_time`, `end_time`, `unit_minutes`(60), `price`
- **일단위 특수 요금**: `start_time=00:00`, `end_time=23:59` → 해당 날 전체에 적용
- **시간대별 특수 요금**: 예) 13:00~15:00 → 해당 시간대만 적용

---

## 3. 요금 계산 로직 (표준)

**소스**: `DefaultRentalCalendarService.calculatePrice()`  
**경로**: `com.nt.cms.rental.calendar.service.DefaultRentalCalendarService`

### 3.1 단일 슬롯(시간대)에 대한 적용 순서

```
1) 특수 요금: 해당 (date, start_time, end_time) 구간에 슬롯이 포함되면 적용
2) 주말·공휴일 요금: apply_to 조건 + 주말/공휴일 여부
   - WEEKEND: 토·일요일만
   - HOLIDAY: 공휴일만 (rental_place_closed_rule의 HOLIDAY 규칙 기준)
   - BOTH: 주말 또는 공휴일
3) 기본 요금: 위 조건에 해당하지 않을 때
```

### 3.2 주말·공휴일 판별

- **주말**: `DayOfWeek.SATURDAY` 또는 `DayOfWeek.SUNDAY`
- **공휴일**: `rental_place_closed_rule`에서 `rule_type='HOLIDAY'`이고 `start_date`가 해당 날짜인 규칙 존재

> ⚠️ 현재 `DefaultRentalCalendarService`의 `calculatePrice`는 `HOLIDAY` apply_to에 대해 `false`를 반환(미적용)합니다.  
> 공휴일 요금 적용을 위해 `rental_place_closed_rule`의 HOLIDAY 규칙과 연동하는 확장이 필요할 수 있습니다.

### 3.3 특수 요금 우선 적용

특수 요금의 `start_time`~`end_time` 구간에 슬롯 전체가 포함되면 해당 가격을 적용합니다.  
여러 특수 요금이 겹치면 **첫 번째 매칭**이 적용됩니다.

### 3.4 총액 계산 (예약 시)

- 슬롯 단위로 `calculatePrice()` 결과를 합산
- 예: 2시간 예약 → 슬롯 2개 가격 합계

```java
// 참고: DefaultRentalReservationService는 현재 totalPrice=0으로 저장
// 추후 CalendarService 연동 후 calculatePrice 결과를 합산해 totalPrice 설정 필요
```

---

## 4. API 및 서비스 진입점

### 4.1 달력/요금 계산

| 역할 | 클래스 | 설명 |
|------|--------|------|
| 달력 일별·슬롯별 가용여부·요금 | `RentalCalendarService` | `getCalendar()` → `List<RentalCalendarDayResponse>` |
| 요금 계산 내부 | `DefaultRentalCalendarService.calculatePrice()` | private 메서드, 캘린더용 |

### 4.2 요금 CRUD (관리자)

| API | 설명 |
|-----|------|
| `GET/PUT /admin/rental/rooms/{roomId}/pricing/base` | 기본 요금 |
| `GET/PUT /admin/rental/rooms/{roomId}/pricing/weekend-holiday` | 주말·공휴일 요금 |
| `GET/POST/PUT/DELETE .../pricing/special` | 특수 요금 |

### 4.3 룸 설정에서 요금 저장

- **DefaultRentalRoomService**: `createRoom`, `updateRoom` 시
  - `basePrice` → `rental_room_pricing_base` upsert
  - `weekendPrice`, `weekendApplyTo` → `rental_room_pricing_weekend_holiday` (UPDATE 후 0건이면 INSERT)

---

## 5. 휴관·공휴일 규칙 (rental_place_closed_rule)

**장소 단위**로 관리됩니다.

| rule_type | 설명 | 필수 필드 |
|-----------|------|----------|
| `WEEKDAY` | 매주 특정 요일 휴관 | week_day (1=월 ~ 7=일) |
| `DATE` | 기간 휴관 | start_date, end_date(선택) |
| `HOLIDAY` | 공휴일 | start_date, holiday_name |

- 휴관일에는 슬롯이 생성되지 않으며, 요금 계산 대상이 아닙니다.
- **관리**: 요금 달력(`pricing.html`)에서 날짜 클릭 → 공휴일 지정 / 휴관일 지정

---

## 6. 추후 개발 시 체크리스트

1. **예약 생성 시 총액 계산**
   - `RentalCalendarService.getCalendar()` 또는 동일 로직으로 슬롯별 요금 조회
   - 예약 구간의 슬롯별 `price` 합산 → `totalPrice` 저장

2. **정산**
   - `rental_reservation`의 `total_price` 또는 별도 정산 테이블 활용
   - 요금 적용 근거(source)가 필요하면 `RentalCalendarSlotResponse.priceSource` 활용

3. **공휴일 요금**
   - `DefaultRentalCalendarService.calculatePrice()`에서 `apply_to=HOLIDAY`일 때
   - `rental_place_closed_rule`의 HOLIDAY 규칙과 해당 날짜 매칭 후 적용

4. **시간대 UTC/로컬**
   - 프론트·API 간 날짜 전달 시 `YYYY-MM-DD`는 로컬 기준으로 통일
   - `toISOString().slice(0,10)` 사용 시 UTC 변환으로 인한 날짜 밀림 주의
