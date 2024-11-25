package sogong.train.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sogong.train.dto.MemberDTO;
import sogong.train.dto.TrainRouteDTO;

@Entity
@Getter
@Setter

public class TiketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
}
