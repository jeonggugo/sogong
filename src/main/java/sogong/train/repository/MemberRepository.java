package sogong.train.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sogong.train.entity.MemberEntity;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    //이메일로 회원정보 조회가능.(select * from member_table where email=?
    Optional<MemberEntity>findByEmail(String email);//optional은 null방지

}
