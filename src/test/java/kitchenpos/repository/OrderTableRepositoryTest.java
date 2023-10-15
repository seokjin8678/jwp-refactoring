package kitchenpos.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@RepositoryTest
class OrderTableRepositoryTest {

    @Autowired
    OrderTableRepository orderTableRepository;

    @Nested
    class findAllByIdIn {

        @Test
        void 식별자_목록으로_모든_엔티티_조회() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            for (int i = 0; i < 3; i++) {
                OrderTable orderTable = new OrderTable();
                orderTableRepository.save(orderTable);
            }

            // when
            List<OrderTable> actual = orderTableRepository.findAllByIdIn(ids);

            // then
            assertThat(actual).hasSize(3);
        }
    }

    @Nested
    class findAllByTableGroupId {

        @Test
        void 테이블_그룹_식별자로_모든_엔티티_조회() {
            // given
            Long tableGroupId = 4885L;
            for (int i = 0; i < 3; i++) {
                OrderTable orderTable = new OrderTable();
                orderTable.setTableGroupId(tableGroupId);
                orderTableRepository.save(orderTable);
            }

            // when
            List<OrderTable> actual = orderTableRepository.findAllByTableGroupId(tableGroupId);

            // then
            assertThat(actual).hasSize(3);
        }
    }
}
