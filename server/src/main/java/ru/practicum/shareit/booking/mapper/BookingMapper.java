package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto == null) {
            return null;
        }

        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
