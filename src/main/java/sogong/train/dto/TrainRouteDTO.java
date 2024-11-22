package sogong.train.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sogong.train.entity.TrainRouteEntity;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class TrainRouteDTO {
    private Long id;

    private Integer price;
    private String departure;
    private String arrival;
    private String departuredate;
    private String arrivaldate;
    private LocalTime departuretime;
    private LocalTime arrivaltime;

    public static TrainRouteDTO toTrainRouteDTO(TrainRouteEntity trainRouteEntity){
        TrainRouteDTO trainRouteDTO = new TrainRouteDTO();
        trainRouteDTO.setId(trainRouteEntity.getId());
        trainRouteDTO.setPrice(trainRouteEntity.getPrice());
        trainRouteDTO.setDeparture(trainRouteEntity.getDeparture());
        trainRouteDTO.setArrival(trainRouteEntity.getArrival());
        trainRouteDTO.setDeparturedate(trainRouteEntity.getDeparturedate());
        trainRouteDTO.setArrivaldate(trainRouteEntity.getArrivaldate());
        trainRouteDTO.setDeparturetime(trainRouteEntity.getDeparturetime());
        trainRouteDTO.setArrivaltime(trainRouteEntity.getArrivaltime());
        return trainRouteDTO;
    }


}
