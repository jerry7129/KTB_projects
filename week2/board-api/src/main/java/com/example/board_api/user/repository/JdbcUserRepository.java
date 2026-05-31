package com.example.board_api.user.repository;

import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneOffset;
import java.util.Optional;

@Repository
@Qualifier("jdbcRepo")
public class JdbcUserRepository implements UserRepository {
    // DataSource는 DB connection을 획득 할 때 사용하는 객체이다.
    private final DataSource dataSource;
    // spring-boot에서 자동으로 생성된 connection pool을 dataSource로 자동으로 주입된다.(DI)
    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users(password, email, nickname, profileImageURL, role, status, createdAt, updatedAt) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        // DB의 연결 정보를 가지고 일정 시간 동안 DB와 연결할 수 있도록 통로의 역할을 함.
        try (Connection conn = dataSource.getConnection()){
            // 다양한 SQL 구문을 정의 및 바인딩하는 방법 및 실제 DB로 전송하는 방법이 정의된 객체.
            // RETURN_GENERATED_KEYS는 DB에서 AUTO_INCREMENT로 생성한 id 값을 가져오겠다는 말.
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getNickname());
            pstmt.setString(4, user.getProfileImageURL());
            pstmt.setInt(5, user.getRole().getCode());
            pstmt.setInt(6, user.getStatus().getCode());
            pstmt.setObject(7, user.getCreatedAt().atOffset(ZoneOffset.UTC));
            pstmt.setObject(8, user.getUpdatedAt().atOffset(ZoneOffset.UTC));

            // DB에 바인딩 된 sql 전송
            pstmt.executeUpdate();
            // DB에서 생성된 id 저장
            Long generatedId = null;
            // SQL 쿼리 결과를 저장하는 객체.
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getLong(1);
                }
            }
            return User.builder()
                    .id(generatedId)
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .profileImageURL(user.getProfileImageURL())
                    .createdAt(user.getCreatedAt()) // 기존 시간 재사용
                    .updatedAt(user.getUpdatedAt()) // 기존 시간 재사용
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ? ";

        try (Connection cnn = dataSource.getConnection()) {
            PreparedStatement pstmt = cnn.prepareStatement(sql);

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(recordToUserEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? ";

        try (Connection cnn = dataSource.getConnection()) {
            PreparedStatement pstmt = cnn.prepareStatement(sql);

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(recordToUserEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        String sql = "SELECT * FROM users WHERE nickname = ? ";

        try (Connection cnn = dataSource.getConnection()) {
            PreparedStatement pstmt = cnn.prepareStatement(sql);

            pstmt.setString(1, nickname);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(recordToUserEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User recordToUserEntity(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .password(rs.getString("password"))
                .email(rs.getString("email"))
                .nickname(rs.getString("nickname"))
                .profileImageURL(rs.getString("profileImageURL"))
                .role(UserRole.fromCode(rs.getInt("role")))
                .status(UserStatus.fromCode(rs.getInt("status")))
                .createdAt(rs.getTimestamp("createdAt").toInstant())
                .updatedAt(rs.getTimestamp("updatedAt").toInstant())
                .build();
    }
}
