package ua.training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.training.api.dto.DestinationDto;
import ua.training.api.mapper.DestinationMapper;
import ua.training.domain.order.Destination;
import ua.training.exception.DestinationNotFoundException;
import ua.training.repository.DestinationRepository;
import ua.training.service.DestinationService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DestinationServiceImpl implements DestinationService {

    private final DestinationRepository destinationRepository;
    private final DestinationMapper destinationMapper;

    public DestinationServiceImpl(DestinationRepository destinationRepository, DestinationMapper destinationMapper) {
        this.destinationRepository = destinationRepository;
        this.destinationMapper = destinationMapper;
    }

    @Override
    public List<DestinationDto> getAllDestinationDto() {

        return destinationRepository.findAll().stream()
                .map(destinationMapper::destinationToDestinationDto)
                .collect(Collectors.toList());
    }


    @Override
    public Destination getDestination(String cityFrom, String cityTo) throws DestinationNotFoundException {

        return destinationRepository
                .findByCityFromAndCityTo(cityFrom, cityTo)
                .orElseThrow(() -> new DestinationNotFoundException("no destination : " + cityFrom + " - " + cityTo));
    }
}
