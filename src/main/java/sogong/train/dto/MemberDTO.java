package sogong.train.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sogong.train.entity.MemberEntity;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDTO {
    private Long id;
    private String email;
    private String name;
    private String password;
    private String phone;
    private String role;

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setPassword(memberEntity.getPassword());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setRole(memberEntity.getRole());
        return memberDTO;
    }
}
