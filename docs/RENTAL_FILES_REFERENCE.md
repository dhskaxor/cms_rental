# 렌탈 모듈 파일 참조

개발 시 자주 참조하는 파일 경로입니다.

---

## Java (src/main/java/com/nt/cms/rental/)

### place (장소)
| 파일 | 설명 |
|------|------|
| `place/controller/AdminRentalPlaceController.java` | 장소 CRUD API |
| `place/service/DefaultRentalPlaceService.java` | 장소 비즈니스 로직 |
| `place/mapper/RentalPlaceMapper.java` | 장소 DB 접근 |

### room (룸)
| 파일 | 설명 |
|------|------|
| `room/controller/AdminRentalRoomController.java` | 룸 CRUD API |
| `room/service/DefaultRentalRoomService.java` | 룸 비즈니스 로직 (기본/주말 요금 저장 포함) |
| `room/dto/RentalRoomRequest.java` | basePrice, weekendPrice, weekendApplyTo 포함 |
| `room/dto/RentalRoomResponse.java` | basePrice, weekendPrice, weekendApplyTo 포함 |
| `room/mapper/RentalRoomMapper.java` | 룸 DB 접근 |
| `room/controller/AdminRentalUnavailableSlotController.java` | 대여불가 구간 API |
| `room/service/DefaultRentalUnavailableSlotService.java` | 대여불가 로직 |

### pricing (요금)
| 파일 | 설명 |
|------|------|
| `pricing/controller/AdminRentalPricingController.java` | 기본/주말·공휴/특수 요금 API |
| `pricing/service/DefaultRentalPricingService.java` | 요금 CRUD (특수 요금) |
| `pricing/mapper/RentalPricingMapper.java` | 요금 DB 접근 |

### calendar (달력·휴관)
| 파일 | 설명 |
|------|------|
| `calendar/service/DefaultRentalCalendarService.java` | **요금 계산 핵심** (calculatePrice), 슬롯 가용·요금 집계 |
| `calendar/controller/AdminRentalClosedRuleController.java` | 휴관·공휴일 규칙 API |
| `calendar/service/DefaultRentalClosedRuleService.java` | 휴관 규칙 로직 |
| `calendar/mapper/RentalCalendarMapper.java` | 달력용 조회 |
| `calendar/mapper/RentalClosedRuleMapper.java` | 휴관 규칙 DB |

### reservation (예약)
| 파일 | 설명 |
|------|------|
| `reservation/service/DefaultRentalReservationService.java` | 예약 생성·조회·취소 (totalPrice 계산 연동 필요) |
| `reservation/controller/RentalReservationAdminController.java` | 관리자 예약 API |
| `reservation/controller/RentalReservationPublicController.java` | 사용자 예약 API |

---

## MyBatis 매퍼 XML (src/main/resources/mapper/rental/)

| 파일 | 설명 |
|------|------|
| `RentalPricingMapper.xml` | base, weekend_holiday, special 요금 CRUD |
| `RentalCalendarMapper.xml` | 달력용 휴관/대여불가/요금/예약 조회 |
| `RentalClosedRuleMapper.xml` | 휴관·공휴일 규칙 CRUD |
| `RentalRoomMapper.xml` | 룸 CRUD |
| `RentalPlaceMapper.xml` | 장소 CRUD |
| `RentalReservationMapper.xml` | 예약 CRUD |

---

## 템플릿 (src/main/resources/templates/admin/rental/)

| 파일 | 설명 |
|------|------|
| `places.html` | 장소 목록·등록·수정 |
| `rooms.html` | 룸 목록·등록·수정 (기본/주말 요금, 1시간 고정) |
| `pricing.html` | FullCalendar 요금 달력, 특수 요금·공휴일·휴관일 설정 |
| `unavailable-slots.html` | 룸별 대여불가 구간 |
| `calendar.html` | 관리자 달력 조회 |

---

## 관련 문서

- **RENTAL_MODULE_STRUCTURE.md** — 모듈 전체 구조, DB, API 요약
- **RENTAL_PRICING_LOGIC.md** — 요금 계산 규칙, 적용 우선순위, 추후 개발 체크리스트
