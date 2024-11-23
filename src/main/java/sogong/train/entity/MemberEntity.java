package sogong.train.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import sogong.train.dto.MemberDTO;

@Entity
@Setter
@Getter
public class MemberEntity {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 지정
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column
    private String role;

    public static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(memberDTO.getId());
        memberEntity.setEmail(memberDTO.getEmail());
        memberEntity.setPassword(memberDTO.getPassword());
        memberEntity.setName(memberDTO.getName());
        memberEntity.setPhone(memberDTO.getPhone());
        memberEntity.setRole("user");
        return memberEntity;
    }
}
