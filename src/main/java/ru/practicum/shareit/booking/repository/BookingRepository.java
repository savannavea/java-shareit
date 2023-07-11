package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, Instant end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, Instant starts, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, Instant starts,
                                                               Instant ends, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, Instant ends, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, Instant starts, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, Instant starts,
                                                                 Instant ends, Sort sort);

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status,
                                                                             Instant now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, Status status,
                                                                               Instant now);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, Status status,
                                                                        Instant now);

    @Query(value = "SELECT * FROM bookings b JOIN items i ON i.id = b.item_id "
            + "WHERE b.item_id = :itemId AND b.ends < :currentTime ORDER BY b.ends ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> findLastBooking(Long itemId, Instant currentTime);

    @Query(value = "SELECT * FROM bookings b JOIN items i ON i.id = b.item_id "
            + "WHERE b.item_id = :itemId AND b.starts > :currentTime AND b.status != 'REJECTED' " +
            "ORDER BY b.starts ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> findNextBooking(Long itemId, Instant currentTime);
}