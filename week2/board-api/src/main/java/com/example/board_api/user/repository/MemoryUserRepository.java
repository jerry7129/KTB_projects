package com.example.board_api.user.repository;

import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Qualifier("memoryRepo")
// In-memory 데이터베이스
public class MemoryUserRepository implements UserRepository {
    // key: sequence, value: user로 이뤄진 HashMap을 데이터베이스로 사용
    // 멀티스레에서 race condition을 피하는 atomic type으로 sequence를 설정. DB에서 primary key 역할을 함.
    private static final AtomicLong sequence = new AtomicLong(-1);
    private static final Map<Long, User> memoryDB = new HashMap<>();

    @Override
    public User save(User user) {
        // In-memory DB에 저장 시 id 값을 user 객체에 부여해야 하는데,
        // Entity에서 Setter 사용은 지양해야 하기에
        // java의 reflection을 사용해 강제로 주입해주었다. reflection은 런타임에 클래의 정보에 접근할 수 있게 한다.
        // 아래 코드는
        // user 객체에 id field가 있을 경우 sequence 값을 1 증가시킨 뒤 id 값을 수정한다.
        // 이후 hashMap에 저장한다.
        try {
            Field idField = user.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Long nextSequence = sequence.incrementAndGet();
            idField.set(user, nextSequence);
            memoryDB.put(nextSequence, user);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(memoryDB.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // memoryDB의 값들을 stream 위에 올려둔 뒤,
        // 각 member들의 email을 보면서 찾으려는 email과 같은지 찾음
        // 그 중 제일 먼저 찾은 것을 반환.
        return memoryDB.values().stream()
                .filter(member -> email.equals(member.getEmail()))
                .findAny();
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return memoryDB.values().stream()
                .filter(member -> nickname.equals(member.getEmail()))
                .findAny();
    }
}
