package com.covoiturage.service;

import com.covoiturage.dto.BookingDTO;
import com.covoiturage.model.Booking;
import com.covoiturage.model.Trip;
import com.covoiturage.model.User;
import com.covoiturage.repository.BookingRepository;
import com.covoiturage.repository.TripRepository;
import com.covoiturage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 创建预订
     */
    @Transactional
    public BookingDTO createBooking(Long passengerId, Long tripId, Integer seatsBooked, String messageToDriver) {
        
        // 1. 验证行程存在
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        // 2. 验证乘客存在
        User passenger = userRepository.findById(passengerId)
            .orElseThrow(() -> new RuntimeException("Passenger not found"));
        
        // 3. 验证不能预订自己的行程
        if (trip.getDriverId().equals(passengerId)) {
            throw new RuntimeException("Vous ne pouvez pas réserver votre propre trajet");
        }
        
        // 4. 验证是否已经预订过
        List<Booking> existingBookings = bookingRepository.findByTripIdAndPassengerId(tripId, passengerId);
        for (Booking existing : existingBookings) {
            if ("pending".equals(existing.getStatus()) || "accepted".equals(existing.getStatus())) {
                throw new RuntimeException("Vous avez déjà une réservation pour ce trajet");
            }
        }
        
        // 4. 验证行程状态
        if (!"active".equals(trip.getStatus())) {
            throw new RuntimeException("Ce trajet n'est plus disponible");
        }
        
        // 5. 验证座位数
        if (seatsBooked == null || seatsBooked < 1) {
            throw new RuntimeException("Nombre de places invalide");
        }
        if (seatsBooked > trip.getAvailableSeats()) {
            throw new RuntimeException("Pas assez de places disponibles. Places restantes: " + trip.getAvailableSeats());
        }
        
        // 6. 创建预订
        Booking booking = new Booking();
        booking.setTripId(tripId);
        booking.setPassengerId(passengerId);
        booking.setSeatsBooked(seatsBooked);
        booking.setMessageToDriver(messageToDriver);
        booking.setStatus("pending");  // 待司机确认
        
        BigDecimal totalPrice = trip.getPricePerSeat().multiply(BigDecimal.valueOf(seatsBooked));
        booking.setTotalPrice(totalPrice);
        
        // 7. 保存预订
        Booking savedBooking = bookingRepository.save(booking);
        
        // 8. 暂时不减少座位，等司机接受后再减少
        // trip.setAvailableSeats(trip.getAvailableSeats() - seatsBooked);
        // tripRepository.save(trip);
        
        // 9. 转换成DTO返回
        BookingDTO dto = new BookingDTO(savedBooking);
        dto.setPassengerName(passenger.getFirstName() + " " + passenger.getLastName());
        dto.setDepartureCity(trip.getDepartureCity());
        dto.setArrivalCity(trip.getArrivalCity());
        dto.setDepartureDatetime(trip.getDepartureDatetime());
        
        return dto;
    }
    
    /**
     * 司机接受预订
     */
    @Transactional
    public BookingDTO acceptBooking(Long bookingId, Long driverId) {
        
        // 1. 查找预订
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // 2. 查找行程
        Trip trip = tripRepository.findById(booking.getTripId())
            .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        // 3. 验证是否是司机本人
        if (!trip.getDriverId().equals(driverId)) {
            throw new RuntimeException("Vous n'êtes pas le conducteur de ce trajet");
        }
        
        // 4. 验证预订状态
        if (!"pending".equals(booking.getStatus())) {
            throw new RuntimeException("Cette réservation a déjà été traitée");
        }
        
        // 5. 验证座位是否足够
        if (booking.getSeatsBooked() > trip.getAvailableSeats()) {
            throw new RuntimeException("Pas assez de places disponibles");
        }
        
        // 6. 更新预订状态
        booking.setStatus("accepted");
        bookingRepository.save(booking);
        
        // 7. 减少可用座位
        trip.setAvailableSeats(trip.getAvailableSeats() - booking.getSeatsBooked());
        tripRepository.save(trip);
        
        // 8. 更新乘客统计
        User passenger = userRepository.findById(booking.getPassengerId()).orElse(null);
        if (passenger != null) {
            passenger.setTotalTripsPassenger(passenger.getTotalTripsPassenger() + 1);
            userRepository.save(passenger);
        }
        
        // 9. 返回DTO
        BookingDTO dto = new BookingDTO(booking);
        if (passenger != null) {
            dto.setPassengerName(passenger.getFirstName() + " " + passenger.getLastName());
        }
        dto.setDepartureCity(trip.getDepartureCity());
        dto.setArrivalCity(trip.getArrivalCity());
        dto.setDepartureDatetime(trip.getDepartureDatetime());
        
        return dto;
    }
    
    /**
     * 司机拒绝预订
     */
    @Transactional
    public BookingDTO rejectBooking(Long bookingId, Long driverId) {
        
        // 1. 查找预订
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // 2. 查找行程
        Trip trip = tripRepository.findById(booking.getTripId())
            .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        // 3. 验证是否是司机本人
        if (!trip.getDriverId().equals(driverId)) {
            throw new RuntimeException("Vous n'êtes pas le conducteur de ce trajet");
        }
        
        // 4. 验证预订状态
        if (!"pending".equals(booking.getStatus())) {
            throw new RuntimeException("Cette réservation a déjà été traitée");
        }
        
        // 5. 更新预订状态
        booking.setStatus("rejected");
        bookingRepository.save(booking);
        
        // 6. 返回DTO
        BookingDTO dto = new BookingDTO(booking);
        User passenger = userRepository.findById(booking.getPassengerId()).orElse(null);
        if (passenger != null) {
            dto.setPassengerName(passenger.getFirstName() + " " + passenger.getLastName());
        }
        dto.setDepartureCity(trip.getDepartureCity());
        dto.setArrivalCity(trip.getArrivalCity());
        dto.setDepartureDatetime(trip.getDepartureDatetime());
        
        return dto;
    }
    
    /**
     * 获取某个行程的所有预订
     */
    public List<BookingDTO> getBookingsByTripId(Long tripId) {
        List<Booking> bookings = bookingRepository.findByTripId(tripId);
        
        Trip trip = tripRepository.findById(tripId).orElse(null);
        
        return bookings.stream()
            .map(booking -> {
                BookingDTO dto = new BookingDTO(booking);
                // 添加乘客信息
                userRepository.findById(booking.getPassengerId()).ifPresent(passenger -> {
                    dto.setPassengerName(passenger.getFirstName() + " " + passenger.getLastName());
                });
                // 添加行程信息
                if (trip != null) {
                    dto.setDepartureCity(trip.getDepartureCity());
                    dto.setArrivalCity(trip.getArrivalCity());
                    dto.setDepartureDatetime(trip.getDepartureDatetime());
                }
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 取消预订 - 乘客可以取消自己的预订，司机可以取消自己行程的预订
     */
    @Transactional
    public BookingDTO cancelBooking(Long bookingId, Long userId) {
        
        // 1. 查找预订
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
        
        // 2. 查找行程
        Trip trip = tripRepository.findById(booking.getTripId())
            .orElseThrow(() -> new RuntimeException("Trajet introuvable"));
        
        // 3. 验证预订状态 - 只有pending或accepted状态的预订可以取消
        if (!"pending".equals(booking.getStatus()) && !"accepted".equals(booking.getStatus())) {
            throw new RuntimeException("Cette réservation ne peut plus être annulée");
        }
        
        // 4. 验证权限 - 乘客可以取消自己的预订，司机可以取消自己行程的预订
        boolean isPassenger = booking.getPassengerId().equals(userId);
        boolean isDriver = trip.getDriverId().equals(userId);
        
        if (!isPassenger && !isDriver) {
            throw new RuntimeException("Vous n'avez pas l'autorisation d'annuler cette réservation");
        }
        
        // 5. 更新预订状态
        String originalStatus = booking.getStatus();
        booking.setStatus("cancelled");
        bookingRepository.save(booking);
        
        // 6. 如果预订已被接受，需要恢复可用座位
        if ("accepted".equals(originalStatus)) {
            trip.setAvailableSeats(trip.getAvailableSeats() + booking.getSeatsBooked());
            tripRepository.save(trip);
        }
        
        // 7. 返回DTO
        BookingDTO dto = new BookingDTO(booking);
        User passenger = userRepository.findById(booking.getPassengerId()).orElse(null);
        if (passenger != null) {
            dto.setPassengerName(passenger.getFirstName() + " " + passenger.getLastName());
        }
        dto.setDepartureCity(trip.getDepartureCity());
        dto.setArrivalCity(trip.getArrivalCity());
        dto.setDepartureDatetime(trip.getDepartureDatetime());
        
        return dto;
    }
    
    /**
     * 获取某个乘客的所有预订
     */
    public List<BookingDTO> getBookingsByPassengerId(Long passengerId) {
        List<Booking> bookings = bookingRepository.findByPassengerId(passengerId);
        
        return bookings.stream()
            .map(booking -> {
                BookingDTO dto = new BookingDTO(booking);
                // 添加行程信息
                tripRepository.findById(booking.getTripId()).ifPresent(trip -> {
                    dto.setDepartureCity(trip.getDepartureCity());
                    dto.setArrivalCity(trip.getArrivalCity());
                    dto.setDepartureDatetime(trip.getDepartureDatetime());
                });
                return dto;
            })
            .collect(Collectors.toList());
    }
}
