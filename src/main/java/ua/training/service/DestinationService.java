package ua.training.service;

import ua.training.api.dto.DestinationDto;
import ua.training.domain.order.Destination;
import ua.training.exception.DestinationNotFoundException;

import java.util.List;

public interface DestinationService {

    List<DestinationDto> getAllDestinationDto();

    Destination getDestination(String cityFrom, String cityTo) throws DestinationNotFoundException;

}
