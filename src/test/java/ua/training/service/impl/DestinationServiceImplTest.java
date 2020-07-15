package ua.training.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.training.api.dto.DestinationDto;
import ua.training.api.mapper.DestinationMapper;
import ua.training.domain.order.Destination;
import ua.training.exception.DestinationNotFoundException;
import ua.training.repository.DestinationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinationServiceImplTest {

    @Mock
    DestinationRepository destinationRepository;

    @Mock
    DestinationMapper destinationMapper;

    @InjectMocks
    DestinationServiceImpl service;

    @Test
    void getAllDestinationDto() {
        List<Destination> destinationList = Arrays.asList(Destination.builder().build(), Destination.builder().build());
        when(destinationRepository.findAll())
                .thenReturn(destinationList);

        when(destinationMapper.destinationToDestinationDto(any(Destination.class)))
                .thenReturn(DestinationDto.builder().build());

        List<DestinationDto> destinationDtoList = destinationList.stream()
                .map(DestinationMapper.INSTANCE::destinationToDestinationDto)
                .collect(Collectors.toList());

        List<DestinationDto> result = service.getAllDestinationDto();

        assertEquals(result.size(), destinationDtoList.size());

        verify(destinationRepository).findAll();
        verify(destinationMapper, times(destinationList.size())).destinationToDestinationDto(any(Destination.class));
    }

    @Test
    void getDestination() throws DestinationNotFoundException {
        String from = "from";
        String to = "to";

        Optional<Destination> destination = Optional.of(Destination.builder().cityFrom(from).cityTo(to).build());

        when(destinationRepository.findByCityFromAndCityTo(anyString(), anyString())).thenReturn(destination);

        Destination result = service.getDestination(from, to);

        assertEquals(result.getCityFrom(), destination.get().getCityFrom());
        assertEquals(result.getCityTo(), destination.get().getCityTo());

        verify(destinationRepository).findByCityFromAndCityTo(anyString(), anyString());
    }

    @Test
    void getDestinationException() throws DestinationNotFoundException {
        when(destinationRepository.findByCityFromAndCityTo(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(DestinationNotFoundException.class,
                () -> {
                    service.getDestination("from", "to");
                });
    }
}
