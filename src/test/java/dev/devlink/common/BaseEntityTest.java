package dev.devlink.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BaseEntityTest {

    static class TestEntity extends BaseEntity {
        public TestEntity() {}
    }

    @Test
    @DisplayName("ID 설정이 정상적으로 작동한다")
    void setIdWorks() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        Long expectedId = 1L;

        // when
        setField(entity, "id", expectedId);

        // then
        assertThat(entity.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("생성 시간 설정이 정상적으로 작동한다")
    void setCreatedAtWorks() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        LocalDateTime now = LocalDateTime.now();

        // when
        setField(entity, "createdAt", now);

        // then
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("수정 시간 설정이 정상적으로 작동한다")
    void setUpdatedAtWorks() throws Exception {
        // given
        TestEntity entity = new TestEntity();
        LocalDateTime now = LocalDateTime.now();

        // when
        setField(entity, "updatedAt", now);

        // then
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("BaseEntity의 모든 필드가 올바르게 선언되어 있다")
    void baseEntityFieldsAreDeclared() {
        // given
        TestEntity entity = new TestEntity();

        // when & then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = BaseEntity.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
