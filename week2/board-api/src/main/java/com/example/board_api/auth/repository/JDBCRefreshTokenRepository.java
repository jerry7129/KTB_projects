package com.example.board_api.auth.repository;
/* ---------------------------------------------------------

   해당 파일은 JPA 없이 순수 JDBC로 구현을 해보다가 한계를 만나
   JPA로 다시 전환하는 과정에서 사용하지 않게 된 파일입니다.
   이대로 지우기 아까워서 일단 레포에 남겨둡니다.

 ----------------------------------------------------------*/
import com.example.board_api.auth.domain.RefreshTokenRepository;
import com.example.board_api.auth.domain.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
//@RequiredArgsConstructor
//public class JDBCRefreshTokenRepository implements RefreshTokenRepository {
public class JDBCRefreshTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RefreshToken> tokenRowMapper = null;
//            (rs, rowNum) ->
//            RefreshToken.builder()
//                    .id(rs.getLong("token_id"))
//                    .userId(rs.getLong("userId"))
//                    .token(rs.getString("token"))
//                    .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
//                    .expiresAt(rs.getTimestamp("created_at").toLocalDateTime())
//                    .build();

    public JDBCRefreshTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    @Override
    public void save(RefreshToken refreshToken) {
        // INSERT 쿼리 작성 (테이블명과 컬럼명은 실제 DB에 맞게 수정 필요)
        String sql = "INSERT INTO refresh_tokens (user_id, token) VALUES (?, ?)";

        // update()는 INSERT, UPDATE, DELETE 쿼리 실행 시 사용합니다.
        jdbcTemplate.update(sql, refreshToken.getUserId(), refreshToken.getToken());
    }

//    @Override
    public Optional<RefreshToken> findByToken(String token) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";

        try {
            // 단건 조회 시 queryForObject 사용
            RefreshToken result = jdbcTemplate.queryForObject(sql, tokenRowMapper, token);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            // 💡 핵심 방어 로직: JdbcTemplate은 결과가 없으면 null이 아니라 예외를 던집니다!
            // 예외를 잡아서 Optional.empty()로 우아하게 반환해 줍니다.
            return Optional.empty();
        }
    }

//    @Override
    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM refresh_tokens WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}
