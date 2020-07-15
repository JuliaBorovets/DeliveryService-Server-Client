package ua.training.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ua.training.api.dto.DestinationDto;
import ua.training.domain.order.Destination;

@Mapper(componentModel = "spring")
public interface  DestinationMapper {

    DestinationMapper INSTANCE = Mappers.getMapper(DestinationMapper.class);

    DestinationDto destinationToDestinationDto(Destination destination);
}
