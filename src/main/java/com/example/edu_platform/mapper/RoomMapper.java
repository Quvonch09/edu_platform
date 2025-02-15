package com.example.edu_platform.mapper;

import com.example.edu_platform.entity.Room;
import com.example.edu_platform.payload.RoomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "color", source = "color")
    RoomDTO toDTO(Room room);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "color", source = "color")
    Room toRoom(RoomDTO roomDTO);
}
