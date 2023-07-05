package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime starts, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime starts,
                                                                 LocalDateTime ends, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime ends, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime starts, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime starts,
                                                                   LocalDateTime ends, Sort sort);

    List<Booking> findByBooker_Id(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

}
