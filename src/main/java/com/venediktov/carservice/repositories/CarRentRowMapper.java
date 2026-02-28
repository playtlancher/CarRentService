package com.venediktov.carservice.repositories;

import com.venediktov.carservice.model.CarRent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CarRentRowMapper implements RowMapper<CarRent> {
    @Override
    public CarRent mapRow(ResultSet rs, int rowNum) throws SQLException {
        CarRent carRent = new CarRent();
        carRent.setId(rs.getLong("id"));
        carRent.setCarId(rs.getLong("car_id"));
        carRent.setCustomerId(rs.getLong("customer_id"));
        carRent.setStartDate(rs.getDate("start_date").toLocalDate());
        carRent.setEndDate(rs.getDate("end_date").toLocalDate());
        carRent.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        carRent.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return carRent;
    }
} 