package com.venediktov.carservice.repositories;

import com.venediktov.carservice.model.Car;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CarRowMapper implements RowMapper<Car> {
    @Override
    public Car mapRow(ResultSet rs, int rowNum) throws SQLException {
        Car car = new Car();
        car.setId(rs.getLong("id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setVinCode(rs.getString("vin_code"));
        car.setCityBased(rs.getString("city_based"));
        car.setProductionYear(rs.getInt("production_year"));
        car.setImageUrl(rs.getString("image_url"));
        car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        car.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return car;
    }
} 