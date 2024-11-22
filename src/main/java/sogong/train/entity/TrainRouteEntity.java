package sogong.train.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sogong.train.dto.TrainRouteDTO;

import java.time.LocalTime;
@Entity
@Setter
@Getter
public class TrainRouteEntity {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 지정
    @Column(unique = true, nullable = false)
    private Long id;

    private Integer price;

    private String departure;
    private String arrival;
    private String departuredate;
    private String arrivaldate;
    private LocalTime departuretime;
    private LocalTime arrivaltime;

    public static TrainRouteEntity toTrainRouteEntity(TrainRouteDTO trainRouteDTO) {
        TrainRouteEntity trainRouteEntity = new TrainRouteEntity();
        trainRouteEntity.setId(trainRouteDTO.getId());
        trainRouteEntity.setPrice(trainRouteDTO.getPrice());
        trainRouteEntity.setDeparture(trainRouteDTO.getDeparture());
        trainRouteEntity.setArrival(trainRouteDTO.getArrival());
        trainRouteEntity.setDeparturedate(trainRouteDTO.getDeparturedate());
        trainRouteEntity.setArrivaldate(trainRouteDTO.getArrivaldate());
        trainRouteEntity.setDeparturetime(trainRouteDTO.getDeparturetime());
        trainRouteEntity.setArrivaltime(trainRouteDTO.getArrivaltime());
        return trainRouteEntity;
    }

}
